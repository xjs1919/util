package com.github.xjs.util.enums;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnumFactory {
	
	@SuppressWarnings("rawtypes")
	private static ConcurrentHashMap<Class, Set> map = new ConcurrentHashMap<Class, Set>();
	
	public static <V, T extends BaseEnum<V>> void add(T t){
		@SuppressWarnings("unchecked")
		Set<BaseEnum<V>> list = map.get(t.getClass());
		if(list == null){
			list = new HashSet<BaseEnum<V>>();
			map.putIfAbsent(t.getClass(), list);
		}
		list.add(t);
	}
	
	@SuppressWarnings("unchecked")
	public static <V, T extends BaseEnum<V>> T getByValue(Class<T> clazz, V value){
		Set<BaseEnum<V>> list = map.get(clazz);
		if(list == null){
			try{
				Field[] fields = clazz.getFields();
				for(Field f : fields){
					f.get(null);
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			list = map.get(clazz);
		}
		for(BaseEnum<V> be : list){
			if(be.getValue().equals(value)){
				return (T)be;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends BaseEnum<V>, V> T getByLabel(Class<T> clazz, String label){
		Set<BaseEnum<V>> list = map.get(clazz);
		if(list == null){
			try{
				Field[] fields = clazz.getFields();
				for(Field f : fields){
					f.get(null);
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			list = map.get(clazz);
		}
		for(BaseEnum<V> be : list){
			if(be.getLabel().equals(label)){
				return (T)be;
			}
		}
		return null;
	}
}
