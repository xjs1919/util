package com.github.xjs.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 605162215@qq.com
 *
 * 2017年6月5日 下午3:45:18
 */
public class LogUtil {
	
	private static ConcurrentHashMap<Class<?>, Logger> loggers = new ConcurrentHashMap<Class<?>, Logger>();
	
	public static void debug(Supplier<String> msgSupplier){
		Class<?> clazz = ClassUtil.getCallerClass(3);
		debug(clazz, msgSupplier, null);
	}
	
	public static void debug(Class<?> clazz, Supplier<String> msgSupplier){
		debug(clazz, msgSupplier, null);
	}
	
	public static void debug(Class<?> clazz, Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		Logger log = getLogger(clazz);
		if(log.isDebugEnabled()){
			if(exceptionSupplier == null){
				log.debug(msgSupplier.get());
			}else{
				log.debug(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void error(Supplier<String> msgSupplier){
		Class<?> clazz = ClassUtil.getCallerClass(3);
		error(clazz, msgSupplier, null);
	}
	
	public static void error(Class<?> clazz, Supplier<String> msgSupplier){
		error(clazz, msgSupplier, null);
	}
	
	public static void error(Class<?> clazz, Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		Logger log = getLogger(clazz);
		if(log.isErrorEnabled()){
			if(exceptionSupplier == null){
				log.error(msgSupplier.get());
			}else{
				log.error(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void info(Supplier<String> msgSupplier){
		Class<?> clazz = ClassUtil.getCallerClass(3);
		info(clazz, msgSupplier, null);
	}
	
	public static void info(Class<?> clazz, Supplier<String> msgSupplier){
		info(clazz, msgSupplier, null);
	}
	
	public static void info(Class<?> clazz, Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		Logger log = getLogger(clazz);
		if(log.isInfoEnabled()){
			if(exceptionSupplier == null){
				log.info(msgSupplier.get());
			}else{
				log.info(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void trace(Supplier<String> msgSupplier){
		Class<?> clazz = ClassUtil.getCallerClass(3);
		trace(clazz, msgSupplier, null);
	}
	
	public static void trace(Class<?> clazz, Supplier<String> msgSupplier){
		trace(clazz, msgSupplier, null);
	}
	
	public static void trace(Class<?> clazz, Supplier<String> msgSupplier,  Supplier<Exception> exceptionSupplier){
		Logger log = getLogger(clazz);
		if(log.isTraceEnabled()){
			if(exceptionSupplier == null){
				log.trace(msgSupplier.get());
			}else{
				log.trace(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	public static void warn(Supplier<String> msgSupplier){
		Class<?> clazz = ClassUtil.getCallerClass(3);
		warn(clazz, msgSupplier, null);
	}
	
	public static void warn(Class<?> clazz, Supplier<String> msgSupplier){
		warn(clazz, msgSupplier, null);
	}
	
	public static void warn(Class<?> clazz, Supplier<String> msgSupplier, Supplier<Exception> exceptionSupplier){
		Logger log = getLogger(clazz);
		if(log.isWarnEnabled()){
			if(exceptionSupplier == null){
				log.warn(msgSupplier.get());
			}else{
				log.warn(msgSupplier.get(), exceptionSupplier.get());
			}
		}
	}
	
	private static Logger getLogger(Class<?> clazz) {
		if(clazz == null) {
			clazz = LogUtil.class;
		}
		Logger logger = loggers.get(clazz);
		if(logger == null) {
			synchronized(LogUtil.class) {
				logger = loggers.get(clazz);
				if(logger == null) {
					logger = LoggerFactory.getLogger(clazz);
					loggers.put(clazz, logger);
				}
			}
		}
		return logger;
	}
}
