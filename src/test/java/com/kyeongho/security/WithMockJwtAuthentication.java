package com.kyeongho.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 테스트 시 이 어노테이션을 사용하면 인증된 Mock 인증정보를 SecurityContext에 담는다.
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 12
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockJwtAuthentication {

    String name() default "홍길동";
    String userId() default "hong12";
    String regNo() default "860824-1655068";
}
