package com.podigua.kafka.updater.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 信任所有主机名验证程序
 *
 * @author podigua
 * @date 2025/04/03
 */
public class TrustAllHostnameVerifier  implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
