package com.example.springbootcommpent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * redis锁key前缀
     * @return
     */
    String prefix() default "";

    /**
     * 过期秒数，默认5秒
     * @return
     */
    int expire() default 5;

    /**
     * 超时单位，默认为秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 生成key的连接符
     * @return
     */
    String delimiter() default ":";
}
