package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private Object meta;
    private ErrorDetail error;

    private ApiResponse() {
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.meta = null;
        response.error = null;
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = null;
        response.meta = null;
        response.error = new ErrorDetail(code, message);
        return response;
    }

    public T getData() {
        return data;
    }

    public Object getMeta() {
        return meta;
    }

    public ErrorDetail getError() {
        return error;
    }

    public static class ErrorDetail {
        private String code;
        private String message;

        public ErrorDetail() {
        }

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
