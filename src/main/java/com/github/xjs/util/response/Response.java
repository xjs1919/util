package com.github.xjs.util.response;

public class Response<T> {
	private int errcode;
	private String errmsg;
	private T data;

	public Response(){
		this((T)null);
	}
	
	public Response(T data){//正常
		this.errcode = ICodeMsg.SUCCESS_CODE;
		this.errmsg = ICodeMsg.SUCCESS_MSG;
		this.data = data;
	}
	
	public Response(ICodeMsg codeMsg){//异常
		this.errcode = codeMsg.getErrcode();
		this.errmsg = codeMsg.getErrmsg();
	}
	
	/**
	 * 正常返回
	 * */
	public static <T> Response<T> success(T t){
		return new Response<T>(t);
	}
	
	/**
	 * 异常返回
	 * */
	public static <T> Response<T> error(ICodeMsg codeMsg){
		return new Response<T>(codeMsg);
	}
	
	/**
	 * 异常返回
	 * */
	public static <T> Response<T> error(Response<?> response){
		return new Response<T>(response.getErrcode(), response.getErrmsg());
	}
	
	/**
	 * 异常，不让外部使用
	 * */
	private Response(int errcode, String errmsg){
		this.errcode = errcode;
		this.errmsg = errmsg;
	}
	
	public int getErrcode() {
		return errcode;
	}
	
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
