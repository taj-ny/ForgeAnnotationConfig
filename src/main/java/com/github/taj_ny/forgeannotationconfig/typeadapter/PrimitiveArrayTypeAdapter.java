package com.github.taj_ny.forgeannotationconfig.typeadapter;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.Utils;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class PrimitiveArrayTypeAdapter<T> extends TypeAdapter<Object, T[]> {
    public PrimitiveArrayTypeAdapter(Config config, TypeToken type) {
        super(config, type, type.getRawType(),
                Utils.primitiveArrayToWrapperArrayType(type.getRawType()));
    }

    @Override
    @SneakyThrows({InvocationTargetException.class, IllegalAccessException.class, NoSuchMethodException.class})
    public Object toA(T[] ts) {
        return MethodUtils.invokeStaticMethod(ArrayUtils.class, "toPrimitive", new Object[] { ts });
    }

    @Override
    @SneakyThrows({InvocationTargetException.class, IllegalAccessException.class, NoSuchMethodException.class})
    public T[] toB(Object o) {
        return (T[]) MethodUtils.invokeStaticMethod(ArrayUtils.class, "toObject", new Object[] { o });
    }

    @Override
    public Class getTypeForValidator() {
        return typeA.getComponentType();
    }
}
