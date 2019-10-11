package com.github.xjs.util;


import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/9/6 15:16
 **/
public class YmlUtil {

    private Map<String,Object> resultMap = new HashMap<String,Object>();

    public void init(InputStream inputStream){
        Yaml yaml = new Yaml();
        Iterator<Object> result = yaml.loadAll(inputStream).iterator();
        while(result.hasNext()){
            Map map=(Map)result.next();
            iteratorYml( map,null);
        }
        IOUtil.closeQuietly(inputStream);
    }

    public Object get(String key) {
        return resultMap.get(key);
    }

    private void iteratorYml(Map map,String parentKey){
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            //如果parent是空，说明是最外层
            if(StringUtil.isEmpty(parentKey)){
                if(value instanceof LinkedHashMap){
                    iteratorYml((Map)value, key.toString());
                }else {
                    resultMap.put(key.toString(), value);
                }
            }else{//追加parent key
                if(value instanceof LinkedHashMap){
                    iteratorYml((Map)value, parentKey+"."+ key.toString());
                }else{
                    resultMap.put(parentKey+"."+key.toString(), value);
                }
            }
        }
    }

    public static void main(String[] args)throws Exception {
        String ymlstr = "key:\n" +
                "  - a\n" +
                "  - b\n" +
                "  - c\n" +
                "key2: hello\n" +
                "key3: {'user':'xjs', 'pass':'123456'}";
        YmlUtil yml = new YmlUtil();
        InputStream in = new ByteArrayInputStream(ymlstr.getBytes());
        yml.init(in);
        Object o1 = yml.get("key");
        System.out.println(o1+","+o1.getClass().getName());
        Object o2 = yml.get("key2");
        System.out.println(o2);
        Object o3 = yml.get("key3.user");
        System.out.println(o3);
    }
}
