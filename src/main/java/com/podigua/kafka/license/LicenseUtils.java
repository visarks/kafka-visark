package com.podigua.kafka.license;

import com.podigua.kafka.core.utils.BeanUtils;
import com.podigua.kafka.core.utils.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 许可证工具类
 *
 * @author podigua
 * @date 2024/11/11
 */
public class LicenseUtils {

    private static  String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8uKLCT8h9Izf/Y63JIWJzcX0BPaezx0EWliNjNv4tgaCdNV95ph6ivdvN7bXiDGktYVlko18aTims12a+o+zxVVCfimdfZ9UmNjuxlWR1f/2AYJBTYw59nzuoxBqYrcWepcwnxYx+YinVlfclmJ8p6a5d7MnZhDouAqg8jbDdq9+FNAracu3dShaLswW4ERrcaDtylgqnTGUtIqwQKXDi/MnatezBKdvmTVOsSoqnNtzj5Yxh0zs51nyCgp3UARoIW9Ze5r2hb85L/B+q/nLIeJ2jFm9FZCtCYtmK/y1CgEuL8NtcgncsFNvGrQTKImqnkVGwCPsID6FqduNx7ACFQIDAQAB";

    public static void main(String[] args) {
        String content1 = encrypt();
        System.out.println(content1);
//        String content = "D52MUZ4nW2+ZuKe0P5sLrfDKJt19hGJnPAa4714aikvlwBVUcPpPIz7zvCTMztpxroZNa7doYqcphynAla32TgFBe1MIeg2nTJtVAM1nVBSZkBahTgE2AxFPFDBhYjnbw3lCaEqymBzgbkFRR/XRIKc6oS2HGiDh0WbcwD4b57YHp05GCEp9Tx/NCb58VKbZsE1ADnQwZvSGrkfKPCSSwm8S7E2Kk+pMBC478UQFu9ZtCOJkjg6IbUsMXXFmlfTBYPrkn1tpHWO0ovtzj/U+GUL/20b9CP21hsRSu4QgJJh+f3yB8bNWDnCqNuo9yKkN3yRsMDZ57D0UGwkAr7cTFQ==";
        License license = decrypt(content1);
        System.out.println(license);
    }

    public static String encrypt() {
        License license = new License();
        license.setExpireTime(addDays(new Date(), 365));
        File file = new File("/Users/podigua/work/software/kafka-visark-private-key.txt");
        String privateKey = FileUtils.read(file);
        return RsaUtils.encryptByPrivateKey(BeanUtils.writeValueAsString(license), privateKey);
    }


    /**
     * 解密
     *
     * @param content 内容
     * @return {@link License }
     */
    public static License decrypt(String content) {
        String license = RsaUtils.decryptByPublicKey(content, PUBLIC_KEY);
        return BeanUtils.readValue(license, License.class);
    }

    /**
     * 添加天数
     *
     * @param date   日期
     * @param amount 量
     * @return {@link Date }
     */
    private static Date addDays(Date date, int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }
}
