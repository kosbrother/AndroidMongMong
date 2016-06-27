package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

public class ResponseEntity<T> {
    @SerializedName("data")
    private T data;
    @SerializedName("error")
    private Error error;

    public T getData() {
        return data;
    }

    public Error getError() {
        return error;
    }

    public static class Error {
        @SerializedName("code")
        private int code;
        @SerializedName("message")
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
