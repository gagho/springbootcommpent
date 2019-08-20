package com.example.springbootcommpent.exceptioncommpent.exception;

/**
 * 自定义异常
 * @author zhouliangze
 * @date 2019/8/20 17:15
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public CustomException(){
        super();
    }

    public CustomException(int code, String message){
        super(message);
        this.setCode(code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
