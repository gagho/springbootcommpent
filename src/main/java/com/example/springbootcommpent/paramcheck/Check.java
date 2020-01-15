package com.example.springbootcommpent.paramcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouliangze
 * @date 2020/1/15 14:23
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {

    // 字段校验规则，格式：字段名+校验规则+冒号+错误信息
    String [] value();
}
