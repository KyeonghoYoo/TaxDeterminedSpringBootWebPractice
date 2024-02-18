package com.kyeongho.common;

import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.errors.execption.ExternalApiCallErrorOccurredException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
class ScarpApiTest {

    @Test
    void RestTemplate를_이용한_scrap_api_호출_테스트() {
        //given
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri("https://codetest.3o3.co.kr/v2").build();
        ScrapApiClient scrapApiClient = new RestTemplateScrapApiClient(restTemplate);
        ScrapRequest request = ScrapRequest.of("홍길동", "860824-1655068");

        //when
        ScrapResponseDto response = scrapApiClient.requestScrap(request);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo("success"));
    }

    @Test
    void RestTemplate를_이용한_scrap_api_호출_실패_테스트() {
        //given
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri("https://codetest.3o3.co.kr/v2").build();
        ScrapApiClient scrapApiClient = new RestTemplateScrapApiClient(restTemplate);
        ScrapRequest request = ScrapRequest.of("없는 회원 이름", "860824-1655068");

        //when…then
        assertThrows(ExternalApiCallErrorOccurredException.class, () -> scrapApiClient.requestScrap(request));
    }
}
