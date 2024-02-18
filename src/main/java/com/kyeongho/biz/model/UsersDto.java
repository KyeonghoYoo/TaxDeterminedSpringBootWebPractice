package com.kyeongho.biz.model;

import com.kyeongho.biz.entity.UsersEntity;
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
public class UsersDto {

    private Long userNo;
    private String userId;
    private String name;
    private String regNo;

    private boolean scrap;

    public static UsersDto from(UsersEntity usersEntity) {
        return UsersDto.of(
                usersEntity.getUserNo(),
                usersEntity.getUserId(),
                usersEntity.getName(),
                usersEntity.getRegNo(),
                usersEntity.isScrap()
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userNo", userNo)
                .append("userId", userId)
                .append("name", name)
                .append("regNo", "[PROTECTED]")
                .append("scrap", scrap)
                .toString();
    }
}
