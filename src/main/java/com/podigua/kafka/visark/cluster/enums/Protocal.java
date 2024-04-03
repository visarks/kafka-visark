package com.podigua.kafka.visark.cluster.enums;

/**
 * protocal
 *
 * @author podigua
 * @date 2024/03/31
 */
public enum Protocal {
    PLAINTEXT,
    SSL,
    SASL_PLAINTEXT,
    SASL_SSL;

    @Override
    public String toString() {
        return this.name();
    }
}
