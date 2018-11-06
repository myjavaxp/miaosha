package com.yibo.miaosha.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        return MOBILE_PATTERN.matcher(src).matches();
    }
}
