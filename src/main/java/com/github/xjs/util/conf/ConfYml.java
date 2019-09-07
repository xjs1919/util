package com.github.xjs.util.conf;


import com.github.xjs.util.IOUtil;
import com.github.xjs.util.StringUtil;
import org.yaml.snakeyaml.Yaml;

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
class ConfYml implements ConfAble{

    private Map<String,String> resultMap = new HashMap<String,String>();

    @Override
    public void init(InputStream inputStream){
        Yaml yaml = new Yaml();
        Iterator<Object> result = yaml.loadAll(inputStream).iterator();
        while(result.hasNext()){
            Map map=(Map)result.next();
            iteratorYml( map,null);
        }
        IOUtil.closeQuietly(inputStream);
    }

    @Override
    public String get(String key) {
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
                    resultMap.put(key.toString(), value==null?"":value.toString());
                }
            }else{//追加parent key
                if(value instanceof LinkedHashMap){
                    iteratorYml((Map)value, parentKey+"."+ key.toString());
                }else{
                    resultMap.put(parentKey+"."+key.toString(), value==null?"":value.toString());
                }
            }
        }
    }
}
