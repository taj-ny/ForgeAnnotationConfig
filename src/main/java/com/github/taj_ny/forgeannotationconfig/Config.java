package com.github.taj_ny.forgeannotationconfig;

import com.github.taj_ny.forgeannotationconfig.element.CategoryConfigElement;
import com.github.taj_ny.forgeannotationconfig.element.ConfigElement;
import com.github.taj_ny.forgeannotationconfig.element.ConfigElementFactory;
import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;
import com.github.taj_ny.forgeannotationconfig.element.property.SingleConfigElement;
import com.github.taj_ny.forgeannotationconfig.exception.IllegalTypeException;
import com.github.taj_ny.forgeannotationconfig.typeadapter.ArrayListTypeAdapter;
import com.github.taj_ny.forgeannotationconfig.typeadapter.TypeAdapter;
import com.github.taj_ny.forgeannotationconfig.validator.IValidator;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.var;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@ConfigCategory(name = "general")
public abstract class Config {
    private static final SimpleDateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy-HHmmss");

    /**
     * Currently does nothing. May be used in the future.
     */
    @Getter
    private int version = 0;

    private final File file;

    /**
     * Forge's persistent configuration, stored in {@link #file}.
     */
    @Getter
    private final Configuration persistentConfiguration;

    private final List<ConfigElement> configElements = new ArrayList<>();
    private final Map<Class, Class<? extends TypeAdapter>> registeredTypeAdapters = new HashMap<Class, Class<?
            extends TypeAdapter>>() {{
                put(List.class, ArrayListTypeAdapter.class);
    }};
    private final Map<Class, IValidator> registeredValidators = new HashMap<>();

    private boolean isInitialized;

    /**
     * @param file The file where configuration will be stored.
     */
    public Config(File file) {
        this.file = file;
        this.persistentConfiguration = new Configuration(file);
        this.persistentConfiguration.load();
    }

    public Config(File file, int version) {
        this(file);
        this.version = version;
    }

    /**
     * Restores default values for all properties.
     */
    public void restoreDefaults() {
        configElements.stream().filter(IConfigElement::isProperty).forEach(IConfigElement::setToDefault);
    }

    /**
     * Loads values from persistent configuration.
     */
    public void load() {
        configElements.forEach(this::load);
    }

    private void load(ConfigElement element) {
        if (element instanceof CategoryConfigElement) {
            element.getChildElements().forEach(e -> load((ConfigElement) e));
        } else if (element instanceof PropertyConfigElement) {
            Object value = ((PropertyConfigElement) element).getPersistentProperty().get();
            if (element.isList()) {
                element.set((Object[]) value);
            } else {
                element.set(value);
            }
        }
    }

    /**
     * Saves values to persistent configuration.
     */
    public void save() {
        configElements.forEach(this::save);
        persistentConfiguration.save();
    }

    private void save(ConfigElement element) {
        if (element instanceof CategoryConfigElement) {
            element.getChildElements().forEach(e -> save((ConfigElement) e));
        } else if (element instanceof PropertyConfigElement) {
            PropertyConfigElement property = (PropertyConfigElement) element;
            if (property.isList()) {
                property.getPersistentProperty().set(element.getList());
            } else {
                property.getPersistentProperty().set(element.get());
            }
        }
    }

    /**
     * Registers a type adapter for the specified type. If a type adapter already exists for the specified type, it
     * will be overwritten.
     * @throws IllegalStateException If the configuration has been initialized.
     */
    public void registerTypeAdapter(Class type, Class<? extends TypeAdapter> typeAdapterClass) throws IllegalStateException {
        if (isInitialized) {
            throw new IllegalStateException("Type adapters cannot be registered after initialization.");
        }

        registeredTypeAdapters.put(type, typeAdapterClass);
    }

    /**
     * Registers a global validator that will be applied to all fields of the specified type T, overridden by
     * {@link ConfigProperty#validator()}. If a validator already exists for the specified type, it will be overwritten.
     * @throws IllegalStateException If the configuration has been initialized.
     */
    public void registerValidator(Class<?> type, IValidator validator) throws IllegalStateException {
        if (isInitialized) {
            throw new IllegalStateException("Validators cannot be registered after initialization.");
        }
        
        registeredValidators.put(type, validator);
    }

    @Nullable
    @SneakyThrows
    public TypeAdapter getTypeAdapter(Class type, TypeToken typeToken) {
        var adapterClass = registeredTypeAdapters.get(type);
        if (adapterClass != null) {
            return TypeAdapter.createInstance(adapterClass, this, typeToken);
        }
        return null;
    }

    /**
     * @return An unmodifiable map of the currently registered global validators.
     */
    public Map<Class, IValidator> getRegisteredValidators() {
        return Collections.unmodifiableMap(registeredValidators);
    }

    /**
     * Registers all properties and categories of the configuration. Properties will hold default values until
     * {@link #load()} is called.
     * @throws IllegalStateException If the configuration has already been initialized.
     */
    protected void initialize() throws IllegalStateException {
        if (isInitialized) {
            throw new IllegalStateException("The configuration has already been initialized.");
        }

        var clazz = getClass();
        var categoryAnnotation = clazz.getDeclaredAnnotation(ConfigCategory.class);
        if (categoryAnnotation == null) {
            // Default annotation
            categoryAnnotation = clazz.getAnnotation(ConfigCategory.class);
        }
        registerCategory(clazz, this, categoryAnnotation);
        isInitialized = true;
    }

    /**
     * Creates a category for the specified class and adds properly annotated fields ({@link ConfigProperty}) and
     * other categories ({@link ConfigCategory}) to it.
     * @param clazz The type of the class to register.
     * @param instance The instance of the class.
     * @return Null if the class is equal to {@link #getClass()},
     * {@link com.github.taj_ny.forgeannotationconfig.element.CategoryConfigElement} otherwise.
     */
    @SneakyThrows
    @Nullable
    protected ConfigElement registerCategory(@NonNull Class clazz, @NonNull Object instance,
                                             ConfigCategory categoryAnnotation) {
        persistentConfiguration.setCategoryComment(categoryAnnotation.name(), categoryAnnotation.comment());
        List<ConfigElement> childElements = new ArrayList<>();
        for (Field field : FieldUtils.getAllFields(clazz)) {
            field.setAccessible(true);

            var fieldCategoryAnnotation = field.getDeclaredAnnotation(ConfigCategory.class);
            if (fieldCategoryAnnotation != null) {
                childElements.add(registerCategory(field.getType(), field.get(instance), fieldCategoryAnnotation));
            }

            ConfigProperty fieldAnnotation = field.getDeclaredAnnotation(ConfigProperty.class);
            if (fieldAnnotation != null) {
                PropertyConfigElement element = ConfigElementFactory.createPropertyElement(this, field,
                        fieldAnnotation, categoryAnnotation, instance);

                childElements.add(element);
            }
        }

        if (clazz.equals(getClass())) {
            // Do not create a category entry for the main category
            configElements.addAll(childElements);
            return null;
        } else {
            return new CategoryConfigElement(this, childElements, categoryAnnotation);
        }
    }

    public List<IConfigElement> getConfigElements() {
        return (List) configElements;
    }

}