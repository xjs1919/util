package com.github.xjs.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:44:13<br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)  
public @interface FiledOrder{
	public int value();//从0开始
	public String name() default "";
}
