package com.podigua.kafka.visark.cluster.enums;

/**
 * 机制
 *
 * @author podigua
 * @date 2024/04/01
 */
public enum Mechanism {
    /**
     * PLAIN
     */
    PLAIN("PLAIN"),
    /**
     * SCRAM-SHA-256
     */
    SCRAM_SHA_256("SCRAM-SHA-256"),
    /**
     * SCRAM-SHA-512
     */
    SCRAM_SHA_512("SCRAM-SHA-512");

    private final String label;

    Mechanism(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
