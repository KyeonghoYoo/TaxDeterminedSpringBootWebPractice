package com.kyeongho.common;

import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
public interface ScrapApiClient {

    ScrapResponseDto requestScrap(ScrapRequest request);
}
