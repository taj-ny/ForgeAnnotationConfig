package com.github.taj_ny.forgeannotationconfig.element.property;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigCategory;
import com.github.taj_ny.forgeannotationconfig.ConfigProperty;
import com.github.taj_ny.forgeannotationconfig.Utils;
import com.github.taj_ny.forgeannotationconfig.entry.array.DoubleArrayEntry;
import com.github.taj_ny.forgeannotationconfig.entry.array.IntegerArrayEntry;
import com.github.taj_ny.forgeannotationconfig.entry.array.StringArrayEntry;
import com.github.taj_ny.forgeannotationconfig.typeadapter.TypeAdapter;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A property where the supported type is an array.
 */
public class ArrayConfigElement<T, S> extends PropertyConfigElement<T, S> {
    /**
     * Default array GUI config entries for supported types.
     */
    protected static final Map<Class<?>, Class<? extends GuiEditArrayEntries.IArrayEntry>> defaultArrayGuiEntries
            = new HashMap<Class<?>, Class<? extends GuiEditArrayEntries.IArrayEntry>>() {{
        put(Boolean.class, GuiEditArrayEntries.BooleanEntry.class);
        put(Double.class, DoubleArrayEntry.class);
        put(Integer.class, IntegerArrayEntry.class);
        put(String.class, StringArrayEntry.class);
    }};

    @Getter
    private Class<? extends GuiEditArrayEntries.IArrayEntry> arrayEntryClass;

    @Getter
    private final int maxListLength;
    @Getter
    private final boolean isListLengthFixed;

    public ArrayConfigElement(Config config, Field field, ConfigProperty fieldAnnotation,
                              ConfigCategory categoryAnnotation, Object instance, TypeAdapter typeAdapter,
                              Class<S> supportedType) {
        super(config, field, fieldAnnotation, categoryAnnotation, instance, typeAdapter, supportedType);

        maxListLength = fieldAnnotation.maxListLength();
        isListLengthFixed = fieldAnnotation.isListLengthFixed();

        if (fieldAnnotation.arrayEntryClass() == GuiEditArrayEntries.IArrayEntry.class) {
            arrayEntryClass = defaultArrayGuiEntries.get(supportedType.isArray() ? supportedType.getComponentType() :
                    supportedType);
        } else {
            arrayEntryClass = fieldAnnotation.arrayEntryClass();
        }
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public Object[] getDefaults() {
        return (S[]) supportedDefaultValue;
    }

    @Override
    public boolean isDefault() {
        return Arrays.equals(getList(), (Object[]) supportedDefaultValue);
    }


    @Override
    public S[] getList() {
        return (S[]) get();
    }

    @Override
    public void set(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SneakyThrows(IllegalAccessException.class)
    public void set(Object[] value) {
        value = Utils.castArray(value, supportedType.getComponentType());
        field.set(this.fieldClassInstance, convertToFieldType((S) value));
        this.persistentProperty.set(value);
    }
}
