package com.kyeongho.common.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * 스크랩 api의 반환 데이터를 역직렬화하여 저장하기 위한 DTO 클래스
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Data
public class ScrapResponseDto {

    private String status;
    private ResponseData data;
    private ResponseError errors;
    @Data
    public static class ResponseData {
        private JsonList jsonList;
        private String appVer;
        private String errMsg;
        private String company;
        private String svcCd;
        private String hostNm;
        private String workerResDt;
        private String workerReqDt;
    }
    @Data
    public static class JsonList {

        @JsonAlias("급여")
        private List<IncomeInfo> incomeInfoList;
        @JsonAlias("산출세액")
        private String calculatedIncomeTax;
        @JsonAlias("소득공제")
        private List<IncomeTaxDeduction> incomeTaxDeductionList;
    }

    @Data
    public static class IncomeInfo {

        @JsonAlias("소득내역")
        private String salaryType;
        @JsonAlias("총지급액")
        private String totalSalaryAmount;
        @JsonAlias("업무시작일")
        private String serviceStartDate;
        @JsonAlias("기업명")
        private String companyName;
        @JsonAlias("이름")
        private String incomeEarnerName;
        @JsonAlias("지급일")
        private String paymentDate;
        @JsonAlias("업무종료일")
        private String serviceEndDate;
        @JsonAlias("주민등록번호")
        private String residentRegistrationNumber;
        @JsonAlias("소득구분")
        private String incomeClassification;
        @JsonAlias("사업자등록번호")
        private String businessRegistrationNumber;

    }
    @Data
    public static class IncomeTaxDeduction {
        @JsonAlias({ "금액", "총납임금액" })
        private String amount;
        @JsonAlias("소득구분")
        private String deductionClassification;
    }

    @Data
    public static class ResponseError {

        private String code;
        private String message;
    }
}
