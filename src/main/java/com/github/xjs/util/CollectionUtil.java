/**
 * 
 */
package com.github.xjs.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:27:50
 */
public class CollectionUtil {
	
	public static <T> T find(List<T> collection, Predicate<T> predicate){
        for(T elem : collection){
            if(predicate.test(elem)){
            	return elem;
            }
        }
        return null;
    }
	
	public static <T> boolean contains(List<T> collection, Predicate<T> predicate){
        for(T elem : collection){
            if(predicate.test(elem)){
            	return true;
            }
        }
        return false;
    }
	
    public static boolean isEmpty(Collection<?> collection) {
		return (collection == null) || collection.isEmpty();
	}
    
    public static boolean isEmpty(Map<?,?> map) {
		return (map == null) || map.isEmpty();
	}
    
    public static <R,T> List<R> extractNotEmptyString(Collection<T> collection, String fieldName){
    	Predicate<R> predicate = (fieldValue)->{return fieldValue!=null && !"".equals(fieldValue);};
    	return extract(collection, fieldName, predicate);
    }
    
    public static <R,T> List<R> extract(Collection<T> collection, String fieldName){
    	Predicate<R> predicate = (fieldValue)->fieldValue!=null;
    	return extract(collection, fieldName, predicate);
    }
    
	public static <R,T> List<R> extract(Collection<T> collection, String fieldName, Predicate<R> predicate){
        if(collection == null || collection.size() <= 0){
            return null;
        }
        try{
            BeanInfo bi = Introspector.getBeanInfo(collection.iterator().next().getClass());
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
            if(pds == null || pds.length <= 0){
                return null;
            }
            Method readMethod = null;
            for(PropertyDescriptor pd : pds){
                if(pd.getName().equals(fieldName)){
                    readMethod = pd.getReadMethod();
                    break;
                }
            }
            if(readMethod == null){
            	return null;
            }
            List<R> retList = new ArrayList<R>(collection.size());
            for(T elem : collection){
                @SuppressWarnings("unchecked")
				R r = (R)readMethod.invoke(elem, (Object[])null);
                if(predicate != null){
                    if(predicate.test(r)){
                        retList.add(r);
                    }
                }
            }
            return retList;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
	
	public static <T> List<T> subList(Collection<T> collection, Predicate<T> predicate){
		if(collection == null || collection.size() <= 0){
			return null;
		}
		List<T> ret = new ArrayList<T>(collection.size());
		for(T elem : collection){
			if(predicate.test(elem)){
				ret.add(elem);
			}
		}
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public static List arrayToList(Object source) {
		return Arrays.asList(ObjectUtil.toObjectArray(source));
	}
}
