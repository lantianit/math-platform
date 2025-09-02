package com.zh.mathplatform.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    
    // 用户相关错误码
    USER_NOT_EXIST(40001, "用户不存在"),
    USER_ACCOUNT_EXIST(40002, "用户账号已存在"),
    USER_EMAIL_EXIST(40003, "用户邮箱已存在"),
    USER_PHONE_EXIST(40004, "用户手机号已存在"),
    USER_ACCOUNT_BANNED(40005, "用户账号已被禁用"),
    PASSWORD_ERROR(40006, "密码错误"),
    TOKEN_EXPIRED(40007, "登录已过期"),
    TOKEN_INVALID(40008, "登录凭证无效"),
    
    // 内容相关错误码
    CONTENT_NOT_EXIST(41001, "内容不存在"),
    CONTENT_ALREADY_DELETED(41002, "内容已被删除"),
    CONTENT_NO_PERMISSION(41003, "无权限操作此内容"),
    CONTENT_TITLE_TOO_LONG(41004, "标题过长"),
    CONTENT_TOO_LONG(41005, "内容过长"),
    CONTENT_TYPE_INVALID(41006, "内容类型无效"),
    CONTENT_STATUS_INVALID(41007, "内容状态无效"),
    
    // 互动相关错误码
    ALREADY_LIKED(42001, "已经点赞过了"),
    NOT_LIKED(42002, "还没有点赞"),
    ALREADY_FOLLOWED(42003, "已经关注过了"),
    NOT_FOLLOWED(42004, "还没有关注"),
    CANNOT_FOLLOW_SELF(42005, "不能关注自己"),
    COMMENT_NOT_EXIST(42006, "评论不存在"),
    COMMENT_NO_PERMISSION(42007, "无权限操作此评论"),
    
    // 学习打卡相关错误码
    ALREADY_CHECKED_IN(43001, "今天已经打卡了"),
    CHECKIN_NOT_EXIST(43002, "打卡记录不存在"),
    CHECKIN_DATE_INVALID(43003, "打卡日期无效"),
    LEARNING_TIME_INVALID(43004, "学习时长无效"),
    MOOD_SCORE_INVALID(43005, "心情评分无效"),
    
    // 话题相关错误码
    TOPIC_NOT_EXIST(44001, "话题不存在"),
    TOPIC_NAME_EXIST(44002, "话题名称已存在"),
    TOPIC_NAME_TOO_LONG(44003, "话题名称过长"),
    TOPIC_DESCRIPTION_TOO_LONG(44004, "话题描述过长"),
    
    // 文件相关错误码
    FILE_UPLOAD_FAILED(45001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(45002, "文件类型不支持"),
    FILE_SIZE_TOO_LARGE(45003, "文件大小超出限制"),
    FILE_NOT_EXIST(45004, "文件不存在"),
    
    // 系统相关错误码
    RATE_LIMIT_EXCEEDED(46001, "请求频率过高，请稍后再试"),
    SERVICE_UNAVAILABLE(46002, "服务暂时不可用"),
    DATABASE_ERROR(46003, "数据库操作失败"),
    CACHE_ERROR(46004, "缓存操作失败"),
    EXTERNAL_SERVICE_ERROR(46005, "外部服务调用失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}