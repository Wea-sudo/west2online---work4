package com.weasudo;

import lombok.Data;

@Data
public class Response <T> {
    private int code;
    private String message;
    private T data;

    public Response() {
    }

    public static  <K> Response<K> success() {
        Response<K> response = new Response<>();
        response.code = 200;
        response.message = "success";
        return response;
    }

    public static <K> Response<K> success (K data) {
        Response<K> response = new Response<>();
        response.code = 200;
        response.message = "success";
        response.data = data;
        return response;
    }

    public static <K> Response<K> error (int code, String message) {
        Response<K> response = new Response<>();
        response.code = code;
        response.message = message;
        return response;
    }


}
