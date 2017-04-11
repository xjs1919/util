/**
 * 
 */
package com.github.xjs.util;

import java.util.EnumSet;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午9:56:58
 */
public class EnumUtil {
	
	public interface BaseEnum<V> {
		public V getValue();
		public String getLabel();
	}
	
	public static <T extends Enum<T> & BaseEnum<V>, V> T getByValue(Class<T> clazz, V id){
		EnumSet<T> set = EnumSet.allOf(clazz);
		for(T t : set){
			if(t.getValue().equals(id)){
				return t;
			}
		}
		return null;
	}
	
	public static <T extends Enum<T> & BaseEnum<V>, V> T getByLabel(Class<T> clazz, String label){
		EnumSet<T> set = EnumSet.allOf(clazz);
		for(T t : set){
			if(t.getLabel().equals(label)){
				return t;
			}
		}
		return null;
	}
	
}
