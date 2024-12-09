package com.podigua.kafka.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Excel 实用程序
 *
 * @author podigua
 * @date 2024/12/05
 */

public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private final static BigDecimal _256 = new BigDecimal("256");
    private final static BigDecimal _6 = new BigDecimal("6");
    private final static Integer _255X256 = 255 * 256;

    /**
     * 像素转换
     *
     * @param width
     * @return
     */
    public static Integer getPixel(Integer width) {
        Integer result = _256.multiply(new BigDecimal(width)).divide(_6, RoundingMode.CEILING).intValue();
        if (result < _255X256) {
            return result;
        } else {
            return _255X256;
        }
    }


}
