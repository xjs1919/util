/**
 * 
 */
package com.github.xjs.util.retry.persist;

import java.util.List;

/**
 * @author 605162215@qq.com
 *
 *         2016年8月15日 上午10:23:57
 */
public interface PersistService<T> {
	
	public void save(T t);

	public void delete(T t);

	public List<T> getAll();

	public int size();
}
