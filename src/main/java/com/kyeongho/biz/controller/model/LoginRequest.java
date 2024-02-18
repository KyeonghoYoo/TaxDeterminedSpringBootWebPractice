package com.kyeongho.biz.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@Data
public class LoginRequest {

    @Schema(description = "회원 아이디", example = "hong12")
    @NotBlank
    private final String userId;
    @Schema(description = "회원 비밀번호", example = "123456")
    @NotBlank
    private final String password;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("password", "[PROTECTED]")
                .toString();
    }
}
