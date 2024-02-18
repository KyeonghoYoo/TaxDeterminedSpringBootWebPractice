package com.kyeongho.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.token")
@Data
public class JwtTokenProperties {

    private String header;
    private String issuer;
    private String clientSecret;
    private int expirySeconds;
    private String[] ignoreUrl;
}
