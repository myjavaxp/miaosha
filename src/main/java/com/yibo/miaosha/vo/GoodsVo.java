package com.yibo.miaosha.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yibo.miaosha.domain.Goods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class GoodsVo extends Goods implements Serializable {
    private static final long serialVersionUID = -7632143707200625511L;
    private Integer stockCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private BigDecimal miaoshaPrice;

    public BigDecimal getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(BigDecimal miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
