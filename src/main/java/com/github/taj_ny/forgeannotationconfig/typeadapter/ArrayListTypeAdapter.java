package com.github.taj_ny.forgeannotationconfig.typeadapter;

import com.github.taj_ny.forgeannotationconfig.Config;
import com.github.taj_ny.forgeannotationconfig.Utils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class ArrayListTypeAdapter<A, B> extends TypeAdapter<List<A>, B[]> {
    private final Class<A> listComponentTypeA;
    private final Class<B> listComponentTypeB;
    private final TypeAdapter<A, B> componentTypeAdapter;

    public ArrayListTypeAdapter(Config config, TypeToken type) {
        super(config, type, List.class, null);
        listComponentTypeA = (Class<A>) ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
        componentTypeAdapter = config.getTypeAdapter(listComponentTypeA, type);
        listComponentTypeB = componentTypeAdapter == null ? (Class<B>) listComponentTypeA :
                componentTypeAdapter.getTypeB();
    }

    @Override
    public List<A> toA(B[] bs) {
        List<A> list = new ArrayList<>(bs.length);
        for (int i = 0; i < bs.length; i++) {
            list.add(componentTypeAdapter == null ? (A) bs[i] : componentTypeAdapter.toA(bs[i]));
        }
        return list;
    }

    @Override
    public B[] toB(List<A> as) {
        B[] arr = (B[]) Array.newInstance(listComponentTypeB, as.size());
        for (int i = 0; i < as.size(); i++) {
            A value = as.get(i);
            arr[i] = componentTypeAdapter == null ? (B) value : componentTypeAdapter.toB(value);
        }
        return arr;
    }

    @Override
    public Class getTypeB() {
        return Utils.classToArrayType(listComponentTypeB);
    }

    @Override
    public Class getTypeForValidator() {
        return listComponentTypeA;
    }
}