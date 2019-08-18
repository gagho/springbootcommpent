package com.example.springbootcommpent.logcompent;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 给controller的进入和返回打印日志
 * @author zhouliangze
 * @date 2019/8/13 20:40
 */
@Component
@Aspect
public class LogAspectConfiguration {

    private static Logger logger = LoggerFactory.getLogger(LogAspectConfiguration.class);

    @Before("within(com.example.springbootcommpent.controller.*)")
    public void before(JoinPoint joinPoint){
        Object [] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("{}, {}:请求参数：{}", method.getDeclaringClass().getName(), method.getName(), StringUtils.join(args, ";"));
    }

    @AfterReturning(value = "within(com.example.springbootcommpent.controller.*)", returning = "rvt")
    public void after(JoinPoint joinPoint, Object rvt){
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("{},{} : 返回数据 : {}", method.getDeclaringClass().getName(), method.getName(), JSONObject.toJSONString(rvt));
    }


}
