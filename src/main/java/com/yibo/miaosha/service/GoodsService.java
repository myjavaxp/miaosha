package com.yibo.miaosha.service;

import com.yibo.miaosha.dao.GoodsMapper;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GoodsService {
    private final GoodsMapper goodsMapper;

    @Autowired
    public GoodsService(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoods();
    }

    public GoodsVo getGoodsVoById(long id) {
        return goodsMapper.getGoodsVoById(id);
    }

    public boolean reduceStock(GoodsVo goods) {
        return goodsMapper.reduceStock(goods) > 0;
    }
}
