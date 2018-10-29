## Kafka Manager的使用

#### 1.安装

Kafka Manager是雅虎开发的一个基于Web的kafka管理工具。具有非常强大的功能。

kafka manager项目地址：++https://github.com/yahoo/kafka-manager++

**下载编译kafka manager**

```
# 从git上下载Kafka manager源码
git clone https://github.com/yahoo/kafka-manager

# 使用sbt进行编译，编译过程中需要等待很长时间
stb clean dist
```
编译完成会生成一个kafka-manager-1.3.x.x.zip的压缩包，或者我们可以直接从网上下载一个`kafka-manager-xxxx.zip`压缩包。

[kafka-manager-1.3.3.15.zip](https://download.csdn.net/download/weixin_41582312/10186839)

[kafka-manager-1.3.3.7.zip](https://download.csdn.net/download/jiu123ba/9937296)

**解压**

```
# 进入kafka manager的zip包目录，解压
unzip kafka-manager-1.3.3.7.zip
```
解压后的目录

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-1.jpeg)

**修改配置文件`./conf/application.conf`**

```
# 默认是localhost，将zkhosts改为自己zk集群的hosts
kafka-manager.zkhosts="localho:2181"
```

#### 2.启动

**启动zookeeper和kafka**

```
# 启动zookeeper
zkServer start

# 启动kafka，JMX_PORT=9991指定Kafka的JMX端口为9991，方便对kafka监控
JMX_PORT=9991 kafka-server-start -daemon /usr/local/etc/kafka/server.properties
```

**启动Kafka Manager**
进入kafka manager的解压目录下的bin目录，执行kafka-manager命令。

```
# 进入/kafka-manager/bin
cd ../kafka-manager/bin

# 执行kafka-manager命令
sh kafka-manager
```
启动后会在控制台输出一些log信息，在浏览器中输入：`localhost:9000`，就会出现一个Kafka Manager的管理界面。

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-2.jpeg)

#### 3.UI界面的介绍

**添加Cluster**

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-3.jpeg)

**topic Summary页面和topic的list页面**

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-4.jpeg)

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-5.jpeg)

**topic的详情页面**

topic的详情页面，可以对topic进行重新分区操作，删除topic操作，更新配置操作等等。

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-6.jpeg)

topic删除操作，如果在server.properties设置delete.topic.enable=true，可以直接删除。如果设置成false，topic通过Delete Topic按钮删除，实际是不能删除的。

**Broker信息页面**

![image](https://raw.githubusercontent.com/zhang3550545/image_center/master/image-2018/kafka-manager-7.jpeg)

