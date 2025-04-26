package com.podigua.kafka.core.unit;


public enum DataSizeUnit {


    B("b", DataSize.ofBytes(1)),

    K("kb", DataSize.ofKilobytes(1)),

    M("mb", DataSize.ofMegabytes(1)),

    G("gb", DataSize.ofGigabytes(1)),

    T("tb", DataSize.ofTerabytes(1));


    private final String suffix;

    private final DataSize size;


    DataSizeUnit(String suffix, DataSize size) {
        this.suffix = suffix;
        this.size = size;
    }

    DataSize size() {
        return this.size;
    }

    public static DataSizeUnit fromSuffix(String suffix) {
        for (DataSizeUnit candidate : values()) {
            if (candidate.suffix.equalsIgnoreCase(suffix)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
    }

}
