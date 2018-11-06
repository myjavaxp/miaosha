package com.yibo.miaosha.controller;

import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.MiaoshaUserService;
import com.yibo.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final MiaoshaUserService userService;

    @Autowired
    public LoginController(MiaoshaUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @PostMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        LOGGER.info(loginVo.toString());
        //登录
        return Result.success(userService.login(response, loginVo));
    }
}
