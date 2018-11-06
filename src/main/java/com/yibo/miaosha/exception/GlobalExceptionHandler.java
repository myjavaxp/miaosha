package com.yibo.miaosha.exception;

import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<Void> exceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCm());
        }
        if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            String[] error = new String[errors.size()];
            for (int i = 0; i < errors.size(); i++) {
                error[i] = errors.get(i).getDefaultMessage();
            }
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(error));
        }
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}