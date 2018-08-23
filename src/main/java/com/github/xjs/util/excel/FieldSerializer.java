package com.github.xjs.util.excel;

/**
 * @author 605162215@qq.com
 *
 * @date 2018年8月23日 下午1:44:41<br/>
 */
public interface FieldSerializer<T,V>{
    public String serialize(T bean, V fieldValue);
}
