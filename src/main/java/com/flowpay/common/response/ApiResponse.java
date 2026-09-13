package com.flowpay.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiResponse<T>(
            String code,
            String message,
            T data,
            Instant timestamp
    ) {
        public static  <T>ApiResponse<T> success(T data) {
            return new ApiResponse<>(
                    "1000",
                    "Success",
                    data,
                    Instant.now()
            );
        }

        public static <T>ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(
                    "1000",
                    message,
                    data,
                    Instant.now()
            );
        }

        public static ApiResponse<Void> success() {
            return new ApiResponse<>(
                    "1000",
                    "Success",
                    null,
                    Instant.now()
            );
        }
    }
