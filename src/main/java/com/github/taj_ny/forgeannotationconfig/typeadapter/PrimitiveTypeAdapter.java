package com.github.taj_ny.forgeannotationconfig.typeadapter;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypeAdapter extends TypeAdapter<Object, Object> {
    /**
     * Primitive types and their respective wrappers.
     */
    public static final Map<Class<?>, Class> WRAPPERS = new HashMap<Class<?>, Class>() {{
        put(byte.class, Byte.class);
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(short.class, Short.class);
    }};

    public PrimitiveTypeAdapter(Config config, TypeToken type) {
        super(config, type, type.getRawType(), WRAPPERS.get(type.getRawType()));
    }

    @Override
    public Object toA(Object o) {
        return o;
    }

    @Override
    public Object toB(Object o) {
        return o;
    }

    @Override
    public Class getTypeForValidator() {
        return typeB;
    }
}
