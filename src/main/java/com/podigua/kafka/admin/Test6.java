package com.podigua.kafka.admin;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 **/
public class Test6 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-group");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<TopicPartition> tps = Optional.ofNullable(consumer.partitionsFor(
                            "test"
                    ))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(info -> new TopicPartition(info.topic(), info.partition()))
                    .collect(Collectors.toList());
            Map<TopicPartition, Long> beginOffsets = consumer.beginningOffsets(tps);
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(tps);
            System.out.println(beginOffsets);
            System.out.println(endOffsets);
            System.out.println(tps.stream().mapToLong(tp -> endOffsets.get(tp) - beginOffsets.get(tp)).sum());
        }
    }
}
