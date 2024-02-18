package com.kyeongho.security;

import com.kyeongho.biz.entity.UsersEntity;
import com.kyeongho.biz.exception.InvalidUserIdException;
import com.kyeongho.biz.model.UsersDto;
import com.kyeongho.biz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.apache.commons.lang3.ClassUtils.isAssignable;

/**
 * authenticationManager를 통해 인증할 때 거치는 인증 로직을 구현함.
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;

        Object userId = authenticationToken.getPrincipal();
        String password = authenticationToken.getCredentials();

         return processUserAuthentication(String.valueOf(userId), password);
    }

    private JwtAuthenticationToken processUserAuthentication(final String userId, final String password) {
        try {
            UsersDto user = userService.login(userId, password);
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(
                            new JwtAuthentication(user.getName(),
                                                    user.getUserId()),
                            null
                    );
            authenticated.setDetails(user);
            return authenticated;
        } catch (InvalidUserIdException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }
}
