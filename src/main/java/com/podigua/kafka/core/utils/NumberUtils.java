package com.podigua.kafka.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数值工具类
 *
 * @author podigua
 * @date 2024/12/13
 */
public class NumberUtils {

    /**
     * 默认精度
     */
    public final static int DEFAULT_SCALE = 2;
    /**
     * 一
     */
    public final static BigDecimal ONE = BigDecimal.ONE;
    /**
     * 十
     */
    public final static BigDecimal TEN = new BigDecimal("10");
    /**
     * 百
     */
    public final static BigDecimal HUNDRED = new BigDecimal("100");
    /**
     * 千
     */
    public final static BigDecimal THOUSAND = new BigDecimal("1000");
    /**
     * 万
     */
    public final static BigDecimal TEN_THOUSAND = new BigDecimal("10000");
    /**
     * 百万
     */
    public final static BigDecimal MILLION = new BigDecimal("1000000");
    /**
     * 亿
     */
    public final static BigDecimal A_HUNDRED_MILLION = new BigDecimal("100000000");

    /**
     * 除法
     * dividend ÷ divisor
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    规模
     * @return {@link BigDecimal }
     */
    public static BigDecimal divide(Number dividend, Number divisor, int scale) {
        if (dividend == null || divisor == null) {
            return null;
        }
        BigDecimal total = new BigDecimal(divisor.toString());
        if(BigDecimal.ZERO.compareTo(total)==0){
            return BigDecimal.ZERO;
        }
        BigDecimal result = new BigDecimal(dividend.toString()).divide(total, scale, RoundingMode.HALF_UP);
        return result.setScale(Math.min(result.stripTrailingZeros().scale(), scale), RoundingMode.HALF_UP);
    }

    /**
     * 保留最小精度
     *
     * @param decimal 十进制
     * @param scale   规模
     * @return {@link BigDecimal }
     */
    public static BigDecimal minScale(BigDecimal decimal, int scale) {
        BigDecimal value = new BigDecimal(decimal.stripTrailingZeros().toPlainString());
        return value.setScale(Math.min(value.scale(), scale), RoundingMode.HALF_UP);
    }
}
