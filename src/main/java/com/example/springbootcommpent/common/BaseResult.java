package com.example.springbootcommpent.common;

/**
 * 返回实体类
 * @author zhouliangze
 * @data 2019/7/3 15:13
 */
public class BaseResult {
    /**
     * 成功标识
     */
    public static final int SUCCESS = 0;

    /**
     * 失败标识
     */
    public static final int FAILURE = 1;

    /**
     * 异常标识
     */
    public static final int EXCEPTION = 9;

    private int code;
    private String msg;
    private Object data;

    public BaseResult() {
        this.code = SUCCESS;
        this.msg = "SUCCESS";
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 判断返回是否成功
     * @return
     */
    private boolean isSuccess(){
        return this.code == SUCCESS;
    }

    /**
     * 设置失败信息
     * @param message
     */
    public void failured(String message){
        this.code = FAILURE;
        this.data = null;
        this.msg = message;
    }

    /**
     * 设置异常信息
     * @param message
     */
    public void exceptioned(String message){
        this.code = EXCEPTION;
        this.data = null;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
