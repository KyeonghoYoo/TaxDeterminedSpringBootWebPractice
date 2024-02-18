package com.kyeongho.biz.service;

import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.biz.model.RefundDto;
import com.kyeongho.biz.model.UsersDto;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 12
 */
public interface UserService {
    UsersDto login(String userId, String password);

    void signUp(String userId, String password, String name, String regNo);

    ScrapResponseDto saveScrapData(String userId, ScrapResponseDto scrapResponseDto);

    UsersDto getUsersDtoByUserId(String userId);

    RefundDto refund(String userId);
}
