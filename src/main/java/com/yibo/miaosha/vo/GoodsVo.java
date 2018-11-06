package com.yibo.miaosha.vo;

import com.yibo.miaosha.domain.Goods;

import java.math.BigDecimal;
import java.util.Date;

public class GoodsVo extends Goods {
    private Integer stockCount;

    private Date startDate;

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
