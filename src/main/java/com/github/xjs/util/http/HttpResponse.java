package com.github.xjs.util.http;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午2:08:20<br/>
 */
public class HttpResponse<T>{
	private int statusCode;
	private String errMsg;
	private T content;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
}
