package com.github.taj_ny.forgeannotationconfig.element;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.ConfigCategory;
import com.github.taj_ny.forgeannotationconfig.ConfigProperty;
import com.github.taj_ny.forgeannotationconfig.element.property.ArrayConfigElement;
import com.github.taj_ny.forgeannotationconfig.element.property.PropertyConfigElement;
import com.github.taj_ny.forgeannotationconfig.element.property.SingleConfigElement;
import com.github.taj_ny.forgeannotationconfig.typeadapter.ArrayTypeAdapter;
import com.github.taj_ny.forgeannotationconfig.typeadapter.PrimitiveArrayTypeAdapter;
import com.github.taj_ny.forgeannotationconfig.typeadapter.PrimitiveTypeAdapter;
import com.github.taj_ny.forgeannotationconfig.typeadapter.TypeAdapter;
import com.github.taj_ny.forgeannotationconfig.util.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class ConfigElementFactory {
    public static PropertyConfigElement createPropertyElement(Config config, Field field,
                                                              ConfigProperty fieldAnnotation,
                                                              ConfigCategory categoryAnnotation, Object instance) {
        // It would be easier to just use the field instead of a TypeToken, but in the future I want to implement a
        // method for manually reading values from persistent configuration, and in that case it won't always be
        // possible to get the type from a field, because it may not exist anymore.
        TypeToken typeToken = field.getGenericType() instanceof ParameterizedType // is field type generic?
                ? TypeToken.getParameterized(field.getType(), ((ParameterizedType) field.getGenericType())
                .getActualTypeArguments())
                : TypeToken.get(field.getType());

        TypeAdapter adapter = config.getTypeAdapter(field.getType(), typeToken);
        if (adapter == null && field.getType().isArray()) {
            if (field.getType().getComponentType().isPrimitive()) {
                adapter = TypeAdapter.createInstance(PrimitiveArrayTypeAdapter.class, config, typeToken);
            } else {
                adapter = TypeAdapter.createInstance(ArrayTypeAdapter.class, config, typeToken);
            }
        } else if (field.getType().isPrimitive()) {
            adapter = TypeAdapter.createInstance(PrimitiveTypeAdapter.class, config, typeToken);
        }

        Class supportedType = adapter == null ? field.getType() : adapter.getTypeB();
        if (supportedType.isArray()) {
            return new ArrayConfigElement(config, field, fieldAnnotation, categoryAnnotation, instance, adapter,
                    supportedType);
        } else {
            return new SingleConfigElement(config, field, fieldAnnotation, categoryAnnotation, instance, adapter,
                    supportedType);
        }
    }
}
