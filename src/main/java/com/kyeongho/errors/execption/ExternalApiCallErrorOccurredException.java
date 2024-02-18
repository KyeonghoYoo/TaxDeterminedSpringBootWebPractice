package com.kyeongho.errors.execption;

import com.kyeongho.common.ApiStatus;

/**
 *
 * @author ykh6242@naver.com
 * @since 2024. 01. 16
 */
public class ExternalApiCallErrorOccurredException extends BusinessException {

    public ExternalApiCallErrorOccurredException() {
        super(ApiStatus.OCCURRED_ERROR_WHILE_CALLING_API);
    }

    public ExternalApiCallErrorOccurredException(String message) {
        super(message, ApiStatus.OCCURRED_ERROR_WHILE_CALLING_API);
    }
}
