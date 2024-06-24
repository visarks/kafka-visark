package com.podigua.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 **/
public class Test6 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.24.128.53:32023,172.24.128.52:32023,172.24.128.54:32023");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"abc\" password=\"3edc$RFV\";");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        String order ="{\n" +
                "    \"AlarmEvent\": {\n" +
                "        \"isUniSend\": 1,\n" +
                "        \"faultPhenomenon\": \"告警标题：[EPON告警]PON信号丢失(ONU掉线)\\\\r\\\\n发生时间：2024-05-08 15:20:52\\\\r\\\\n网元名称：南京市江宁区谷尚机房中兴OLT/C300/01(11.0.6.94)(11.0.6.94)\\\\r\\\\n网元类型：OLT\\\\r\\\\n告警对象名称：11.0.6.94\\\\r\\\\n告警对象类型：ONU\\\\r\\\\n定位信息描述：\\\\r\\\\n地市名称：南京市\\\\r\\\\n所属传输系统：\\\\r\\\\n\",\n" +
                "        \"orderType\": 1,\n" +
                "        \"agentMan\": \"刘铜强16601334461\",\n" +
                "        \"faultLevel\": 1,\n" +
                "        \"sheetLabel\": \"南京市:【11.0.6.94】发生[EPON告警]PON信号丢失(ONU掉线)(一级告警)\",\n" +
                "        \"senderName\": \"江苏综合监控(13022502000)\",\n" +
                "        \"senderId\": \"zhjk_js\",\n" +
                "        \"alarmId\": \"NEW_BOCO_WNMS_2054426556_1900856007_1859246830_2353586585\",\n" +
                "        \"copyManId\": \"CJ-liutongqiang\",\n" +
                "        \"eventTime\": \"2024-05-08 15:20:52\",\n" +
                "        \"alarmStaId\": \"16455064178183435312688872609811315860\",\n" +
                "        \"dealTimeLimit\": \"2024-05-09 16:37:55\",\n" +
                "        \"copyManMobile\": \"16601334461\",\n" +
                "        \"networkType\": \"1\",\n" +
                "        \"acceptManId\": \"CJ-liutongqiang\",\n" +
                "        \"smsToUserId\": \"CJ-liutongqiang\",\n" +
                "        \"sheetNo\": \"JS网调【2024】网络故障0508-1733701\",\n" +
                "        \"woSource\": \"0\",\n" +
                "        \"sheetCreateTime\": \"2024-05-08 16:37:55\",\n" +
                "        \"agentManId\": \"CJ-liutongqiang\",\n" +
                "        \"copyMan\": \"刘铜强16601334461\",\n" +
                "        \"agentManMobile\": \"16601334461\",\n" +
                "        \"alarmProvince\": \"江苏省\",\n" +
                "        \"acceptMan\": \"刘铜强16601334461\",\n" +
                "        \"professionalType\": 10,\n" +
                "        \"acceptManMobile\": \"16601334461\",\n" +
                "        \"smsToUserName\": \"刘铜强(16601334461)\",\n" +
                "        \"oriAlarmId\": \"\"\n" +
                "    },\n" +
                "    \"TimeStamp\": \"2024-05-08 16:37:59\",\n" +
                "    \"InterfaceType\": 3,\n" +
                "    \"SystemName\": \"网络故障3.0\",\n" +
                "    \"OperationType\": 13\n" +
                "}";
        ProducerRecord<String, String> record = new ProducerRecord<>("recv-workorder-emos", order);
        Future<RecordMetadata> future = producer.send(record);
        future.get();
        System.out.println("发送消息:" + order);
    }
}
