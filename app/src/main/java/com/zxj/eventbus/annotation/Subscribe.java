package com.zxj.eventbus.annotation;

import com.zxj.eventbus.interfaces.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    ThreadMode threadMode() default ThreadMode.MAIN;

}
