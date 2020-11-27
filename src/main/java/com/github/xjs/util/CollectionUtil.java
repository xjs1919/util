/**
 * 
 */
package com.github.xjs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月12日 下午5:27:50
 */
public class CollectionUtil {


	/**
	 * 按照batchSize批量执行，比如: 数据库批量更新的场景，防止一次太多，可以拆分多次执行
	 * */
	public static <T> void batchExecute(List<T> list, int batchSize, Consumer<? super List<T>> consumer){
		List<List<T>> listList = split(list, batchSize);
		if(listList == null || listList.size() <= 0){
			return;
		}
		listList.stream().forEach(consumer);
	}

	/**
	 * list按照batchSize拆分
	 * */
	public static <T> List<List<T>> split(List<T> list, int batchSize){
		if(batchSize <= 0){
			throw new RuntimeException("batch size 必须大于0");
		}
		if(list == null || list.size() <= 0){
			return new ArrayList<>(0);
		}
		int totalSize = list.size();
		int resultSize = (totalSize + batchSize - 1)/batchSize;
		List<List<T>> ret = new ArrayList<>(resultSize);
		for(int i=0; i<resultSize; i++){
			List<T> subList = new ArrayList<>(batchSize);
			for(int j=i*batchSize; j<list.size() && j<(i+1)*batchSize; j++){
				subList.add(list.get(j));
			}
			ret.add(subList);
		}
		return ret;
	}
	
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
    
    public static <R,T> List<R> extract(Collection<T> collection, Function<T, R> mapper){
    	if(collection == null || collection.size() <= 0 || mapper == null){
    		return null;
    	}
    	return collection.stream().map(mapper).collect(Collectors.toList());
    }
    
    public static <R,T> List<R> extract(Collection<T> collection, Function<T, R> mapper, Predicate<R> filter){
    	if(collection == null || collection.size() <= 0 || mapper == null){
    		return null;
    	}
    	return collection.stream().map(mapper).filter(filter).collect(Collectors.toList());
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
