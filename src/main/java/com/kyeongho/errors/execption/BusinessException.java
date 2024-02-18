package com.kyeongho.errors.execption;

import com.kyeongho.common.ApiStatus;
import lombok.Getter;

/**
 * 애플리케이션 비즈니스 로직 진행 중 정상적인 흐름을 가져갈 수 없을때 발생시키는 Exception의 최상위 클래스
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ApiStatus apiStatus;

    public BusinessException(ApiStatus apiStatus) {
        super(apiStatus.getMessage());
        this.apiStatus = apiStatus;
    }

    public BusinessException(String message, ApiStatus apiStatus) {
        super(message);
        this.apiStatus = apiStatus;
    }
}
