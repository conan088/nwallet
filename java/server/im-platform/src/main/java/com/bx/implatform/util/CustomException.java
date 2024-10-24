package com.bx.implatform.util;

import com.bx.implatform.enums.ResponseEnum ;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private static final long serialVersionUID=1L;
	private int code = ResponseEnum.FAIL.getCode();//默认异常
	private Object data;

    public CustomException(String msg) {
        this(msg, false);
    }
    public CustomException(int code, String msg) {
        this(msg, false);
        this.code = code;
    }
    public CustomException(ResponseEnum r) {
    	this(r.getMsg(), false);
    	this.code = r.getCode();
    }
    public CustomException(ResponseEnum r,Object data) {
    	this(r.getMsg(), false);
    	this.code = r.getCode();
    	this.data = data;
    }
    /**
     * 包含message, 可指定是否记录异常
     * @param msg
     * @param recordStackTrace 最终异常性能消耗高50倍
     */
    public CustomException(String msg, boolean recordStackTrace) {
        super(msg, null, false, recordStackTrace);
    }
    /**
     * 包含message和cause, 会记录栈异常
     * @param msg
     * @param cause
     */
    public CustomException(String msg, Throwable cause) {
        super(msg, cause, false, true);
    }
}
