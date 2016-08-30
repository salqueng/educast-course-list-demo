package com.example.hoon.educastcourselistdemo.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hoon on 2015. 4. 6..
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Include {
    String fields() default "";
    String separator() default ",";
}
