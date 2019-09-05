package com.example.springbootcommpent.annotation;

import java.lang.annotation.*;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:21
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {


    /**
     * 字段名称
     * @return
     */
    String name() default "";
}
