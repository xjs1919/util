/**
 * 
 */
package com.chrhc.xjs.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:27:50
 */
public class CollectionUtil {
	
	public interface Has<T, F>{
        public boolean has(T bean, F fieldValue);
    }

    public static <T, F> T find(List<T> collection, F fieldValue, Has<T, F> has){
        for(T elem : collection){
            if(has.has(elem, fieldValue)){
                return elem;
            }
        }
        return null;
    }

    public interface Eq<T>{
        public boolean eq(T t1, T t2);
    }

    public static <T> boolean contains(List<T> collection,T elem){
        if(elem == null){
            return false;
        }
        Eq<T> eq = (t1, t2)->t1.equals(t2);
        return contains(collection, elem, eq);
    }

    public static <T> boolean contains(List<T> collection,T elem, Eq<T> eq){
        if(elem == null){
            return false;
        }
        for(T t : collection){
            if(eq.eq(elem, t)){
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
}
