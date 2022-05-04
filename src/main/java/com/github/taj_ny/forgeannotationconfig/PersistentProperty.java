package com.github.taj_ny.forgeannotationconfig;

import lombok.SneakyThrows;
import lombok.var;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * A property saved in the configuration file.
 */
public class PersistentProperty {
    private Property property;
    private Class valueType;

    public PersistentProperty(Config config, String fieldName, ConfigProperty fieldAnnotation,
                              ConfigCategory categoryAnnotation, Object defaultValue) {
        var forgeConfig = config.getPersistentConfiguration();
        var category = categoryAnnotation.name();
        var name = fieldAnnotation.name().isEmpty() ? fieldName : fieldAnnotation.name();
        var comment = fieldAnnotation.comment();
        var isListLengthFixed = fieldAnnotation.isListLengthFixed();
        var maxListLength = fieldAnnotation.maxListLength();
        Double minValue = fieldAnnotation.minValue();
        Double maxValue = fieldAnnotation.maxValue();

        valueType = defaultValue.getClass();
        if (valueType.equals(Boolean.class)) {
            property = forgeConfig.get(category, name, (Boolean) defaultValue, comment);
        } else if (valueType.equals(Integer.class)) {
            property = forgeConfig.get(category, name, (Integer) defaultValue, comment, minValue.intValue(),
                    maxValue.intValue());
        } else if (valueType.equals(Double.class)) {
            property = forgeConfig.get(category, name, (Double) defaultValue, comment, minValue, maxValue);
        } else if (valueType.equals(String.class)) {
            property = forgeConfig.get(category, name, (String) defaultValue, comment);
        } else if (valueType.equals(Boolean[].class)) {
            boolean[] defaultValues = ArrayUtils.toPrimitive((Boolean[]) defaultValue);
            property = forgeConfig.get(category, name, defaultValues, comment, isListLengthFixed, maxListLength);
        } else if (valueType.equals(Integer[].class)) {
            int[] defaultValues = ArrayUtils.toPrimitive((Integer[]) defaultValue);
            property = forgeConfig.get(category, name, defaultValues, comment, minValue.intValue(),
                    maxValue.intValue(), isListLengthFixed, maxListLength);
        } else if (valueType.equals(Double[].class)) {
            double[] defaultValues = ArrayUtils.toPrimitive((Double[]) defaultValue);
            property = forgeConfig.get(category, name, defaultValues, comment, minValue, maxValue,
                    isListLengthFixed, maxListLength);
        } else {
            var validationPattern = fieldAnnotation.validateInput() && !fieldAnnotation.validationPattern().isEmpty()
                            ? Pattern.compile(fieldAnnotation.validationPattern()) : null;
            property = forgeConfig.get(category, name, (String[]) defaultValue, comment, isListLengthFixed,
                    maxListLength, validationPattern);
        }
    }

    /**
     * Gets the value of the property.
     * @return The value of a supported type
     */
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class})
    public Object get() {
        Object value;
        if (valueType.equals(Boolean.class)) {
            value = property.getBoolean();
        } else if (valueType.equals(Integer.class)) {
            value = property.getInt();
        } else if (valueType.equals(Double.class)) {
            value = property.getDouble();
        } else if (valueType.equals(String.class)) {
            value = property.getString();
        } else if (valueType.equals(Boolean[].class)) {
            value = property.getBooleanList();
        } else if (valueType.equals(Integer[].class)) {
            value = property.getIntList();
        } else if (valueType.equals(Double[].class)) {
            value = property.getDoubleList();
        } else {
            value = property.getStringList();
        }
        if (value.getClass().isArray() && value.getClass().getComponentType().isPrimitive()) {
            return MethodUtils.invokeStaticMethod(ArrayUtils.class, "toObject", value);
        }
        return value;
    }

    /**
     * Sets the value of the property.
     * @param value The value of a supported type.
     */
    @SneakyThrows
    public void set(Object value) {
        if (valueType.equals(Boolean.class)) {
            property.set((Boolean) value);
        } else if (valueType.equals(Integer.class)) {
            property.set((Integer) value);
        } else if (valueType.equals(Double.class)) {
            property.set((Double) value);
        } else if (valueType.equals(String.class)) {
            property.set((String) value);
        } else if (valueType.equals(Boolean[].class)) {
            property.set((boolean[]) value);
        } else if (valueType.equals(Integer[].class)) {
            property.set((int[]) value);
        } else if (valueType.equals(Double[].class)) {
            property.set((double[]) value);
        } else if (valueType.equals(String[].class)) {
            property.set((String[]) value);
        }
    }

    /**
     * Sets the value of a list property. Converts wrappers into primitives if possible.
     */
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public void set(Object[] value) {
        Object possiblyPrimitiveArray = value;
        try {
            possiblyPrimitiveArray = MethodUtils.invokeStaticMethod(ArrayUtils.class, "toPrimitive",
                    new Object[] { value });
        } catch (NoSuchMethodException e) { }
        set(possiblyPrimitiveArray);
    }
}
