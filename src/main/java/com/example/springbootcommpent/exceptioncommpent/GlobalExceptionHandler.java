package com.example.springbootcommpent.exceptioncommpent;

import com.example.springbootcommpent.common.BaseResult;
import com.example.springbootcommpent.exceptioncommpent.exception.CustomException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 * @author zhouliangze
 * @date 2019/8/20 17:09
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{


    /**
     * 自定义异常捕获
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public BaseResult customExceptionHandler(HttpServletRequest request, final Exception e){
        CustomException exception = (CustomException)e;
        return new BaseResult(exception.getCode(), exception.getMessage());
    }

    /**
     * 运行时异常捕获
     * @param request
     * @param e
     * @param response
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResult runtimeExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response){
        BaseResult baseResult = new BaseResult();
        RuntimeException runtimeException = (RuntimeException) e;
        baseResult.exceptioned(runtimeException.getMessage());
        return baseResult;
    }

    /**
     * valid校验接口异常
     * 通用的接口映射异常处理方
     * @param ex
     * @param body
     * @param httpHeaders
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders httpHeaders, HttpStatus status, WebRequest request){
        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException)ex;
            return new ResponseEntity<>(new BaseResult(BaseResult.FAILURE, exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()), status);
        }
        if(ex instanceof MethodArgumentTypeMismatchException){
            MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
            return new ResponseEntity<>(new BaseResult(BaseResult.EXCEPTION, "参数转换失败"), status);
        }

        return new ResponseEntity<>(new BaseResult(BaseResult.EXCEPTION, "参数转换失败"), status);
    }

}
