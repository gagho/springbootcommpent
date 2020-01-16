package com.example.springbootcommpent.paramcheck;

import cn.hutool.core.collection.CollectionUtil;
import com.example.springbootcommpent.common.BaseResult;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * @author zhouliangze
 * @date 2020/1/15 14:31
 */
@Aspect
@Component
public class ParamCheckAspect {

    private static final Logger logger = LoggerFactory.getLogger(ParamCheckAspect.class);

    private static final String SEPARATOR = ":";

    /**
     * 设置切面
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(public * * (..)) && @annotation(com.example.springbootcommpent.paramcheck.Check)")
    public Object check(ProceedingJoinPoint point) throws Throwable {
        Object object;
        String msg = doCheck(point);
        logger.info("msg {}", msg);
        if (StringUtils.isNotEmpty(msg)) {
            return new BaseResult<>(1, msg);
        }
        object = point.proceed();
        return object;
    }

    /**
     * 参数校验
     *
     * @param point
     * @return
     */
    private String doCheck(ProceedingJoinPoint point) {

        //获取方法参数值
        Object[] arguments = point.getArgs();
        //获取方法
        Method method = getMethod(point);
        //获取默认的错误信息
        String methodInfo = StringUtils.isEmpty(method.getName()) ? "" : "参数不满足条件：";
        String msg = "";
        if(isCheck(method, arguments)){
            Check check = method.getAnnotation(Check.class);
            String [] fields = check.value();
            for(Object vo : arguments){
                if(vo == null){
                    msg = "未传入参数";
                }else {
                    String [] voClazzName = vo.getClass().getName().split("\\.");
                    String voName = voClazzName[voClazzName.length-1];
                    for(String field : fields){
                        FileInfo fileInfo = resolveField(field, methodInfo);
                        String fileStr = fileInfo.field;
                        if(fileStr.split("\\.").length == 2){
                            if(!fileStr.split("\\.")[0].equals(voName)){
                                continue;
                            }
                            fileInfo.field = fileStr.split("\\.")[1];
                        }
                        Object value = null;
                        try {
                            value = ReflectionUtil.invokeGetter(vo, fileInfo.field);
                        }catch (IllegalArgumentException e){
                            continue;
                        }
                        Boolean isValid = fileInfo.optEnum.fun.apply(value, fileInfo.operatorNum);
                        msg = isValid ? msg : fileInfo.errMsg;
                    }
                }
            }
        }
        return msg;

    }

    /**
     * 解析字段
     * @param fieldStr
     * @param methodInfo
     * @return
     */
    private FileInfo resolveField(String fieldStr, String methodInfo){
        FileInfo fileInfo = new FileInfo();
        String errMsg = "";
        if(fieldStr.contains(SEPARATOR)){
            errMsg = fieldStr.split(SEPARATOR)[1];
            fieldStr = fieldStr.split(SEPARATOR)[0];
        }

        //解析字段
        if(fieldStr.contains(Operator.GREATER_THAN.value)){
            fileInfo.optEnum = Operator.GREATER_THAN;
        }else if(fieldStr.contains(Operator.GREATER_THAN_EQUAL.value)){
            fileInfo.optEnum = Operator.GREATER_THAN_EQUAL;
        }else if(fieldStr.contains(Operator.LESS_THAN.value)){
            fileInfo.optEnum = Operator.LESS_THAN;
        }else if(fieldStr.contains(Operator.LESS_THAN_EQUAL.value)){
            fileInfo.optEnum = Operator.LESS_THAN_EQUAL;
        }else if(fieldStr.contains(Operator.NOT_EQUAL.value)){
            fileInfo.optEnum = Operator.NOT_EQUAL;
        }else if(fieldStr.contains(Operator.NOT_NULL.value)){
            fileInfo.optEnum = Operator.NOT_NULL;
        }else if(fieldStr.contains(Operator.PATTERN.value)){
            fileInfo.optEnum = Operator.PATTERN;
        }

        if(fieldStr.contains(Operator.NOT_NULL.value)){
            fileInfo.field = fieldStr.split(" ")[0];
            fileInfo.operatorNum = "";
        }else if(fieldStr.contains(Operator.PATTERN.value)){
            fileInfo.field = fieldStr.split(" " + Operator.PATTERN.value + " ")[0];
            fileInfo.operatorNum = fieldStr.split(" " + Operator.PATTERN.value + " ")[1];
        }else {
            fileInfo.field = fieldStr.split(fileInfo.optEnum.value)[0];
            fileInfo.operatorNum = fieldStr.split(fileInfo.optEnum.value)[1];
        }
        fileInfo.operator = fileInfo.optEnum.value;
        // 处理错误信息
        String defaultMsg;
        if(Operator.NOT_NULL.value.equals(fileInfo.operator)){
            defaultMsg = fileInfo.field + "不能为空";
        }else if(Operator.PATTERN.value.equals(fileInfo.operator)){
            defaultMsg = fileInfo.field + "不满足条件";
        }else {
            defaultMsg = methodInfo + fileInfo.field + " " + fileInfo.operator + " " + fileInfo.operatorNum;
        }
        fileInfo.errMsg = StringUtils.isEmpty(errMsg) ? defaultMsg : errMsg;
        return fileInfo;
    }

