package com.podigua.kafka;

import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Headers;

import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 **/
public class Test {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Lists.of("test"));
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1));
            if(records!=null){
                int count = records.count();
                if(count>0){
                    System.out.println("----------------------");
                    Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
                    while (iterator.hasNext()){
                        ConsumerRecord<String, String> next = iterator.next();
                        Headers headers = next.headers();
                        String key = next.key();
                        int partition = next.partition();
                        long timestamp = next.timestamp();
                        String topic = next.topic();
                        String value = next.value();
                        System.out.println("headers:"+headers+"key:"+key+"partition:"+partition+"timestamp:"+timestamp+"topic:"+topic+"value:"+value);
                    }
                }

            }

            consumer.close();
            break;
        }
    }
}
