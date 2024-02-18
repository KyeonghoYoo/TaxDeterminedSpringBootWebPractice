package com.kyeongho.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongho.security.Jwt;
import com.kyeongho.security.JwtTokenProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    public Jwt jwt(JwtTokenProperties jwtTokenProperties) {
        return new Jwt(jwtTokenProperties.getIssuer(), jwtTokenProperties.getClientSecret(), jwtTokenProperties.getExpirySeconds());
    }

    @Bean
    public Map<String, String> usersCanSignUpMap() throws IOException {
        File currentFile = new File(".");
        File file = new File(currentFile.getAbsolutePath() + "/src/main/resources/static/UserList.json");
        return new ObjectMapper().readValue(file, new TypeReference<List<Map<String, String>>>() {})
                .stream()
                .collect(toMap(
                        mapToKey -> mapToKey.get("name"),
                        mapToValue -> mapToValue.get("regNo")
                ));
    }
}
