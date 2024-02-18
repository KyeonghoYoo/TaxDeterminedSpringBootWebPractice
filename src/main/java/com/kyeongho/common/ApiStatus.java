package com.kyeongho.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Getter
public enum ApiStatus {
    //// 2 Series
    SUCCESS("작업을 완료하였습니다.", HttpStatus.OK),
    CREATED("생성을 완료하였습니다.", HttpStatus.CREATED),

    //// 4 Series
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    BAD_USER_ID_PROVIDED("잘못된 아이디입니다.", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIAL("잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    // 401 UNAUTHORIZED
    UNAUTHORIZED("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    BAD_JWT_PROVIDED("토큰에 문제가 있어 접근할 수 없습니다.", HttpStatus.UNAUTHORIZED),
    // 403 FORBIDDEN
    FORBIDDEN("요청한 리소스에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    NOT_USER_ID_FOUND("해당 아이디를 가진 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    //// 5 Series
    INTERNAL_SERVER_ERROR("요청 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    OCCURRED_ERROR_WHILE_CALLING_API("외부 API와 통신중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    ApiStatus(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    private final String message;
    private final HttpStatus status;

    public static Optional<ApiStatus> of(String name) {
        for (ApiStatus apiStatus : ApiStatus.values()) {
            if(apiStatus.name().equalsIgnoreCase(name)){
                return Optional.of(apiStatus);
            }
        }
        return Optional.empty();
    }

    public static Optional<ApiStatus> of(HttpStatus status) {
        for (ApiStatus apiStatus : ApiStatus.values()) {
            if(apiStatus.getStatus().equals(status)){
                return Optional.of(apiStatus);
            }
        }
        return Optional.empty();
    }
}
