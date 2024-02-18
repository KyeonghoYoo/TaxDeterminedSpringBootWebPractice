package com.kyeongho.biz.entity;

import com.kyeongho.biz.exception.BadCredentialException;
import com.kyeongho.common.BaseEntity;
import com.kyeongho.utils.EncryptionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 */
@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UsersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;
    private String userId;
    private String password;
    private String name;
    private String regNo;

    @Setter
    private boolean scrap;
    @Setter
    private BigDecimal totalSalaryAmount;
    @Setter
    private BigDecimal calculatedIncomeTax;

    @OneToMany(mappedBy = "usersEntity",cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<IncomeTaxDeductionEntity> incomeTaxDeductionEntities = new ArrayList<>();

    public UsersEntity(PasswordEncoder passwordEncoder, String userId, String password, String name, String regNo) {
        this.userId = userId;
        this.password = passwordEncoder.encode(password);
        this.name = name;
        this.regNo = EncryptionUtils.encryptAES256(regNo);
        this.scrap = false;
    }

    /**
     * 요청된 패스워드가 회원의 패스워드와 일치하는지 검증한다.
     *
     * @param passwordEncoder org.springframework.security.crypto.password.PasswordEncoder
     * @param credential 클라이언트로부터 전달 받은 패스워드
     */
    public boolean login(PasswordEncoder passwordEncoder, String credential) throws BadCredentialException {
        return passwordEncoder.matches(credential, this.password);
    }

    public void addIncomeTaxDeductionEntity(IncomeTaxDeductionEntity incomeTaxDeductionEntity) {
        incomeTaxDeductionEntities.add(incomeTaxDeductionEntity);
        incomeTaxDeductionEntity.setUsersEntity(this);
    }

    public String getRegNo() {
        return EncryptionUtils.decryptAES256(regNo);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userNo", userNo)
                .append("userId", userId)
                .append("password", "[PROTECTED]")
                .append("name", name)
                .append("regNo", "[PROTECTED]")
                .append("scrap", scrap)
                .append("totalSalaryAmount", totalSalaryAmount)
                .append("calculatedIncomeTax", calculatedIncomeTax)
                .toString();
    }
}
