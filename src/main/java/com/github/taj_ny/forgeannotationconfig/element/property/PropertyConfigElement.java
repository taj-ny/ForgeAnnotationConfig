package com.github.taj_ny.forgeannotationconfig.element.property;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigCategory;
import com.github.taj_ny.forgeannotationconfig.ConfigProperty;
import com.github.taj_ny.forgeannotationconfig.PersistentProperty;
import com.github.taj_ny.forgeannotationconfig.element.ConfigElement;
import com.github.taj_ny.forgeannotationconfig.typeadapter.TypeAdapter;
import com.github.taj_ny.forgeannotationconfig.validator.IValidator;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class PropertyConfigElement<T, S> extends ConfigElement {
    protected static final Map<Class<?>, ConfigGuiType> configGuiTypes = new HashMap<Class<?>, ConfigGuiType>() {{
        put(Boolean.class, ConfigGuiType.BOOLEAN);
        put(Integer.class, ConfigGuiType.INTEGER);
        put(Double.class, ConfigGuiType.DOUBLE);
        put(String.class, ConfigGuiType.STRING);
    }};

    /**
     * An instance of the class containing the field.
     */
    protected final Object fieldClassInstance;
    protected final Field field;

    @Getter
    protected Class<S> supportedType;

    @Getter
    protected Class<? extends GuiConfigEntries.IConfigEntry> configEntryClass;

    @Getter
    protected PersistentProperty persistentProperty;

    @Nullable
    @Getter
    public TypeAdapter<T, S> typeAdapter;
    @Nullable
    @Getter
    public IValidator validator;

    @Getter
    protected ConfigGuiType type;

    // Applicable to fields of supported type Integer and Double
    protected final Double minValue;
    protected final Double maxValue;

    @Getter
    protected final String[] validValues;
    @Getter
    protected final Pattern validationPattern;

    /**
     * Can be a collection or an object/primitive array even in non-list properties.
     */
    protected final T defaultValue;
    /**
     * Can be an object array only in list properties.
     */
    protected S supportedDefaultValue;

    @SneakyThrows
    public PropertyConfigElement(Config config, Field field, ConfigProperty fieldAnnotation,
                                 ConfigCategory categoryAnnotation, Object instance, TypeAdapter typeAdapter,
                                 Class<S> supportedType) {
        super(config, fieldAnnotation.name().isEmpty() ? field.getName() : fieldAnnotation.name(),
                fieldAnnotation.languageKey(),
                fieldAnnotation.comment(),
                fieldAnnotation.requireWorldRestart() || categoryAnnotation.requiresWorldRestart(),
                fieldAnnotation.requireMcRestart() || categoryAnnotation.requiresMcRestart(),
                fieldAnnotation.showInGui());

        this.field = field;
        fieldClassInstance = instance;

        this.typeAdapter = typeAdapter;
        this.supportedType = supportedType;

        this.defaultValue = (T) field.get(instance);
        supportedDefaultValue = convertToSupportedType(defaultValue);

        Class supportedComponentType = supportedType.isArray() ? supportedType.getComponentType() : supportedType;
        if (configGuiTypes.containsKey(supportedComponentType)) {
            type = configGuiTypes.get(supportedComponentType);
        } else {
            throw new IllegalArgumentException(String.format("Type %s is not supported. A type adapter is required.",
                    this.supportedType));
        }

        this.persistentProperty = new PersistentProperty(config, field.getName(), fieldAnnotation, categoryAnnotation,
                supportedDefaultValue);

        this.minValue = fieldAnnotation.minValue();
        this.maxValue = fieldAnnotation.maxValue();


        validValues = fieldAnnotation.validValues();

        if (fieldAnnotation.validator() != IValidator.class) {
            validator = fieldAnnotation.validator().newInstance();
        } else if (typeAdapter != null) {
            validator = config.getRegisteredValidators().get(typeAdapter.getTypeForValidator());
        } else {
            validator = config.getRegisteredValidators().get(field.getType());
        }

        this.validationPattern = fieldAnnotation.validationPattern()
                .equals(ConfigProperty.UNSPECIFIED_VALIDATION_PATTERN) ? null
                : Pattern.compile(fieldAnnotation.validationPattern());
    }

    /**
     * Attempts to validate the user input.
     * @return The result of the validation, false if an exception occurred, true if validation is disabled.
     */
    public boolean tryValidateInput(GuiTextField textField) {
        boolean isValid;
        if (validationPattern != null) {
            isValid = validationPattern.matcher(textField.getText()).matches();
            if (!isValid) {
                return false;
            }
        }

        if (validator != null) {
            try {
                return validator.validate(this, textField.getText());
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    protected T convertToFieldType(S value) {
        return typeAdapter == null ? (T) value : typeAdapter.toA(value);
    }

    protected S convertToSupportedType(T value) {
        return typeAdapter == null ? (S) value : typeAdapter.toB(value);
    }

    @Override
    public Number getMinValue() {
        return type == ConfigGuiType.INTEGER ? (Integer) minValue.intValue() : (Number) minValue;
    }

    @Override
    public Number getMaxValue() {
        return type == ConfigGuiType.INTEGER ? (Integer) maxValue.intValue() : (Number) maxValue;
    }

    @Override
    public boolean isDefault() {
        return get().equals(supportedDefaultValue);
    }

    @Override
    @SneakyThrows(IllegalAccessException.class)
    public void setToDefault() {
        // field.set(fieldClassInstance, defaultValue) would cause issues for mutable types
        field.set(fieldClassInstance, convertToFieldType(supportedDefaultValue));
    }

    @Override
    public S getDefault() {
        return supportedDefaultValue;
    }

    @Override
    @SneakyThrows(IllegalAccessException.class)
    public S get() {
        return convertToSupportedType((T) field.get(fieldClassInstance));
    }

    @Override
    @SneakyThrows(IllegalAccessException.class)
    public void set(Object value) {
        T supportedValue = convertToFieldType((S) value);
        field.set(fieldClassInstance, supportedValue);
        this.persistentProperty.set(value);
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public String getQualifiedName() {
        return null;
    }

    @Override
    public List<IConfigElement> getChildElements() {
        return null;
    }
}
