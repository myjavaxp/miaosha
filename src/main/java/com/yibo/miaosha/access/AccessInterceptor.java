package com.yibo.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.yibo.miaosha.config.UserArgumentResolver;
import com.yibo.miaosha.constant.CommonConstants;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.AccessKey;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 并不能代替掉{@link UserArgumentResolver}
 *
 * @author yibo
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private final MiaoshaUserService userService;

    private final RedisService redisService;

    @Autowired
    public AccessInterceptor(MiaoshaUserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        MiaoshaUser user = getUser(request, response);
        if (handler instanceof HandlerMethod) {
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null || user.getNickname().equals("游客")) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }  //do nothing
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < maxCount) {
                redisService.incr(ak, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm), CommonConstants.FEATURES);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return new MiaoshaUser("游客");
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser user = userService.getByToken(response, token);
        return user == null ? new MiaoshaUser("游客") : user;
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length < 1) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(MiaoshaUserService.COOKIE_NAME_TOKEN)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
