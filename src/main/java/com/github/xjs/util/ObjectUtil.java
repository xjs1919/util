/**
 * 
 */
package com.github.xjs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;

/**
 * @author 605162215@qq.com
 *
 * 2016年10月12日 下午1:37:50
 */
public class ObjectUtil {
	
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		@SuppressWarnings("rawtypes")
		Class wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}
	
	public static byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
		}
		return baos.toByteArray();
	}
	
	public static Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return ois.readObject();
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to deserialize object", ex);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Failed to deserialize object type", ex);
		}
	}
}
