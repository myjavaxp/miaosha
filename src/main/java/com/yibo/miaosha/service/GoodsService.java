package com.yibo.miaosha.service;

import com.yibo.miaosha.dao.GoodsMapper;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoods();
    }

    public GoodsVo getGoodsVoById(long id) {
        return goodsMapper.getGoodsVoById(id);
    }
}
