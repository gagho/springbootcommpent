package com.example.springbootcommpent.resubmit;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:24
 */
public interface CacheKeyGenerator {

    /**
     * 获取aop参数，生成指定缓存key
     * @param joinPoint
     * @return
     */
    String getLockKey(ProceedingJoinPoint joinPoint);
}
