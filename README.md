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

## SpringBoot不继承父parent如何打可运行的jar包？
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.junbaor.test.App</mainClass>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Mybatis插入null改为空字符串
```xml
insert指定列，如果value为null，表字段的默认值不会生效导致报错。
sql里可以参考mysql的ifnull函数ifnull(#{userName},'')，oracle对应nvl()
<insert id="insert" parameterType="com.github.xjs.springbootdemo.dao.Users">
    insert into users (id, user_name)
    values (#{id}, ifnull(#{userName},''))
</insert>
```

## 手动deploy jar包到maven私服
```xml
mvn deploy:deploy-file -DgroupId=*** -DartifactId=***  -Dversion=***  -Dpackaging=jar -Dfile="***.jar" -Dsources="***-sources.jar"  -Durl=http://***/nexus/content/repositories/releases/ -DrepositoryId=nexus-release --settings D:\apache-maven-3.6.3\conf\settings.xml
```

## EmEditor删除空行
```sh
正则替换：^[\s\t]*\n
```
