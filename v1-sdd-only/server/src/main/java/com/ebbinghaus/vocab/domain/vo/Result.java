package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 统一 API 响应体。
 * 所有 Controller 返回此类型，前端根据 code 判断成功/失败。
 */
@Data
public class Result<T> {

    /** 状态码：200=成功，其他=失败 */
    private int code;

    /** 提示消息 */
    private String message;

    /** 响应数据 */
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return error(400, message);
    }
}
