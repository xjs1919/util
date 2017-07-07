package com.github.xjs.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by xujiashuai on 2016/9/5.
 */
public class PropertiesLoaderFactory {

    public static ConcurrentHashMap<String, PropertiesLoader> propertiesHolder = new ConcurrentHashMap<String, PropertiesLoader>();

    private PropertiesLoaderFactory(){}

    public static PropertiesLoader getPropertyLoader(String resourcesPath){
    	PropertiesLoader propertiesLoader =  propertiesHolder.get(resourcesPath);
        if(propertiesLoader == null){
            synchronized(PropertiesLoaderFactory.class){
                propertiesLoader =  propertiesHolder.get(resourcesPath);
                if(propertiesLoader == null){
                    Properties properties = loadProperties(resourcesPath);
                    propertiesLoader = new PropertiesLoader(properties);
                    propertiesHolder.put(resourcesPath, propertiesLoader);
                }
            }
        }
        return propertiesLoader;
    }
    
    private static Properties loadProperties(String location) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = PropertiesLoaderFactory.class.getClassLoader().getResourceAsStream(location);
            props.load(is);
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(is);
        }
        return props;
    }
    
    public static class PropertiesLoader{
    	private Properties properties;
    	private PropertiesLoader(Properties properties){
            this.properties = properties;
        }
    	private String getValue(String key) {
            String systemProperty = System.getProperty(key);
            return systemProperty != null?systemProperty:this.properties.getProperty(key);
        }

        public String getString(String key) {
            String value = this.getValue(key);
            if(value == null) {
                throw new NoSuchElementException();
            } else {
                return value;
            }
        }

        public String getString(String key, String defaultValue) {
            String value = this.getValue(key);
            return value != null?value:defaultValue;
        }

        public Integer getInteger(String key) {
            String value = this.getValue(key);
            if(value == null) {
                throw new NoSuchElementException();
            } else {
                return Integer.valueOf(value);
            }
        }

        public Integer getInteger(String key, Integer defaultValue) {
            String value = this.getValue(key);
            return value != null?Integer.valueOf(value):defaultValue;
        }

        public Double getDouble(String key) {
            String value = this.getValue(key);
            if(value == null) {
                throw new NoSuchElementException();
            } else {
                return Double.valueOf(value);
            }
        }

        public Double getDouble(String key, Double defaultValue) {
            String value = this.getValue(key);
            return Double.valueOf(value != null?Double.valueOf(value).doubleValue():(double)defaultValue.intValue());
        }

        public Float getFloat(String key, Double defaultValue) {
            String value = this.getValue(key);
            if(value == null) {
                throw new NoSuchElementException();
            } else {
                return Float.valueOf(value);
            }
        }

        public Float getFloat(String key, Float defaultValue) {
            String value = this.getValue(key);
            return Float.valueOf(value != null?Float.valueOf(value).floatValue():(float)defaultValue.floatValue());
        }

        public Boolean getBoolean(String key) {
            String value = this.getValue(key);
            if(value == null) {
                throw new NoSuchElementException();
            } else {
                return Boolean.valueOf(value);
            }
        }

        public Boolean getBoolean(String key, boolean defaultValue) {
            String value = this.getValue(key);
            return Boolean.valueOf(value != null?Boolean.valueOf(value).booleanValue():defaultValue);
        }
    }
    
    public static void main(String[] args)throws Exception {
    	int timeout = PropertiesLoaderFactory.getPropertyLoader("application.properties").getInteger("fdfs.connect_timeout", 0);
    	System.out.println(timeout);
    	String url = PropertiesLoaderFactory.getPropertyLoader("spring/jdbc.properties").getString("url", "");
    	System.out.println(url);
    }
}
