package com.podigua.kafka.core.utils;

/**
 *
 **/
public class SystemUtils {
    private final static String OS_NAME = System.getProperty("os.name");
    private final static Boolean IS_ISO_MAC = OS_NAME != null && OS_NAME.startsWith("Mac");

    /**
     * 是 Mac
     *
     * @return {@link Boolean}
     */
    public static Boolean isMac() {
        return IS_ISO_MAC;
    }
}
