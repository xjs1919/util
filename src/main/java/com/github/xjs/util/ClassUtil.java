/**
 * 
 */
package com.github.xjs.util;

/**
 * @author xujs@inspur.com
 *
 * @date 2017年9月1日 下午2:47:01
 */
public class ClassUtil {
	
	public static Class<?> getCallerClass(int stackTraceDepth){
		StackTraceElement stackElements[] = Thread.currentThread().getStackTrace(); 
		if(stackElements.length < stackTraceDepth) {
			return null;
		}
		String callerClass = stackElements[stackTraceDepth].getClassName(); 
		try{
			try {
				ClassLoader cl = getDefaultClassLoader();
				return cl.loadClass(callerClass);
			}catch(Exception e) {
				return Class.forName(callerClass);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return LogUtil.class;
		}
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = LogUtil.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}
}
