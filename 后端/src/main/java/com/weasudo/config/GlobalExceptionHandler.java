package com.weasudo.config;

import com.weasudo.common.Response;
import com.weasudo.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.weasudo.common.ResponseCodeEnum.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //业务异常处理
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        return Response.error(e.getCodeEnum());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public Response<?> handleParamException(Exception e) {
        log.warn("请求参数异常", e);
        return Response.error(PARAM_ERROR);
    }

    //兜底异常
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Response.error(SYSTEM_ERROR);
    }
}
