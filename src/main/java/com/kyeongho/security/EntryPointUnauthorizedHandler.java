package com.kyeongho.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongho.common.ApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.kyeongho.utils.ApiUtils.error;

/**
 * 인증되지 않은 유저의 요청을 핸들링
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(ApiStatus.UNAUTHORIZED.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(new ObjectMapper()
                        .writeValueAsString(error(ApiStatus.UNAUTHORIZED.getMessage(),
                                ApiStatus.UNAUTHORIZED.getStatus())));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
