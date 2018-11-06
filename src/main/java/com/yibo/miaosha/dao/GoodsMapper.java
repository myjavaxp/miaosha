package com.yibo.miaosha.dao;

import com.yibo.miaosha.domain.Goods;
import com.yibo.miaosha.vo.GoodsVo;

import java.util.List;

public interface GoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<GoodsVo> listGoods();

    GoodsVo getGoodsVoById(long id);

    void reduceStock(GoodsVo goods);
}