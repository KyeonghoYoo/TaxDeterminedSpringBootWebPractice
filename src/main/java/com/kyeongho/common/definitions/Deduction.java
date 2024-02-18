package com.kyeongho.common.definitions;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
public enum Deduction {

    INSURANCE_PREMIUM(
            "보험료",
            0.12
    ),
    EDUCATION_EXPENSES(
            "교육비",
            0.15
    ),
    DONATIONS(
            "기부금",
            0.15
    ),
    MEDICAL_EXPENSES(
            "의료비",
            0.15
    ),
    RETIREMENT_PENSION(
            "퇴직연금",
            0.15
    ),
    ;

    private final String classifier;

    private final UnaryOperator<BigDecimal> calculator;

    Deduction(String classifier, double multiplyValue) {
        this.classifier = classifier;
        this.calculator = bigDecimal -> bigDecimal.multiply(BigDecimal.valueOf(multiplyValue));
    }

    public BigDecimal calculate(BigDecimal amount) {
        return calculator.apply(amount);
    }

    public static Optional<Deduction> of(String classifier) {
        for (Deduction deduction : Deduction.values()) {
            if(deduction.classifier.equalsIgnoreCase(classifier)){
                return Optional.of(deduction);
            }
        }
        return Optional.empty();
    }
}
