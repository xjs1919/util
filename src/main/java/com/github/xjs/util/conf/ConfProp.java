package com.github.xjs.util.conf;

import com.github.xjs.util.IOUtil;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Description
 * @Author xujs@mamcharge.com
 * @Date 2019/9/6 19:15
 **/
class ConfProp implements ConfAble {

    private Properties props;

    @Override
    public void init(InputStream in)throws Exception{
        try {
            Properties props = new Properties();
            props.load(in);
            this.props = props;
        }finally{
            IOUtil.closeQuietly(in);
        }
    }

    @Override
    public String get(String key) {
        Object value = this.props.get(key);
        if(value == null){
            return null;
        }
        return value.toString();
    }

}
