package com.kyeongho.biz.exception;

import com.kyeongho.common.ApiStatus;
import com.kyeongho.errors.execption.BusinessException;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 15
 */
public class NotUserIdFoundException extends BusinessException {
    public NotUserIdFoundException() {
        super(ApiStatus.NOT_USER_ID_FOUND);
    }

    public NotUserIdFoundException(String message) {
        super(message, ApiStatus.NOT_USER_ID_FOUND);
    }
}
