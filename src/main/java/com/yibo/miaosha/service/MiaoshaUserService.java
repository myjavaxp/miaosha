package com.yibo.miaosha.service;

import com.yibo.miaosha.dao.MiaoshaUserMapper;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.exception.GlobalException;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.MiaoshaUserKey;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.util.Md5Util;
import com.yibo.miaosha.util.UUIDUtil;
import com.yibo.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Transactional(readOnly = true)
public class MiaoshaUserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    private final MiaoshaUserMapper miaoshaUserMapper;

    private final RedisService redisService;

    @Autowired
    public MiaoshaUserService(MiaoshaUserMapper miaoshaUserMapper, RedisService redisService) {
        this.miaoshaUserMapper = miaoshaUserMapper;
        this.redisService = redisService;
    }

    private MiaoshaUser getById(long id) {
        return miaoshaUserMapper.selectByPrimaryKey(id);
    }


    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }


    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = Md5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge((int) MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
