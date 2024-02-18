package com.kyeongho.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Mock 인증 정보를 생성하는 로직이 구현된 클래스
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
public class WithMockJwtAuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(final WithMockJwtAuthentication annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(
                        new JwtAuthentication(annotation.name(),
                                                annotation.userId()),
                        null
                );
        context.setAuthentication(authentication);
        return context;
    }
}
