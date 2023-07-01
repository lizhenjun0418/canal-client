## 一、canal 客户端 canal client

### 介绍

* Canal是阿里巴巴mysql数据库binlog的增量订阅&消费组件  
* 使用该客户端前请先了解Canal, https://github.com/alibaba/canal  
* Canal 自身提供了简单的客户端，如果要转换为数据库的实体对象，处理消费数据要每次进行对象转换。
* 该客户端直接将Canal的数据原始类型转换为各个数据表的实体对象，并解耦数据的增删改操作，方便给业务使用。
* 在使用该客户端前，需要先部署好Canal-server端，具体部署步骤请参考[Canal服务端部署](<#canan_server>)。

### 要求

* java8+

### 特性

* 解耦单表增删操作
* simple,cluster,zookeeper,kafka客户端支持
* 同步异步处理支持
* spring boot 开箱即用

### 如何使用

spring boot 方式
maven 依赖

```xml
  <dependency>
      <groupId>com.lizhenjun</groupId>
      <artifactId>canal-spring-boot-starter</artifactId>
      <version>1.0.0-RELEASE</version>
  </dependency>
```

java 方式

```xml

<dependency>
    <groupId>com.lizhenjun</groupId>
    <artifactId>canal-client</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

配置说明

|属性|描述|默认值|
|:----    |:---------------------    |:------- |
|canal.mode |canal 客户端类型 目前支持4种类型 simple,cluster,zk,kafka(kafka 目前支持flatMessage 格式)|simple
|canal.filter| canal过滤的表名称，如配置则只订阅配置的表|""
|canal.batch-size| 消息的数量，超过该次数将进行一次消费 |1(个)
|canal.timeout  |消费的时间间隔(s)|1s
|canal.server     |服务地址,多个地址以,分隔 格式 ${host}:${port}|null
|canal.destination |canal 的instance 名称,kafka模式为topic 名称|null
|canal.user-name     |canal 的用户名    |null
|canal.password |canal 的密码     |null
|canal.group-id  |kafka groupId 消费者订阅消息时可使用，kafka canal 客户端 |null
|canal.async |是否是异步消费，异步消费时，消费时异常将导致消息不会回滚，也不保证顺序性 |true
|canal.partition |kafka partition |null

### 订阅数据库的增删改操作

实现EntryHandler<T> 接口，泛型为想要订阅的数据库表的实体对象，
该接口的方法为 java 8 的 default 方法，方法可以不实现，如果只要监听增加操作，只实现增加方法即可  
下面以一个t_user表的user实体对象为例,
默认情况下，将使用实体对象的jpa 注解 @Table中的表名来转换为EntryHandler中的泛型对象，

```java
public class UserHandler implements EntryHandler<User>{

}
```

如果实体类没有使用jpa @Table的注解，也可以使用@CanalTable 注解在EntryHandler来标记表名，例如

```java
@CanalTable(value = "t_user")
@Component
public class UserHandler implements EntryHandler<User>{

   /**
    *  新增操作
    * @param user
    */
   @Override
    public void insert(User user) {
	   //你的逻辑
        log.info("新增 {}",user);
    }
    /**
     * 对于更新操作来讲，before 中的属性只包含变更的属性，after 包含所有属性，通过对比可发现那些属性更新了
     * @param before
     * @param after
    */
    @Override
    public void update(User before, User after) {
    	//你的逻辑
        log.info("更新 {} {}",before,after);
    }
    /**
    *  删除操作
    * @param user
    */
    @Override
    public void delete(User user) {
       //你的逻辑
        log.info("删除 {}",user); 
   }

    /**
     * ddl语句执行
     * @Param sql
     * @Return void
     */
    default void ddl(String sql) {
        log.info("ddl sql {}",sql);
    }
}
```

另外也支持统一的处理@CanalTable(value="all"),这样除去存在EntryHandler的表以外，其他所有表的处理将通过该处理器,统一转为Map<String, String>对象

```java
@CanalTable(value = "all")
@Component
public class DefaultEntryHandler implements EntryHandler<Map<String, String>> {

    /**
     *  新增操作
     * @param map
     */
     @Override
     public void insert(Map<String, String> map) {
         logger.info("insert message  {}", map);
     }

    /**
     * 对于更新操作来讲，before 中的属性只包含变更的属性，after 包含所有属性，通过对比可发现那些属性更新了
     * @param before
     * @param after
     */
     @Override
     public void update(Map<String, String> before, Map<String, String> after) {
         logger.info("update before {} ", before);
         logger.info("update after {}", after);
     }

    /**
     *  删除操作
     * @param map
     */
     @Override
     public void delete(Map<String, String> map) {
         logger.info("delete  {}", map);
     }
     
    /**
     * ddl语句执行
     * @Param sql
     * @Return void
     */
    default void ddl(String sql) {
        log.info("ddl sql {}",sql);
    }
}
```

如果你想获取除实体类信息外的其他信息，可以使用

```java
CanalModel canal = CanalContext.getModel();
```

具体使用可以查询项目demo 示例  
https://github.com/lizhenjun0418/canal-client/tree/main/canal-example

### 二、<a id="canan_server">Canal服务端部署</a>

#### 1.配置mysql，开启binlog功能
* 开启mysql的binlog功能，并配置binlog模式为row。
* 在my.cnf 加入如下：
* [mysqld]
* log-bin=mysql-bin #添加这一行就ok
* binlog-format=ROW #选择row模式
* server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复
* 数据库重启后, 简单测试 my.cnf 配置是否生效:
```html
mysql> show variables like 'binlog_format';
  +---------------+-------+
  | Variable_name | Value |
  +---------------+-------+
  | binlog_format | ROW   |
  +---------------+-------+
  mysql> show variables like 'log_bin';
  +---------------+-------+
  | Variable_name | Value |
  +---------------+-------+
  | log_bin       | ON    |
  +---------------+-------+
```
#### 增加mysql账号
* 在mysql数据库创建一个专门的账号，提供给canal-server使用。
```html
-- 创建账号
CREATE USER canal IDENTIFIED BY 'canal'; 
-- 授权
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;
```

#### 部署canal-server
* 下载 https://github.com/alibaba/canal/releases/download/canal-1.1.6/canal.deployer-1.1.6.tar.gz
* 解压 tar -zxvf canal.deployer-1.1.6.tar.gz -C /data/canal-server/
* 修改 /data/canal-server/conf/example/instance.properties 文件
```html
#################################################

# position info，需要改成自己的数据库信息
canal.instance.master.address = 127.0.0.1:3306

# username/password，需要改成自己的数据库信息
canal.instance.dbUsername = canal
canal.instance.dbPassword = canal
canal.instance.defaultDatabaseName = 
canal.instance.connectionCharset = UTF-8

# table regex
canal.instance.filter.regex = canal_test\\..*

#################################################
```

* 设置table filter规则方式为：
```html
mysql 数据解析关注的表，Perl正则表达式.
多个正则之间以逗号(,)分隔，转义符需要双斜杠(\\) 
常见例子：
1.  所有表：.*   or  .*\\..*
2.  canal schema下所有表： canal\\..*
3.  canal下的以canal打头的表：canal\\.canal.*
4.  canal schema下的一张表：canal.test1
5.  多个规则组合使用：canal\\..*,mysql.test1,mysql.test2 (逗号分隔)
```

* 启动： /data/canal-server/bin/startup.sh
* 验证启动状态，查看log文件: vim canal/log/canal/canal.log
```html
2023-06-23 18:03:24.044 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## set default uncaught exception handler
2023-06-23 18:03:24.052 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## load canal configurations
2023-06-23 18:03:24.062 [main] INFO  com.alibaba.otter.canal.deployer.CanalStarter - ## start the canal server.
2023-06-23 18:03:24.104 [main] INFO  com.alibaba.otter.canal.deployer.CanalController - ## start the canal server[172.21.0.1(172.21.0.1):11111]
2023-06-23 18:03:25.675 [main] INFO  com.alibaba.otter.canal.deployer.CanalStarter - ## the canal server is running now ......
```