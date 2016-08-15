/**
 * 
 */
package com.github.xjs.util.retry;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:21:44
 */
public interface RetryAble {
	public boolean retryAble()throws Exception;
}
