package com.example.hoon.educastcourselistdemo.utils.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hoon on 15. 1. 10..
 */
public class MultipleParameterizedType implements ParameterizedType {

    private final Class<?> mBase;
    private final Class<?>[] mOthers;

    public MultipleParameterizedType(Class<?> base, Class<?>... others) {
        mBase = base;
        mOthers = others;
    }
    public Type[] getActualTypeArguments() {
        return mOthers;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return mBase;
    }
}
