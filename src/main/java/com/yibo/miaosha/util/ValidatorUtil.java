package com.yibo.miaosha.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        return mobile_pattern.matcher(src).matches();
    }
}
