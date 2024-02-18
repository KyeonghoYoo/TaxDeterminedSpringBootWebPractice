package com.kyeongho.common;

import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.errors.execption.ExternalApiCallErrorOccurredException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
@RequiredArgsConstructor
public class RestTemplateScrapApiClient implements ScrapApiClient {

    private final RestTemplate restTemplate;

    @Cacheable("scrap")
    @Override
    public ScrapResponseDto requestScrap(ScrapRequest request) {
        ScrapResponseDto scrapResponseDto
                = restTemplate.postForObject("/scrap", request, ScrapResponseDto.class);
        if (checkNotNull(scrapResponseDto).getStatus().equals("success")) {
            return scrapResponseDto;
        } else {
            throw new ExternalApiCallErrorOccurredException(scrapResponseDto.getErrors().getMessage());
        }
    }
}
