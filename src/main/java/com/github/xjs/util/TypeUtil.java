/**
 * 
 */
package com.github.xjs.util;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午9:59:27
 */
public class TypeUtil {
	
	public static boolean isSimpleType(Class<?> clazz){
		if(clazz == String.class){
			return true;
		}else if(clazz == int.class || clazz == Integer.class){
			return true;
		}else if(clazz == long.class || clazz == Long.class){
			return true;
		}else if(clazz == boolean.class || clazz == Boolean.class){
			return true;
		}else if(clazz == byte.class || clazz == Byte.class){
			return true;
		}else if(clazz == char.class || clazz == Character.class){
			return true;
		}else if(clazz == short.class || clazz == Short.class){
			return true;
		}else if(clazz == float.class || clazz == Float.class){
			return true;
		}else if(clazz == double.class || clazz == Double.class){
			return true;
		}
		return false;
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = TypeUtil.class.getClassLoader();
		}
		return cl;
	}
	
	public static Class<?> forName(String name) throws ClassNotFoundException, LinkageError {
		return forName(name, null);
	}
	
	public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return classLoaderToUse.loadClass(name);
		}
		catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
				try {
					return classLoaderToUse.loadClass(innerClassName);
				}
				catch (ClassNotFoundException ex2) {
					// swallow - let original exception get through
				}
			}
			throw ex;
		}
	}

	
}
