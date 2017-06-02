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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:27:50
 */
public class CollectionUtil {
	
	public static <T> T find(List<T> collection, Predicate<T> predicate){
		Optional<T> optional = collection.stream().filter(predicate).findFirst();
		if(optional.isPresent()){
			return optional.get();
		}
		return null;
    }
	
	public static <T> boolean contains(List<T> collection, Predicate<T> predicate){
		Optional<T> optional = collection.stream().filter(predicate).findAny();
		return optional.isPresent();
    }
	
    public static boolean isEmpty(Collection<?> collection) {
		return (collection == null) || collection.isEmpty();
	}
    
    public static boolean isEmpty(Map<?,?> map) {
		return (map == null) || map.isEmpty();
	}
    
    public static <R,T> List<R> extractNotEmptyString(Collection<T> collection, Function<T, R> mapper){
    	if(collection == null || collection.size() <= 0 || mapper == null){
    		return null;
    	}
    	return collection.stream().map(mapper).collect(Collectors.toList());
    }
    
    @Deprecated
    public static <R,T> List<R> extractNotEmptyString(Collection<T> collection, String fieldName){
    	Predicate<R> predicate = (fieldValue)->{return fieldValue!=null && !"".equals(fieldValue);};
    	return extract(collection, fieldName, predicate);
    }
    
    @Deprecated
    public static <R,T> List<R> extract(Collection<T> collection, String fieldName){
    	Predicate<R> predicate = (fieldValue)->fieldValue!=null;
    	return extract(collection, fieldName, predicate);
    }
    
    @Deprecated
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
		return collection.stream().filter(predicate).collect(Collectors.toList());
	}
	
	@SuppressWarnings("rawtypes")
	public static List arrayToList(Object source) {
		return Arrays.asList(ObjectUtil.toObjectArray(source));
	}
	
	public static <T> String join(List<T> collection){
		return join(collection, ",");
	}
	
	public static <T> String join(List<T> collection, String sep){
		if(collection == null || collection.size() <= 0){
			return "";
		}
		if(StringUtil.isEmpty(sep)){
			sep = "";
		}
		final String s = sep;
		Optional<String> optional = collection.stream().map((t)->t.toString()).reduce((a,b)->a + s + b);
		if(optional.isPresent()){
			return optional.get();
		}else{
			return "";
		}
	}
}
