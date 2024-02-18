package com.kyeongho.errors.execption;

import com.kyeongho.common.ApiStatus;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ApiStatus.UNAUTHORIZED);
    }
    public UnauthorizedException(String message) {
        super(message, ApiStatus.UNAUTHORIZED);
    }
}
