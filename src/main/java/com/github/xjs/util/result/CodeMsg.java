package com.github.xjs.util.result;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年9月15日 下午12:27:58<br>
 * 
 * 接口的错误码
 */
public class CodeMsg implements ICodeMsg{
	
	public static CodeMsg SUCCESS = new CodeMsg(ICodeMsg.SUCCESS_CODE, ICodeMsg.SUCCESS_MSG);
	public static CodeMsg SERVER_ERROR = new CodeMsg(ICodeMsg.SERVER_ERROR_CODE, ICodeMsg.SERVER_ERROR_MSG);
	public static CodeMsg VALIDATE_ERROR = new CodeMsg(400, "参数检验出错:%s");
	public static CodeMsg BUSINESS_ERROR = new CodeMsg(ICodeMsg.SERVER_ERROR_CODE, "%s");
	//TODO 添加其他的错误码和错误信息
	;

	private int code;
	private String message;
	private CodeMsg(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	@Override
	public int getErrcode() {
		return code;
	}
	
	@Override
	public String getErrmsg(String... args) {
		if(args == null || args.length <= 0) {
			return message;
		}
		return String.format(message, (Object[])args);
	}
	
	public ICodeMsg withArgs(String... args) {
		if(args == null || args.length <= 0) {
			return this;
		}
		int code = this.code;
		String message = getErrmsg(args);
		return new CodeMsg(code, message);
	}
	
	@Override
	public String toString() {
		return code+":"+message;
	}
}
