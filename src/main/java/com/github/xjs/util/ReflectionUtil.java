/**
 * 
 */
package com.github.xjs.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 605162215@qq.com
 *
 * 2016年10月12日 下午1:48:23
 */
public class ReflectionUtil {
	
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}
	
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static void setField(Field field, Object target, Object value) {
		try {
			field.set(target, value);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	public static Object getField(Field field, Object target) {
		try {
			return field.get(target);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}
	
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class[0]);
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (Method method : methods) {
				if (name.equals(method.getName()) &&
						(paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}


	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) {
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	public static boolean declaresException(Method method, Class<?> exceptionType) {
		Class<?>[] declaredExceptions = method.getExceptionTypes();
		for (Class<?> declaredException : declaredExceptions) {
			if (declaredException.isAssignableFrom(exceptionType)) {
				return true;
			}
		}
		return false;
	}


	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	
	public static boolean isEqualsMethod(Method method) {
		if (method == null || !method.getName().equals("equals")) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == Object.class);
	}

	public static boolean isHashCodeMethod(Method method) {
		return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
	}

	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
	}

	public static boolean isObjectMethod(Method method) {
		try {
			Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (SecurityException ex) {
			return false;
		} catch (NoSuchMethodException ex) {
			return false;
		}
	}
	
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
				Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}
	
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
				&& !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	public static void doWithMethods(Class<?> clazz, MethodCallback mc) throws IllegalArgumentException {
		doWithMethods(clazz, mc, null);
	}

	public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf)
			throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Shouldn't be illegal to access method '" + method.getName()
						+ "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		}
		else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}

	public static Method[] getAllDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
		final List<Method> methods = new ArrayList<Method>(32);
		doWithMethods(leafClass, new MethodCallback() {
			public void doWith(Method method) {
				methods.add(method);
			}
		});
		return methods.toArray(new Method[methods.size()]);
	}

	public static void doWithFields(Class<?> clazz, FieldCallback fc) throws IllegalArgumentException {
		doWithFields(clazz, fc, null);
	}

	public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff)
			throws IllegalArgumentException {

		// Keep backing up the inheritance hierarchy.
		Class<?> targetClass = clazz;
		do {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				// Skip static and final fields.
				if (ff != null && !ff.matches(field)) {
					continue;
				}
				try {
					fc.doWith(field);
				}
				catch (IllegalAccessException ex) {
					throw new IllegalStateException(
							"Shouldn't be illegal to access field '" + field.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}


	public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
		if (src == null) {
			throw new IllegalArgumentException("Source for field copy cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("Destination for field copy cannot be null");
		}
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName()
					+ "] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				makeAccessible(field);
				Object srcValue = field.get(src);
				field.set(dest, srcValue);
			}
		}, COPYABLE_FIELDS);
	}


	/**
	 * Action to take on each method.
	 */
	public interface MethodCallback {

		/**
		 * Perform an operation using the given method.
		 * @param method the method to operate on
		 */
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
	}


	/**
	 * Callback optionally used to filter methods to be operated on by a method callback.
	 */
	public interface MethodFilter {

		/**
		 * Determine whether the given method matches.
		 * @param method the method to check
		 */
		boolean matches(Method method);
	}


	/**
	 * Callback interface invoked on each field in the hierarchy.
	 */
	public interface FieldCallback {

		/**
		 * Perform an operation using the given field.
		 * @param field the field to operate on
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}


	/**
	 * Callback optionally used to filter fields to be operated on by a field callback.
	 */
	public interface FieldFilter {

		/**
		 * Determine whether the given field matches.
		 * @param field the field to check
		 */
		boolean matches(Field field);
	}


	/**
	 * Pre-built FieldFilter that matches all non-static, non-final fields.
	 */
	public static FieldFilter COPYABLE_FIELDS = new FieldFilter() {

		public boolean matches(Field field) {
			return !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()));
		}
	};


	/**
	 * Pre-built MethodFilter that matches all non-bridge methods.
	 */
	public static MethodFilter NON_BRIDGED_METHODS = new MethodFilter() {

		public boolean matches(Method method) {
			return !method.isBridge();
		}
	};


	/**
	 * Pre-built MethodFilter that matches all non-bridge methods
	 * which are not declared on {@code java.lang.Object}.
	 */
	public static MethodFilter USER_DECLARED_METHODS = new MethodFilter() {

		public boolean matches(Method method) {
			return (!method.isBridge() && method.getDeclaringClass() != Object.class);
		}
	};

}
