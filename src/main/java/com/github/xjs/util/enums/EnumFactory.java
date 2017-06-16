package com.github.xjs.util.enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnumFactory {
	
	@SuppressWarnings("rawtypes")
	private static ConcurrentHashMap<Class, Set> map = new ConcurrentHashMap<Class, Set>();

	public static <V, T extends BaseEnum<V>> void add(T t) {
		@SuppressWarnings("unchecked")
		Set<BaseEnum<V>> list = map.get(t.getClass());
		if (list == null) {
			list = new HashSet<BaseEnum<V>>();
			map.putIfAbsent(t.getClass(), list);
		}
		list.add(t);
	}

	@SuppressWarnings("unchecked")
	public static <V, T extends BaseEnum<V>> T getByValue(Class<T> clazz, V value) {
		init(clazz);
		Set<BaseEnum<V>> list = map.get(clazz);
		for (BaseEnum<V> be : list) {
			if (be.getValue().equals(value)) {
				return (T) be;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends BaseEnum<V>, V> T getByLabel(Class<T> clazz, String label) {
		init(clazz);
		Set<BaseEnum<V>> list = map.get(clazz);
		for (BaseEnum<V> be : list) {
			if (be.getLabel().equals(label)) {
				return (T) be;
			}
		}
		return null;
	}

    @SuppressWarnings("unchecked")
	public static <T extends BaseEnum<V>, V> List<T> getAll(Class<T> clazz) {
        init(clazz);
        Set<T> set = map.get(clazz);
        if (set != null && set.size() > 0){
        	List<T> list =  new ArrayList<T>(set.size());
            list.addAll(set);
            return list;
        }
        return null;
	}
    
    private static void init(Class<?> clazz){
		if(map.get(clazz) != null){
			return;
		}
        try {
            Field[] fields = clazz.getFields();
            for (Field f : fields) {
                f.get(null);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
