package com.yibo.miaosha.dao;

import com.yibo.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MiaoshaUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoshaUser record);

    int insertSelective(MiaoshaUser record);

    MiaoshaUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoshaUser record);

    int updateByPrimaryKey(MiaoshaUser record);

    void addUsers(@Param("list") List<MiaoshaUser> list);

    void initGoods();

    void initMiaoshaGoods();

    void initOrder();

    void initMiaoshaOrder();

    void initUser();
}