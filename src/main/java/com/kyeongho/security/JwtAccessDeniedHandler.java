package com.kyeongho.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongho.common.ApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.kyeongho.utils.ApiUtils.error;

/**
 * 인증된 유저가 권한이 없는 요청을 했을 시 핸들링
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
                                 final AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(new ObjectMapper()
                .writeValueAsString(error(ApiStatus.FORBIDDEN.getMessage(),
                        ApiStatus.FORBIDDEN.getStatus())));
        response.getWriter().flush();
    }
}