    /**
     * 获取方法
     * @param point
     * @return
     */
    private Method getMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = point.getTarget().getClass().getDeclaredMethod(point.getSignature().getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                logger.error("方法解析异常", e);
            }
        }
        return method;
    }

    private Boolean isCheck(Method method, Object [] arguments){
        Boolean isCheck = true;
        //校验是否有注解，校验是否有参数，以及参数是否为一个
        if(!method.isAnnotationPresent(Check.class) || arguments == null){
            isCheck = false;
        }
        return isCheck;
    }

    //--------------------------------------------------------------------------------------------
    //
    //内部类及内部枚举
    //校验规则

    /**
     * 不为空
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isNotNull(Object value, String operatorNum){
        Boolean isNotNull = true;
        Boolean isStringNull = (value instanceof String) && StringUtils.isEmpty((String)value);
        Boolean isCollectionNull = (value instanceof Collection) && CollectionUtil.isEmpty((Collection<?>) value);
        if(value == null){
            isNotNull = false;
        }else {
            if(isStringNull || isCollectionNull){
                isNotNull = false;
            }
        }
        return isNotNull;
    }

    /**
     * 大于
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isGreaterThan(Object value, String operatorNum){
        Boolean isGreaterThan = false;
        if(value == null){
            return false;
        }
        Boolean isStringGreaterThen = (value instanceof String) && ((String)value).length() > Integer.parseInt(operatorNum);
        Boolean isLongGreaterThen = (value instanceof Long) && ((Long) value) > Long.valueOf(operatorNum);
        Boolean isIntegerGreaterThen = (value instanceof Integer) && ((Integer) value) > Integer.valueOf(operatorNum);
        Boolean isShortGreaterThen = (value instanceof Short) && ((Short) value) > Short.valueOf(operatorNum);
        Boolean isFloatGreaterThen = (value instanceof Float) && ((Float) value) > Float.valueOf(operatorNum);
        Boolean isDoubleGreaterThen = (value instanceof Double) && ((Double) value) > Double.valueOf(operatorNum);
        Boolean isCollectionGreaterThen = (value instanceof Collection) && ((Collection) value).size() > Integer.valueOf(operatorNum);
        if (isStringGreaterThen || isLongGreaterThen || isIntegerGreaterThen ||
                isShortGreaterThen || isFloatGreaterThen || isDoubleGreaterThen || isCollectionGreaterThen) {
            isGreaterThan = true;
        }
        return isGreaterThan;
    }

    /**
     * 大于等于
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isGreaterThanEqual(Object value, String operatorNum) {
        Boolean isGreaterThanEqual = Boolean.FALSE;
        if (value == null) {
            return Boolean.FALSE;
        }
        Boolean isStringGreaterThenEqual = (value instanceof String) && ((String) value).length() >= Integer.valueOf(operatorNum);
        Boolean isLongGreaterThenEqual = (value instanceof Long) && ((Long) value) >= Long.valueOf(operatorNum);
        Boolean isIntegerGreaterThenEqual = (value instanceof Integer) && ((Integer) value) >= Integer.valueOf(operatorNum);
        Boolean isShortGreaterThenEqual = (value instanceof Short) && ((Short) value) >= Short.valueOf(operatorNum);
        Boolean isFloatGreaterThenEqual = (value instanceof Float) && ((Float) value) >= Float.valueOf(operatorNum);
        Boolean isDoubleGreaterThenEqual = (value instanceof Double) && ((Double) value) >= Double.valueOf(operatorNum);
        Boolean isCollectionGreaterThenEqual = (value instanceof Collection) && ((Collection) value).size() >= Integer.valueOf(operatorNum);
        if (isStringGreaterThenEqual || isLongGreaterThenEqual || isIntegerGreaterThenEqual ||
                isShortGreaterThenEqual || isFloatGreaterThenEqual || isDoubleGreaterThenEqual || isCollectionGreaterThenEqual) {
            isGreaterThanEqual = Boolean.TRUE;
        }
        return isGreaterThanEqual;
    }

    /**
     * 少于
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isLessThan(Object value, String operatorNum) {
        Boolean isLessThan = Boolean.FALSE;
        if (value == null) {
            return Boolean.FALSE;
        }
        Boolean isStringLessThen = (value instanceof String) && ((String) value).length() < Integer.valueOf(operatorNum);
        Boolean isLongLessThen = (value instanceof Long) && ((Long) value) < Long.valueOf(operatorNum);
        Boolean isIntegerLessThen = (value instanceof Integer) && ((Integer) value) < Integer.valueOf(operatorNum);
        Boolean isShortLessThen = (value instanceof Short) && ((Short) value) < Short.valueOf(operatorNum);
        Boolean isFloatLessThen = (value instanceof Float) && ((Float) value) < Float.valueOf(operatorNum);
        Boolean isDoubleLessThen = (value instanceof Double) && ((Double) value) < Double.valueOf(operatorNum);
        Boolean isCollectionLessThen = (value instanceof Collection) && ((Collection) value).size() < Integer.valueOf(operatorNum);
        if (isStringLessThen || isLongLessThen || isIntegerLessThen ||
                isShortLessThen || isFloatLessThen || isDoubleLessThen || isCollectionLessThen) {
            isLessThan = Boolean.TRUE;
        }
        return isLessThan;
    }

    /**
     * 少于等于
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isLessThanEqual(Object value, String operatorNum) {
        Boolean isLessThanEqual = Boolean.FALSE;
        if (value == null) {
            return Boolean.FALSE;
        }
        Boolean isStringLessThenEqual = (value instanceof String) && ((String) value).length() <= Integer.valueOf(operatorNum);
        Boolean isLongLessThenEqual = (value instanceof Long) && ((Long) value) <= Long.valueOf(operatorNum);
        Boolean isIntegerLessThenEqual = (value instanceof Integer) && ((Integer) value) <= Integer.valueOf(operatorNum);
        Boolean isShortLessThenEqual = (value instanceof Short) && ((Short) value) <= Short.valueOf(operatorNum);
        Boolean isFloatLessThenEqual = (value instanceof Float) && ((Float) value) <= Float.valueOf(operatorNum);
        Boolean isDoubleLessThenEqual = (value instanceof Double) && ((Double) value) <= Double.valueOf(operatorNum);
        Boolean isCollectionLessThenEqual = (value instanceof Collection) && ((Collection) value).size() <= Integer.valueOf(operatorNum);
        if (isStringLessThenEqual || isLongLessThenEqual || isIntegerLessThenEqual ||
                isShortLessThenEqual || isFloatLessThenEqual || isDoubleLessThenEqual || isCollectionLessThenEqual) {
            isLessThanEqual = Boolean.TRUE;
        }
        return isLessThanEqual;
    }

    /**
     * 不等于
     * @param value
     * @param operatorNum
     * @return
     */
    private static Boolean isNotEqual(Object value, String operatorNum) {
        Boolean isNotEqual = Boolean.FALSE;
        if (value == null) {
            return Boolean.FALSE;
        }
        Boolean isStringNotEqual = (value instanceof String) && !value.equals(operatorNum);
        Boolean isLongNotEqual = (value instanceof Long) && !value.equals(Long.valueOf(operatorNum));
        Boolean isIntegerNotEqual = (value instanceof Integer) && !value.equals(Integer.valueOf(operatorNum));
        Boolean isShortNotEqual = (value instanceof Short) && !value.equals(Short.valueOf(operatorNum));
        Boolean isFloatNotEqual = (value instanceof Float) && !value.equals(Float.valueOf(operatorNum));
        Boolean isDoubleNotEqual = (value instanceof Double) && !value.equals(Double.valueOf(operatorNum));
        Boolean isCollectionNotEqual = (value instanceof Collection) && ((Collection) value).size() != Integer.valueOf(operatorNum);
        if (isStringNotEqual || isLongNotEqual || isIntegerNotEqual ||
                isShortNotEqual || isFloatNotEqual || isDoubleNotEqual || isCollectionNotEqual) {
            isNotEqual = Boolean.TRUE;
        }
        return isNotEqual;
    }

    private static Boolean isPattern(Object value, String operatorNum) {
        Boolean isNotEqual = Boolean.FALSE;
        if (value == null) {
            return Boolean.FALSE;
        }
        String string = (String)value;
        return Pattern.matches(operatorNum, string);
    }



    class FileInfo{
        String field;

        String errMsg;

        String operator;

        String operatorNum;

        Operator optEnum;
    }

    enum Operator {
        /**
         * 大于
         */
        GREATER_THAN(">", ParamCheckAspect::isGreaterThan),
        /**
         * 大于等于
         */
        GREATER_THAN_EQUAL(">=", ParamCheckAspect::isGreaterThanEqual),
        /**
         * 小于
         */
        LESS_THAN("<", ParamCheckAspect::isLessThan),
        /**
         * 小于等于
         */
        LESS_THAN_EQUAL("<=", ParamCheckAspect::isLessThanEqual),
        /**
         * 不等于
         */
        NOT_EQUAL("!=", ParamCheckAspect::isNotEqual),
        /**
         * 不为空
         */
        NOT_NULL("not null", ParamCheckAspect::isNotNull),

        /**
         * 正则表达式
         */
        PATTERN("pattern", ParamCheckAspect::isPattern);

        private String value;

        /**
         * BiFunction：接收字段值(Object)和操作数(String)，返回是否符合规则(Boolean)
         */
        private BiFunction<Object, String, Boolean> fun;

        Operator(String value, BiFunction<Object, String, Boolean> fun) {
            this.value = value;
            this.fun = fun;
        }
    }

}
