package com.weasudo.common;

import lombok.Data;

import static com.weasudo.common.ResponseCodeEnum.*;

@Data
public class Response <T> {
    private int code;
    private String msg;
    private T data = null;

    public Response() {
    }

    public static  <K> Response<K> success() {
        Response<K> response = new Response<>();
        response.code = SUCCESS.getCode();
        response.msg = SUCCESS.getMsg();
        return response;
    }

    public static  <K> Response<K> success(K data) {
        Response<K> response = new Response<>();
        response.code = SUCCESS.getCode();
        response.msg = SUCCESS.getMsg();
        response.data = data;
        return response;
    }


    public static <K> Response<K> error (ResponseCodeEnum responseCode) {
        Response<K> response = new Response<>();
        response.code = responseCode.getCode();
        response.msg = responseCode.getMsg();
        return response;
    }


}
