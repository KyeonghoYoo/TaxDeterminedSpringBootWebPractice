package com.kyeongho.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongho.common.RestTemplateScrapApiClient;
import com.kyeongho.common.ScrapApiClient;
import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class ScrapApiClientTestConfig {

    @Bean
    @Primary
    public ScrapApiClient scrapApiClient() throws IOException {
        File currentFile = new File(".");
        File file = new File(currentFile.getAbsolutePath() + "/src/test/resources/ScrapResponse.json");
        ScrapResponseDto scrapResponseDto = new ObjectMapper().readValue(file, ScrapResponseDto.class);
        RestTemplateScrapApiClient mockRestTemplateScrapApiClient = mock(RestTemplateScrapApiClient.class);
        when(mockRestTemplateScrapApiClient.requestScrap(ScrapRequest.of("홍길동", "860824-1655068")))
                .thenReturn(scrapResponseDto);
        return mockRestTemplateScrapApiClient;
    }
}
