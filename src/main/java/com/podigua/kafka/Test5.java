package com.podigua.kafka;

import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 *
 **/
public class Test5 {
    public static void main(String[] args) {
        // 配置Kafka消费者的属性
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // 消费主题
        String topic="test";
        // Create AdminClient
        try (AdminClient adminClient = AdminClient.create(props)) {
            TopicPartition p1=new TopicPartition("test",0);
            TopicPartition p2=new TopicPartition("test",1);
            TopicPartition p3=new TopicPartition("test",2);

            DescribeProducersResult result = adminClient.describeProducers(Lists.of(p1,p2,p3));
            Map<TopicPartition, DescribeProducersResult.PartitionProducerState> map = result.all().get();
            map.forEach((k,v)->{
                System.out.println("topic:"+k.topic()+".partition:"+k.partition());
                List<ProducerState> states = v.activeProducers();
                states.forEach(state->{
                    System.out.println(state);
                });
            });

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
