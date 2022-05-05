package com.github.taj_ny.forgeannotationconfig.typeadapter;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.Utils;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Adds support for arrays of objects.
 */
public class ArrayTypeAdapter<A, B> extends TypeAdapter<A[], B[]> {
    private final TypeAdapter<A, B> componentTypeAdapter;
    private final Class<A> componentTypeA;
    private final Class<B> componentTypeB;

    public ArrayTypeAdapter(Config config, TypeToken type) {
        super(config, type, type.getRawType(), null);
        Class<A> componentType = type.getRawType().getComponentType();
        componentTypeAdapter = config.getTypeAdapter(type.getRawType().getComponentType(), type);
        componentTypeA = componentTypeAdapter == null ? componentType
                : componentTypeAdapter.getTypeA();
        componentTypeB = componentTypeAdapter == null ? (Class<B>) componentType
                : componentTypeAdapter.getTypeB();
    }

    @Override
    public Class getTypeB() {
        return componentTypeAdapter == null ? type.getRawType() : Utils.classToArrayType(componentTypeB);
    }

    @Override
    public A[] toA(B[] values) {
        A[] convertedArray = (A[]) Array.newInstance(componentTypeA, values.length);
        for (int i = 0; i < values.length; i++) {
            convertedArray[i] = componentTypeAdapter == null ? (A) values[i] : componentTypeAdapter.toA(values[i]);
        }
        return convertedArray;
    }

    @Override
    public B[] toB(A[] values) {
        B[] convertedArray = (B[]) Array.newInstance(componentTypeB, values.length);
        for (int i = 0; i < values.length; i++) {
            convertedArray[i] = componentTypeAdapter == null ? (B) values[i] : componentTypeAdapter.toB(values[i]);
        }
        return convertedArray;
    }

    @Override
    public Class getTypeForValidator() {
        return componentTypeA;
    }
}
