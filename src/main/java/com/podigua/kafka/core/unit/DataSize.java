package com.podigua.kafka.core.unit;

import com.podigua.kafka.core.utils.NumberUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataSize implements Comparable<DataSize> {
    private static final Pattern PATTERN = Pattern.compile("^([+\\-]?(\\d+(\\.\\d*)?|\\.\\d+))([a-zA-Z]{0,2})$");
    /**
     * KB
     */
    private static final long BYTES_PER_KB = 1024;

    /**
     * MB
     */
    private static final long BYTES_PER_MB = BYTES_PER_KB * 1024;

    /**
     * GB
     */
    private static final long BYTES_PER_GB = BYTES_PER_MB * 1024;
    /**
     * TB
     */
    private static final long BYTES_PER_TB = BYTES_PER_GB * 1024;

    private long bytes;

    private DataSize(long bytes) {
        this.bytes = bytes;
    }

    public long toBytes() {
        return this.bytes;
    }

    public static DataSize ofBytes(long bytes) {
        return new DataSize(bytes);
    }

    /**
     * 千字节
     *
     * @param kilobytes 千 字节
     * @return {@link DataSize }
     */
    public static DataSize ofKilobytes(long kilobytes) {
        return new DataSize(Math.multiplyExact(kilobytes, BYTES_PER_KB));
    }

    /**
     * 兆字节
     *
     * @param megabytes 兆 字节
     * @return {@link DataSize }
     */
    public static DataSize ofMegabytes(long megabytes) {
        return new DataSize(Math.multiplyExact(megabytes, BYTES_PER_MB));
    }

    /**
     * GB
     *
     * @param gigabytes 千兆字节
     * @return {@link DataSize }
     */
    public static DataSize ofGigabytes(long gigabytes) {
        return new DataSize(Math.multiplyExact(gigabytes, BYTES_PER_GB));
    }

    /**
     * TB 数
     *
     * @param terabytes TB 级
     * @return {@link DataSize }
     */
    public static DataSize ofTerabytes(long terabytes) {
        return new DataSize(Math.multiplyExact(terabytes, BYTES_PER_TB));
    }

    /**
     * 之
     *
     * @param amount 量
     * @param unit   单位
     * @return {@link DataSize }
     */
    public static DataSize of(Number amount, DataSizeUnit unit) {
        Assert.notNull(amount, "amount must not be null");
        Assert.notNull(unit, "Unit must not be null");
        long bytes=new BigDecimal(amount.toString()).multiply(new BigDecimal(unit.size().toBytes())).longValue();
        return new DataSize(bytes);
    }


    /**
     * 转为KB
     *
     * @return long
     */
    public long toKilobytes(){
        return toKilobytes(0).longValue();
    }
    /**
     * 转为KB
     *
     * @param scale 精度
     * @return {@link BigDecimal }
     */
    public BigDecimal toKilobytes(int scale) {
        return NumberUtils.divide(this.bytes, BYTES_PER_KB, scale);
    }


    /**
     * 转为MB
     *
     * @return long
     */
    public long toMegabytes(){
        return toMegabytes(0).longValue();
    }
    /**
     * 转为MB
     *
     * @param scale 精度
     * @return {@link BigDecimal }
     */
    public BigDecimal toMegabytes(int scale) {
        return NumberUtils.divide(this.bytes, BYTES_PER_MB, scale);
    }

    /**
     * 转为GB
     *
     * @return long
     */
    public long toGigabytes(){
        return toGigabytes(0).longValue();
    }
    /**
     * 转为GB
     *
     * @param scale 精度
     * @return {@link BigDecimal }
     */
    public BigDecimal toGigabytes(int scale) {
        return NumberUtils.divide(this.bytes, BYTES_PER_GB, scale);
    }


    /**
     * 转为TB
     *
     * @return long
     */
    public long toTerabytes(){
        return toTerabytes(0).longValue();
    }
    /**
     * 转为TB
     *
     * @param scale 精度
     * @return {@link BigDecimal }
     */
    public BigDecimal toTerabytes(int scale) {
        return NumberUtils.divide(this.bytes, BYTES_PER_TB, scale);
    }


    @Override
    public String toString() {
        return toString(0);
    }
    public String toString(int scale) {
        if(this.bytes>=BYTES_PER_TB){
            return toTerabytes(scale).toPlainString()+DataSizeUnit.T.name();
        } else if (this.bytes>=BYTES_PER_GB) {
            return toGigabytes(scale).toPlainString()+DataSizeUnit.G.name();
        }else if (this.bytes>=BYTES_PER_MB) {
            return toMegabytes(scale).toPlainString()+DataSizeUnit.M.name();
        }else if (this.bytes>=BYTES_PER_KB) {
            return toKilobytes(scale).toPlainString()+DataSizeUnit.K.name();
        }
        return this.bytes+DataSizeUnit.B.name();
    }

    /**
     * 解析
     *
     * @param text 发短信
     * @return {@link DataSize }
     */
    public static DataSize parse(CharSequence text) {
        return parse(text, DataSizeUnit.B);
    }


    /**
     * 解析
     *
     * @param text        发短信
     * @param defaultUnit 默认单位
     * @return {@link DataSize }
     */
    public static DataSize parse(CharSequence text, DataSizeUnit defaultUnit) {
        Assert.notNull(text, "Text must not be null");
        try {
            Matcher matcher = PATTERN.matcher(StringUtils.trimAllWhitespace(text));
            Assert.state(matcher.matches(), "Does not match data size pattern");
            String unitString = matcher.group(4);
            DataSizeUnit unit=defaultUnit;
            if(StringUtils.hasText(unitString)){
                unit=DataSizeUnit.fromSuffix(unitString);
            }
            BigDecimal amount = new BigDecimal(matcher.group(1));
            return DataSize.of(amount, unit);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("'" + text + "' is not a valid data size", ex);
        }
    }
    @Override
    public int compareTo(DataSize other) {
        return Long.compare(this.bytes, other.bytes);
    }

}
