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

## MySQL相关
- 1.Mybatis插入null改为空字符串
```xml
insert指定列，如果value为null，表字段的默认值不会生效导致报错。
sql里可以参考mysql的ifnull函数ifnull(#{userName},'')，oracle对应nvl()
<insert id="insert" parameterType="com.github.xjs.springbootdemo.dao.Users">
    insert into users (id, user_name)
    values (#{id}, ifnull(#{userName},''))
</insert>
```
- 2.mysql大分页优化
```sql
# mysql的查询完全命中索引的时候,称为覆盖索引,是非常快的,因为查询只需要在索引上进行查找,之后可以直接返回,而不用再回数据表拿数据.
# 因此我们可以先查出索引的ID,然后根据Id拿数据
select * from (select id from job limit 1000000,100) a left join job b on a.id = b.id;
```

- 3.insert on duplicate key update
```xml
<insert id="insertOnDuplicateKeyUpdate"
            parameterType="com.github.xjs.domain.UserEntity" useGeneratedKeys="true" keyProperty="id" >
  insert into user_entity
  <trim prefix="(" suffix=")" suffixOverrides=",">
    <if test="name != null">name,</if>
    <if test="age != null">age,</if>
  </trim>
  <trim prefix="values (" suffix=")" suffixOverrides=",">
    <if test="name != null">#{name,jdbcType=VARCHAR},</if>
    <if test="age != null">#{age,jdbcType=INTEGER},</if>
  </trim>
  <trim prefix="ON DUPLICATE KEY UPDATE" suffixOverrides=",">
    <if test="name != null">name = #{name,jdbcType=VARCHAR},</if>
    <if test="age != null">age = #{age,jdbcType=INTEGER},</if>
  </trim>
</insert>
<!-- 
1. 如果你插入的记录导致一个UNIQUE索引或者primary key(主键)出现重复，那么就会认为该条记录存在，则执行update语句而不是insert语句，反之，则执行insert语句而不是更新语句。
2. 所以 ON DUPLICATE KEY UPDATE是不能写where条件的
3. 如果插入了一个新行，则受影响的行数是1，如果修改了已存在的一行数据，则受影响的行数是2，如果值不变，则受影响行数是0
-->
```

- 4.insert on duplicate key update容易引发死锁
替换的办法:
```java
try{
    insert();
}catch(DuplicateKeyExceprion e){
    update();
}
```

## 手动deploy jar包到maven私服
```xml
mvn deploy:deploy-file -DgroupId=*** -DartifactId=***  -Dversion=***  -Dpackaging=jar -Dfile="***.jar" -Dsources="***-sources.jar"  -Durl=http://***/nexus/content/repositories/releases/ -DrepositoryId=nexus-release --settings D:\apache-maven-3.6.3\conf\settings.xml
```

## EmEditor删除空行
```sh
正则替换：^[\s\t]*\n
```

## EmEditor json格式化- 添加宏
```js
document.selection.SelectAll();
document.selection.Text = JSON.stringify(JSON.parse(document.selection.Text), null, '\t');
```

## SpringBoot相关
- 1.日期处理
```java
@JsonComponent
public class DateConfigure {
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder() {
        return builder -> {
            TimeZone tz = TimeZone.getTimeZone("GMT+8");
            DateFormat df = new SimpleDateFormat(pattern);
            df.setTimeZone(tz);
            builder.failOnEmptyBeans(false)
                    .failOnUnknownProperties(false)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .dateFormat(df);
        };
    }
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)));
    }
}
//如果有特殊格式，可以用@JsonFormat定制：
@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
//还可以这样
@JsonSerialize(using = DateToStringSerializer.class)
public class DateToStringSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (date != null) {
            jsonGenerator.writeString(DateUtil.format(date,DateUtil.FORMAT_YMDHMS));
        } else {
            jsonGenerator.writeNull();
        }
    }
}
```
- 2.Long统一转String返回
```java
@Configuration
public class WebConfigure implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        //不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        //放到第一个
        converters.add(0, jackson2HttpMessageConverter);
    }
}
// 或者可以这样
@JsonSerialize(using = LongToStringSerializer.class)
public class LongToStringSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(value.toString());
        } else {
            gen.writeNull();
        }
    }
}
```
## nginx配置跨域
```xml
server {
         listen  80 default_server;
         server_name _;  
 
         add_header Access-Control-Allow-Credentials true;
         add_header Access-Control-Allow-Origin $http_origin;
         
              
         location /file {
            if ($request_method = 'OPTIONS') {
                add_header Access-Control-Allow-Origin $http_origin;
                add_header Access-Control-Allow-Methods $http_access_control_request_method;
                add_header Access-Control-Allow-Credentials true;
                add_header Access-Control-Allow-Headers $http_access_control_request_headers;
                add_header Access-Control-Max-Age 1728000;
                return 204;
             }         
        }
 
    }
```

## Spring跨域
```java
@Configuration
public class CorsConfig {
 
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //支持cookie 跨域
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setMaxAge(300L);//设置时间有效
 
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```


### macbook安装brew
```sh
/bin/zsh -c "$(curl -fsSL https://gitee.com/cunkai/HomebrewCN/raw/master/Homebrew.sh)"
```

### git操作

- 1.基于tag创建新分支
```bash
git branch sprint-xjs-temp（新分支） sprint-da75a44-20201201164256（已有tag）#创建新分支
git checkout sprint-xjs-temp # 切换到新分支
git push origin sprint-xjs-temp # 提交到服务端
```

- 2.基于commit创建新分支
```bash
git show f58e0955 # 查找完整的cimmit id
git checkout -b temp_xiaoye_210618 f58e0955d9097738d2c26a73be26880a223dc114 # 创建新分支
git push origin temp_xiaoye_210618 # 提交到服务端
```

- 3.分支merge,develop分支merge到master
```bash
git checkout master #切换到master
git merge origin/develop # meger develop
git push origin master # 提交到master
```

- 4.删除分支dev_old 
```bash
git checkout dev_new # 切换到北的分支
git brand -d dev_old # 本地删除分支
git brand -D dev_old # 强制删除本地分支
git push origin --delete dev_old # 远程删除
```


### 虚拟机重启ip总是变动
- 1.进入网卡配置目录
```sh
[root@sc-chenlu ~]# cd /etc/sysconfig/network-scripts/
```
- 2.编辑ifcfg-enxxx网卡的配置文件
```sh
vi ifcfg-eno16777736
修改：BOOTPROTO=none           #none手工指定ip，dhcp 表示虚拟机动态获得ip地址
添加：IPADDR=192.168.136.135   #IPADDR指定具体的ip地址
```
- 3.重新启动网络服务使修改生效
```sh
service  network  restart
```
