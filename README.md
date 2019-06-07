# util 

## 微信扫一扫关注公众号：爪哇优太儿
![扫一扫加关注](https://img-blog.csdnimg.cn/20190524100820287.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2dvbGRlbmZpc2gxOTE5,size_16,color_FFFFFF,t_7)

> 常用的一些工具类

## fastjson处理日期 
```java
//第一种
SerializeConfig config = new SerializeConfig();  
config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));  
String str = JSON.toJSONString(u,config);
//第二种
JSONObject.DEFFAULT_DATE_FORMAT="yyyy-MM-dd";
String str = JSON.toJSONString(u,SerializerFeature.WriteDateUseDateFormat);
//第三种
String str = JSON.toJSONStringWithDateFormat(u, "yyyy-MM-dd", SerializerFeature.WriteDateUseDateFormat);

```
