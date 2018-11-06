package com.yibo.miaosha.dao;

import com.yibo.miaosha.domain.MiaoshaOrder;
import org.apache.ibatis.annotations.Param;

public interface MiaoshaOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoshaOrder record);

    int insertSelective(MiaoshaOrder record);

    MiaoshaOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoshaOrder record);

    int updateByPrimaryKey(MiaoshaOrder record);

    MiaoshaOrder getOne(@Param("userId") long userId, @Param("goodsId") long goodsId);
}