/**
 * 
 */
package com.github.xjs.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 605162215@qq.com
 *
 * 2017年6月5日 下午3:45:18
 */
public class LogUtil {
	
	public static Logger log = LoggerFactory.getLogger(LogUtil.class);
	
	public static void debug(Supplier<String> msgSupplier){
		if(log.isDebugEnabled()){
			log.debug(msgSupplier.get());
		}
	}
	
	public static void error(Supplier<String> msgSupplier){
		if(log.isErrorEnabled()){
			log.error(msgSupplier.get());
		}
	}
	
	public static void info(Supplier<String> msgSupplier){
		if(log.isInfoEnabled()){
			log.info(msgSupplier.get());
		}
	}
	
	public static void trace(Supplier<String> msgSupplier){
		if(log.isTraceEnabled()){
			log.trace(msgSupplier.get());
		}
	}
	
	public static void warn(Supplier<String> msgSupplier){
		if(log.isWarnEnabled()){
			log.warn(msgSupplier.get());
		}
	}
}
