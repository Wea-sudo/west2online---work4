package com.weasudo.common;

public enum ResponseCodeEnum {
    // ========== 通用状态码 ==========
    SUCCESS(200, "成功"), // 操作成功
    PARAM_ERROR(201, "参数错误/缺失"),        // 参数异常
    BUSINESS_ERROR(202, "业务异常"),          // 通用业务异常
    SYSTEM_ERROR(500, "系统异常"),      // 系统异常

    // ========== 业务专属状态码 ==========
//    USER_NOT_EXIST(203, "用户不存在"),        // 用户模块     // 用户模块
//    USER_NOT_EXIST(204, "用户已存在"),        // 用户模块     // 用户模块
//    PASSWORD_ERROR(204, "密码错误"),          // 用户模块
//    PASSWORD_ERROR(204, "两次密码不一致"), // 用户模块
    USER_NOT_EXIST(203, "用户不存在"),
    USERNAME_ALREADY_EXIST(204, "用户已存在"),
    PASSWORD_ERROR(205, "密码错误"),
    PASSWORD_NOT_MATCH(206, "两次密码不一致"),
    OLD_PASSWORD_ERROR(207, "旧密码错误"),
    NEW_PASSWORD_SAME_AS_OLD(208, "新密码不能与旧密码相同"),
    UPDATE_USER_PASSWORD_FAILED(209, "更新用户密码失败"),
    GENERATE_TOKEN_FAILED(210, "生成Token失败"),
    UPDATE_USER_INFO_FAILED(210, "更新用户信息失败"),
    UPDATE_USER_FAILED(207, "更新用户失败"),   // 用户模块
    DELETE_USER_FAILED(208, "删除用户失败"),   // 用户模块
    TOKEN_EXPIRED(401, "Token已过期"),             // 用户模块
    TOKEN_INVALID(401, "Token无效"),               // 用户模块

    ARTICLE_NOT_EXIST(207, "文章不存在"),     // 文章模块
    ALREADY_THUMB(208, "已点赞"),             // 文章模块
    NOT_THUMB(209, "未点赞"),                 // 文章模块
    THUMB_FAIL(210,"点赞失败"),
    CANCEL_THUMB_FAIL(215,"取消点赞失败"),
    ALREADY_COLLECT(210, "已收藏"),           // 文章模块
    NOT_COLLECT(211, "未收藏"),              // 文章模块
    CANCEL_COLLECT_FAIL(212,"取消收藏失败"),
    COLLECT_FAIL(213,"收藏失败"),
    CREATE_ARTICLE_FAILED(212, "创建文章失败"),// 文章模块
    NO_AUTH(213, "无权限操作"),              // 文章模块


    CREATE_COMMENT_FAILED(213, "创建评论失败"),// 文章模块

    FILE_NAME_ALREADY_EXIST(214, "文件已存在"),// 文件模块
    FILE_NOT_FOUND(215, "文件不存在"),          // 文件模块
    UPLOAD_FILE_FAILED(216, "上传文件失败");          // 文件模块
    // 枚举属性：对应base里的code和msg
    private final Integer code;  // 状态码
    private final String msg;    // 提示语

    // 构造方法（枚举的构造方法默认private）
    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // getter方法（枚举属性默认private，需要getter）
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
