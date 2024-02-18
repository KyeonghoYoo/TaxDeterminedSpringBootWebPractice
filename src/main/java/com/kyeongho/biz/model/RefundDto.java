package com.kyeongho.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 15
 */
@Data
@AllArgsConstructor(staticName = "of")
public class RefundDto {

    @JsonProperty("이름")
    private String name;
    @JsonProperty("결정세액")
    private String taxDetermined;
    @JsonProperty("퇴직연금세액공제")
    private String retirementPensionTaxCredit;
}
