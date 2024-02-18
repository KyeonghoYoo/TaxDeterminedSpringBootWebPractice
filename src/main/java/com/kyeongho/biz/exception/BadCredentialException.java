package com.kyeongho.biz.exception;

import com.kyeongho.common.ApiStatus;
import com.kyeongho.errors.execption.BusinessException;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
public class BadCredentialException extends BusinessException {

    public BadCredentialException() {
        super(ApiStatus.BAD_CREDENTIAL);
    }

    public BadCredentialException(String message) {
        this(message, ApiStatus.BAD_CREDENTIAL);
    }

    public BadCredentialException(String message, ApiStatus apiStatus) {
        super(message, apiStatus);
    }
}
