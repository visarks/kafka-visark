package com.podigua.kafka.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.podigua.kafka.State;

import java.util.Date;

/**
 * 许可证
 *
 * @author podigua
 * @date 2024/11/11
 */
public class License {
    /**
     * 产品
     */
    private String product = State.PRODUCT;
    /**
     * 版本
     */
    private String version = State.VERSION;
    /**
     * 到期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date expireTime;


    /**
     * 到期
     *
     * @return boolean
     */
    public boolean expire(){
        return expireTime.before(new Date());
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
