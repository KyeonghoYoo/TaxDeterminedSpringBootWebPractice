package com.kyeongho.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Configuration
public class OpenapiConfig {
    @Bean
    public OpenAPI customOpenApi(@Value("${springdoc.version}") String docVersion) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("token", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addParameters("Authentication", new Parameter().name("Authentication").in(ParameterIn.HEADER.toString()).schema(new StringSchema().description("JWT Access Token").example("Bearer {someaccesstoken}")).required(true))
                )
                .servers(newArrayList(new Server().url("http://localhost:8080").description("로컬 서버")))
                .info(
                        new Info()
                                .title("yookyeongho API Server")
                                .description("유경호 API 서버")
                                .version(docVersion)
                                .contact(new Contact()
                                        .name("유경호")
                                        .email("ykh6242@naver.com"))
                                .license(new License().name("MIT"))
                );
    }
}
