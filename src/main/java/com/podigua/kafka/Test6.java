package com.podigua.kafka;

import com.podigua.kafka.core.utils.UUIDUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 *
 **/
public class Test6 {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(props);
        while (true){
            String uuid = UUIDUtils.uuid();
            ProducerRecord<String,String> record=new ProducerRecord<>("test", uuid);
            producer.send(record);
            System.out.println("发送消息:"+uuid);
        }
    }
}
