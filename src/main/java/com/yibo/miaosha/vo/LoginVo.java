package com.yibo.miaosha.vo;

import com.yibo.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.StringJoiner;

public class LoginVo {

    @NotBlank(message = "手机号码不能为空")
    @IsMobile
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Length(min = 32)
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LoginVo.class.getSimpleName() + "[", "]")
                .add("mobile='" + mobile + "'")
                .add("password='" + password + "'")
                .toString();
    }
}
