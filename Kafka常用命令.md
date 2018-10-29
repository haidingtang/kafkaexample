## Kafka常用命令

#### kafka启动命令
```
# 指定对应的配置文件启动
kafka-server-start.sh /hadoop/kafka/config/server.properties

# -daemon 以后台的方式启动
kafka-server-start.sh -daemon /hadoop/kafka/config/server.properties

# 指定JMX port端口启动，指定jmx，可以方便监控kafka集群
JMX_PORT=9991 kafka-server-start.sh -daemon /hadoop/kafka/config/server.properties
```

#### kafka停止命令
```
kafka-server-stop.sh

或者

kill -9 pid
```

---

#### kafka的topic相关命令

```
# 创建topic
kafka-topics.sh --zookeeper server01:2181 --create --topic test --replication-factor 1 --partitions 3 

# 删除topic
kafka-topics.sh --zookeeper server01:2181 --delete --topic test

# 修改topic的分区，注意：分区数量只能增加，不能减少
kafka-topics.sh --zookeeper server01:2181 --alter --topic test --partitions 5

# 查看所有topic
kafka-topics.sh --zookeeper server01:2181 --list

# 查看所有topic的详细信息
kafka-topics.sh --zookeeper server01:2181 --describe

# 查看指定topic的详细信息
kafka-topics.sh --zookeeper server01:2181 --describe --topic test

# describe命令还提供一些参数，用于过滤输出结果，如：
# --topic-with-overrides：可以找出所有包含覆盖配置的主题，它只会列出包含与集群不一样配置的主题
kafka-topics.sh --zookeeper server01:2181 --describe --topics-with-overrides

# describe有两个参数用于找出有问题的分区 
# --unavailable-partitions：列出所有没有首领的分区，这些分区已经处于离线状态，对于生产者和消费者来说是不可用的
# --under-replicated-partitions：列出所有包含不同步副本的分区。
kafka-topics.sh --zookeeper server01:2181 --describe --unavailable-partitions
kafka-topics.sh --zookeeper server01:2181 --describe --under-replicated-partitions
```

#### 删除topic的操作

1.如果kafka配置delete.topic.enable=true，那么可以直接删除topic，执行删除topic命令

```
kafka-topics.sh --zookeeper server01:2181 --delete --topic test
```

2.如果kafka配置delete.topic.enable=false，删除操作如下：

```
kafka-topics.sh --zookeeper server01:2181 --delete --topic test
```

出现提示：

```
Topic test is marked for deletion.
Note: This will have no impact if delete.topic.enable is not set to true.
```
这里只是标记删除，并没有删除数据，同时也不能往这个topic写入数据

手动删除topic信息，具体操作如下：

```
# 1. 打开zkCli
zkCli.sh -server server01:2181

# 2. 删除zookeeper的topic相关信息

# 删除topic test的consumer group，如果有消费记录的话
rmr /consumers/test-group

rmr /config/topics/test
rmr /brokers/topics/test
rmr /admin/delete_topics/test

# 3. 在每台机器上，删除topic的log文件
rm -rf /hadoop/kafka/logs/test-*

# 4.重新启动kafka集群
kafka-server-stop.sh
kafka-server-start.sh -daemon /hadoop/kafka/config/server.properties
```
---


#### 生产者shell命令

```
kafka-console-producer.sh --broker-list server01:9092 --topic test
```

#### 消费者shell命令

```
kafka-console-consumer.sh --zookeeper server01:2181 --from-beginning --topic test

# 指定group消费组
kafka-console-consumer.sh --zookeeper server01:2181 --from-beginning --group test_group --topic test
```

#### 消费者群组命令

```
# 列出所有消费者群组，相当于zkCli客户端执行 ls /consumer
kafka-consumer-groups.sh --zookeeper server01:2181 --list


# 列出test_group消费组的详细信息
kafka-consumer-groups.sh --zookeeper  server01:2181 --describe --group test_group

# 输出结果
TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     
test            0          8               8               0               -               
test            1          8               8               0               -               
test            2          8               8               0               - 

# CURRENT-OFFSET: 当前消费者群组最近提交的offset，也就是消费者分区里读取的当前位置
# LOG-END-OFFSET: 当前最高水位偏移量，也就是最近一个读取消息的偏移量，同时也是最近一个提交到集群的偏移量
# LAG：消费者的CURRENT-OFFSET与broker的LOG-END-OFFSET之间的差距


# 删除消费者群组
kafka-consumer-groups.sh --zookeeper server01:2181 --delete --group test_group

# 删除消费者群组中的topic
kafka-consumer-groups.sh --zookeeper server01:2181 --delete --group test_group --topic test
```

#### kafka-run-class命令

```
# 导出偏移量
kafka-run-class.sh kafka.tools.ExportZkOffsets --zkconnect server01:2181 --group testGroup --output-file test_group_offsets

# 查看test_group_offsets
cat test_group_offsets

# 结果
/consumers/testGroup/offsets/test_java/0:60
/consumers/testGroup/offsets/test_java/1:12
/consumers/testGroup/offsets/test_java/2:33


# 导入偏移量
kafka-run-class.sh kafka.tools.ImportZkOffsets --zkconnect server01:2181 --input-file test_group_offsets


# segment片段信息
kafka-run-class.sh kafka.tools.DumpLogSegments --files /hadoop/kafka/logs/test-1/00000000000000000000.log

# segment片段信息 -print-data-log
kafka-run-class.sh kafka.tools.DumpLogSegments --files /hadoop/kafka/logs/test-1/00000000000000000000.log -print-data-log

# 输出结果
Starting offset: 0
offset: 0 position: 0 CreateTime: 1531535576273 isvalid: true keysize: -1 valuesize: 5 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: world
offset: 1 position: 73 CreateTime: 1531535921119 isvalid: true keysize: -1 valuesize: 4 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: lead
offset: 2 position: 145 CreateTime: 1531535928792 isvalid: true keysize: -1 valuesize: 4 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: java
offset: 3 position: 217 CreateTime: 1531535933287 isvalid: true keysize: -1 valuesize: 1 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: f
offset: 4 position: 286 CreateTime: 1531535940595 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: la
offset: 5 position: 356 CreateTime: 1531535944164 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: lf
offset: 6 position: 426 CreateTime: 1531535949468 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: lo
offset: 7 position: 496 CreateTime: 1531536196524 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: ll
offset: 8 position: 566 CreateTime: 1531547074348 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: lo
offset: 9 position: 636 CreateTime: 1531547078872 isvalid: true keysize: -1 valuesize: 3 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: pop
offset: 10 position: 707 CreateTime: 1531547082156 isvalid: true keysize: -1 valuesize: 2 magic: 2 compresscodec: NONE producerId: -1 producerEpoch: -1 sequence: -1 isTransactional: false headerKeys: [] payload: ls

```
