package com.example.hoon.educastcourselistdemo.utils.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoon on 15. 2. 24..
 */
public class Cloner {

    public static void copyFieldByField(Object src, Object dest) {
        copyFields(src, dest, src.getClass(), new ArrayList<String>());
    }

    public static void copyFieldByField(Object src, Object dest, List<String> excludes) {
        copyFields(src, dest, src.getClass(), excludes);
    }

    private static void copyFields(Object src, Object dest, Class<?> klass, List<String> excludes) {
        Field[] fields = klass.getDeclaredFields();
        for (Field f : fields) {
            if (!excludes.contains(f.getName())) {
                f.setAccessible(true);
                copyFieldValue(src, dest, f);
            }
        }

        klass = klass.getSuperclass();
        if (klass != null) {
            copyFields(src, dest, klass, excludes);
        }
    }

    private static void copyFieldValue(Object src, Object dest, Field f) {
        try {
            Object value = f.get(src);
            f.set(dest, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}