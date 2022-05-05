package com.github.taj_ny.forgeannotationconfig;

import com.github.taj_ny.forgeannotationconfig.typeadapter.PrimitiveTypeAdapter;

import java.lang.reflect.Array;

public class Utils {
    /**
     * Converts a class to an array class. For example, boolean to boolean[].
     */
    public static <T> Class<T[]> classToArrayType(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }

    /**
     * Converts a primitive array class to a wrapper array class For example, boolean[] to Boolean[].
     */
    public static Class primitiveArrayToWrapperArrayType(Class arr) {
        return classToArrayType(PrimitiveTypeAdapter.WRAPPERS.get(arr.getComponentType()));
    }

    /**
     * Casts all values in an array to the specified type.
     * @throws ClassCastException If a value in the array couldn't be cast to the specified type.
     * @return A new array of the specified type containing all the cast values.
     */
    public static Object[] castArray(Object[] arr, Class<?> type) {
        Object[] castedArray = (Object[]) Array.newInstance(type, arr.length);
        for (int i = 0; i < arr.length; i++) {
            castedArray[i] = type.cast(arr[i]);
        }
        return castedArray;
    }
}
