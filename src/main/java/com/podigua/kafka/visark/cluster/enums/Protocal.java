package com.podigua.kafka.visark.cluster.enums;

/**
 * protocal
 *
 * @author podigua
 * @date 2024/03/31
 */
public enum Protocal {
    PLAINTEXT,
    SASL_PLAINTEXT;
//    SSL,
//    SASL_SSL;

    @Override
    public String toString() {
        return this.name();
    }
}
