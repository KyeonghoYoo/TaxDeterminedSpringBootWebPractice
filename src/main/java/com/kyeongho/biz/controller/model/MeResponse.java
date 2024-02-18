package com.kyeongho.biz.controller.model;

import com.kyeongho.biz.model.UsersDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@Data
@AllArgsConstructor(staticName = "of")
public class MeResponse {

    private Long userNo;
    private String userId;
    private String name;
    private String regNo;

    public static MeResponse from(UsersDto usersDto) {
        return MeResponse.of(
                usersDto.getUserNo(),
                usersDto.getUserId(),
                usersDto.getName(),
                usersDto.getRegNo()
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userNo", userNo)
                .append("userId", userId)
                .append("name", name)
                .append("regNo", "[PROTECTED]")
                .toString();
    }
}
