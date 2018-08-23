package com.github.xjs.util.result;

public interface ICodeMsg {
	public static final int SUCCESS_CODE = 0;
	public static final String SUCCESS_MSG = "成功";
	public static final int SERVER_ERROR_CODE = 500;
	public static final String SERVER_ERROR_MSG = "服务端异常";
	public static final int DUBBO_ERROR_CODE = 501;
	public static final String DUBBO_ERROR_MSG = "远程服务端异常";
	public int getErrcode();
	public String getErrmsg(String ... args);
	public ICodeMsg withArgs(String ... args);
}
