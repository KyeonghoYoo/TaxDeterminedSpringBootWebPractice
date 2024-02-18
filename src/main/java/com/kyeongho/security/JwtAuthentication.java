package com.kyeongho.security;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 인증된 유저 정보를 담은 Authentication
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
public class JwtAuthentication {

    public final String userId;
    public final String name;

    public JwtAuthentication(final String name, final String userId) {
        this.userId = checkNotNull(userId, "userId must be provided");
        this.name = checkNotNull(name, "username must be provided");
    }

}
