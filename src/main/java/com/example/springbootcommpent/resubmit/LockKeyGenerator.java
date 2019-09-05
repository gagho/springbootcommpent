package com.example.springbootcommpent.resubmit;

import com.example.springbootcommpent.annotation.CacheLock;
import com.example.springbootcommpent.annotation.CacheParam;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:25
 */
public class LockKeyGenerator implements CacheKeyGenerator {

    @Override
    public String getLockKey(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);
        final Object [] args = joinPoint.getArgs();
        final Parameter [] parameters = method.getParameters();
        StringBuilder stringBuilder = new StringBuilder();

        //默认解析方法里面带CacheParam注解的属性，如果没有尝试解析实体对象中的
        for(int i = 0; i < parameters.length; i++){
            final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
            if (annotation == null){
                continue;
            }
            stringBuilder.append(cacheLock.delimiter()).append(args[i]);
        }

        if(StringUtils.isEmpty(stringBuilder)){
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for(int i = 0 ; i < parameterAnnotations.length; i++){
                final Object object = args[i];
                final Field [] fields = object.getClass().getDeclaredFields();
                for(Field field : fields){
                    final CacheParam annotation = field.getAnnotation(CacheParam.class);
                    if(annotation == null){
                        continue;
                    }
                    field.setAccessible(true);
                    stringBuilder.append(cacheLock.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return cacheLock.prefix() + stringBuilder.toString();
    }
}
