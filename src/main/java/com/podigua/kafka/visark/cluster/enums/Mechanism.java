package com.podigua.kafka.visark.cluster.enums;

import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.kafka.clients.admin.ScramMechanism;

/**
 * 机制
 *
 * @author podigua
 * @date 2024/04/01
 * {@link  ScramMechanism}
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

    public static ObservableList<Mechanism> MECHANISM = FXCollections.observableArrayList(
            Mechanism.PLAIN,
            Mechanism.SCRAM_SHA_256,
            Mechanism.SCRAM_SHA_512
    );
}
