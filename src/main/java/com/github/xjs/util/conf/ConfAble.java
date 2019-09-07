package com.github.xjs.util.conf;

import java.io.InputStream;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/9/7 12:59
 **/
public interface ConfAble {

    /**初始化**/
    void init(InputStream in)throws Exception;

    /**获取key的值*/
    String get(String key);

    default <T> T get(String key, Class<T> valueClazz, T defaultValue) {
        String value = get(key);
        if(value == null){
            return defaultValue;
        }
        return strToValue(value, valueClazz);
    }

    default <T> T strToValue(String value, Class<T> valueClazz){
        if(valueClazz == int.class || valueClazz == Integer.class){
            return (T)Integer.valueOf(value);
        }if(valueClazz == String.class){
            return (T)value;
        }else{
            return (T)value;
        }
    }
}
