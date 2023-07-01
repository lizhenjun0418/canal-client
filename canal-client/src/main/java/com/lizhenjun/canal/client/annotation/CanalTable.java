package com.lizhenjun.canal.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 功能描述: Table注解
* @Author: lizhenjun
* @Date: 2023/7/1 13:24
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalTable {

    String value() default "";
}
