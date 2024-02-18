package com.kyeongho.biz.entity;

import com.kyeongho.common.BaseEntity;
import com.kyeongho.common.model.ScrapResponseDto;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@Entity
@Table(name = "IncomeTaxDeduction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class IncomeTaxDeductionEntity extends BaseEntity {

    private static final DecimalFormat format = new DecimalFormat("#,##0.###");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeTaxDeductionNo;
    private BigDecimal amount;
    private String deductionClassification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    @Setter
    @ToString.Exclude
    private UsersEntity usersEntity;

    public IncomeTaxDeductionEntity(
            BigDecimal amount,
            String deductionClassification) {
        this.amount = amount;
        this.deductionClassification = deductionClassification;
    }

    public static IncomeTaxDeductionEntity from(ScrapResponseDto.IncomeTaxDeduction incomeTaxDeduction) {
        return new IncomeTaxDeductionEntity(
                new BigDecimal(incomeTaxDeduction.getAmount().replace(",", "")),
                incomeTaxDeduction.getDeductionClassification()
        );
    }
}
