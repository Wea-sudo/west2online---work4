package com.weasudo.exception;

import com.weasudo.common.ResponseCodeEnum;

public class BusinessException extends RuntimeException {
    private final ResponseCodeEnum codeEnum;

    public BusinessException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.codeEnum = codeEnum;
    }

    public ResponseCodeEnum getCodeEnum() {
        return this.codeEnum;
    }
}
