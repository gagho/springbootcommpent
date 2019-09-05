package com.example.springbootcommpent.resubmit.config;

import com.example.springbootcommpent.annotation.CacheLock;
import com.example.springbootcommpent.resubmit.CacheKeyGenerator;
import com.example.springbootcommpent.resubmit.util.RedisLockHelper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:43
 */
@Aspect
@Configuration
public class LockMethodInterceptor {

    private final RedisLockHelper redisLockHelper;

    private final CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    public LockMethodInterceptor(RedisLockHelper redisLockHelper, CacheKeyGenerator cacheKeyGenerator) {
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.redisLockHelper = redisLockHelper;
    }

    @Around("execution(public * * (..)) && @annotation(com.example.springbootcommpent.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if(StringUtils.isEmpty(lock.prefix())){
            throw new RuntimeException("lockKey不能为空");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(joinPoint);
        String value = UUID.randomUUID().toString();
        try {
            boolean success = redisLockHelper.lock(lockKey, value, lock.expire(), lock.timeUnit());
            if(!success){
                throw new RuntimeException("重复提交");
            }
            try {
                return joinPoint.proceed();
            }catch (Throwable throwable){
                throw new RuntimeException("系统异常");
            }
        }finally {
            redisLockHelper.unlock(lockKey, value);
        }
    }
}
