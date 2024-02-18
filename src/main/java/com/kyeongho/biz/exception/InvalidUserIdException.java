package com.kyeongho.biz.exception;

import com.kyeongho.common.ApiStatus;
import com.kyeongho.errors.execption.BusinessException;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
public class InvalidUserIdException extends BusinessException {

    public InvalidUserIdException() {
        super(ApiStatus.BAD_USER_ID_PROVIDED);
    }
}
