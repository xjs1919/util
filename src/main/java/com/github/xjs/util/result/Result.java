package com.github.xjs.util.result;

public class Result<T> {
	private int errcode;
	private String errmsg;
	private T data;

	public Result(){
		this((T)null);
	}
	
	public Result(T data){//正常
		this.errcode = ICodeMsg.SUCCESS_CODE;
		this.errmsg = ICodeMsg.SUCCESS_MSG;
		this.data = data;
	}
	
	public Result(ICodeMsg codeMsg){//异常
		this.errcode = codeMsg.getErrcode();
		this.errmsg = codeMsg.getErrmsg();
	}
	
	/**
	 * 正常返回
	 * */
	public static <T> Result<T> success(T t){
		return new Result<T>(t);
	}
	
	/**
	 * 异常返回
	 * */
	public static <T> Result<T> error(ICodeMsg codeMsg){
		return new Result<T>(codeMsg);
	}
	
	/**
	 * 异常返回
	 * */
	public static <T> Result<T> error(Result<?> response){
		return new Result<T>(response.getErrcode(), response.getErrmsg());
	}
	
	/**
	 * 异常，不让外部使用
	 * */
	private Result(int errcode, String errmsg){
		this.errcode = errcode;
		this.errmsg = errmsg;
	}
	
	public int getErrcode() {
		return errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public T getData() {
		return data;
	}
}
