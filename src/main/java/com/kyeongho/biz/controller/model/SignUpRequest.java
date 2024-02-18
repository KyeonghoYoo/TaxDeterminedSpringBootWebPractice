package com.kyeongho.biz.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 13
 */
@Data
public class SignUpRequest {

    @Schema(description = "아이디", example = "hong12")
    @NotBlank
    private final String userId;
    @Schema(description = "비밀번호", example = "123456")
    @NotBlank
    private final String password;
    @Schema(description = "이름", example = "홍길동")
    @NotBlank
    private final String name;
    @Schema(description = "주민등록번호", example = "860824-1655068")
    @NotBlank
    private final String regNo;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("password", "[PROTECTED]")
                .append("name", name)
                .append("regNo", "[PROTECTED]")
                .toString();
    }
}
