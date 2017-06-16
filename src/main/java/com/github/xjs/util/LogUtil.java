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
		debug(msgSupplier, null);
	}
	
	public static void debug(Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		if(log.isDebugEnabled()){
			if(exceptionSupplier == null){
				log.debug(msgSupplier.get());
			}else{
				log.debug(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void error(Supplier<String> msgSupplier){
		error(msgSupplier, null);
	}
	
	public static void error(Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		if(log.isErrorEnabled()){
			if(exceptionSupplier == null){
				log.error(msgSupplier.get());
			}else{
				log.error(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void info(Supplier<String> msgSupplier){
		info(msgSupplier, null);
	}
	
	public static void info(Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		if(log.isInfoEnabled()){
			if(exceptionSupplier == null){
				log.info(msgSupplier.get());
			}else{
				log.info(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void trace(Supplier<String> msgSupplier){
		trace(msgSupplier, null);
	}
	
	public static void trace(Supplier<String> msgSupplier,  Supplier<Exception> exceptionSupplier){
		if(log.isTraceEnabled()){
			if(exceptionSupplier == null){
				log.trace(msgSupplier.get());
			}else{
				log.trace(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void warn(Supplier<String> msgSupplier){
		warn(msgSupplier, null);
	}
	
	public static void warn(Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		if(log.isWarnEnabled()){
			if(exceptionSupplier == null){
				log.warn(msgSupplier.get());
			}else{
				log.warn(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
}
