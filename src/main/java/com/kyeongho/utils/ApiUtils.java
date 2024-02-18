package com.kyeongho.utils;

import com.kyeongho.common.ApiStatus;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
public class ApiUtils {

    private ApiUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static <T> ApiResult<T> success(T response) {
        return success(response, ApiStatus.SUCCESS);
    }

    public static <T> ApiResult<T> success(T response, ApiStatus apiStatus) {
        return success(response, apiStatus.getMessage(), apiStatus.getStatus());
    }

    public static <T> ApiResult<T> success(T response, String message, HttpStatus status) {
        return new ApiResult<>(true, response, status.value(), message);
    }

    public static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, status.value(), message);
    }

    @Getter
    @ToString
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final int status;
        private final String message;

        private ApiResult(boolean success, T response, int status, String message) {
            this.success = success;
            this.response = response;
            this.status = status;
            this.message = message;
        }
    }
}
