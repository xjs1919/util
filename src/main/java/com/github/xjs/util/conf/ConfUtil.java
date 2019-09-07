package com.github.xjs.util.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/9/7 12:46
 **/
public class ConfUtil {

    private ConfUtil(){}

    private static AtomicBoolean init = new AtomicBoolean(false) ;

    private static ConfUtil instance = new ConfUtil();

    private ConfType confType;

    public static void init(File confFile)throws Exception{
        InputStream in = new FileInputStream(confFile);
        init(in, ConfType.getBySuffix(confFile.getName()));
    }

    public static void init(InputStream inputStream, ConfType confType)throws Exception{
        if(!init.compareAndSet(false, true)){
            return;
        }
        instance.confType = confType;
        instance.confType.getConfHandler().init(inputStream);
    }

    public static <T> T get(String key, Class<T> valueClazz, T defValue){
        return instance.confType.getConfHandler().get(key, valueClazz, defValue);
    }
}
