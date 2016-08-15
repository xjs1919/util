/**
 * 
 */
package com.github.xjs.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:01:37
 */
public class ExceptionUtil {

	public static String getStackTrace(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer.toString();
	}

	public static String getMainClassName() {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if ("main".equals(stackTraceElement.getMethodName())) {
				return stackTraceElement.getClassName();
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(getMainClassName());
	}
}
