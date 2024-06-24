package com.podigua.kafka;

/**
 *
 **/
public class Main {
    public static void main(String[] args) {
        String str="<eddy xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "            xmlns=\"http://www.lxjava.com/xml\"\n" +
                "            xsi:schemaLocation=\"http://www.lxjava.com/xml  ../xsd/resource.xsd\">\n" +
                "    <jdbc name=\"fims\">-->\n" +
                "       <jdbc-url>jdbc:mysql://192.168.1.146:3306/fims?createDatabaseIfNotExist=true&amp;COLLATE=utf8_general_ci&amp;useUnicode=true&amp;autoReconnect=true&amp;characterEncoding=UTF-8&amp;useOldAliasMetadataBehavior=true&amp;useSSL=false&amp;serverTimezone=Asia/Shanghai</jdbc-url>\n" +
                "       <username>fims</username>\n" +
                "       <password>1qaz@WSX</password>\n" +
                "       <driverClassName>com.mysql.jdbc.Driver</driverClassName>\n" +
                "    </jdbc>\n" +
                "    <kafka name=\"kafka\">\n" +
                "      <bootstrap-servers>192.168.1.152:9092</bootstrap-servers>\n" +
                "    </kafka>\n" +
                "</eddy>";

        System.out.println(str.replace("\n","").replace("\"","\\\""));
    }
}
