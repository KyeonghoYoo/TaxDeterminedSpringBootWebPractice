package com.kyeongho.biz.service;

import com.kyeongho.biz.entity.IncomeTaxDeductionEntity;
import com.kyeongho.biz.entity.UsersEntity;
import com.kyeongho.biz.exception.BadCredentialException;
import com.kyeongho.biz.exception.InvalidUserIdException;
import com.kyeongho.biz.exception.NotUserIdFoundException;
import com.kyeongho.biz.model.RefundDto;
import com.kyeongho.biz.model.UsersDto;
import com.kyeongho.biz.repository.IncomeTaxDeductionRepository;
import com.kyeongho.biz.repository.UsersRepository;
import com.kyeongho.common.ApiStatus;
import com.kyeongho.common.ScrapApiClient;
import com.kyeongho.common.definitions.Deduction;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.errors.execption.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kyeongho.utils.EncryptionUtils.encryptAES256;

/**
 *
 *  @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 12
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.###");

    private final PasswordEncoder passwordEncoder;
    private final ScrapApiClient scrapApiClient;
    private final UsersRepository usersRepository;
    private final IncomeTaxDeductionRepository incomeTaxDeductionRepository;

    @Transactional
    @Override
    public void signUp(String userId, String password, String name, String regNo)  {
        if (usersRepository.findByNameAndRegNo(name, encryptAES256(regNo)).isPresent()) {
            throw new BusinessException("해당 이름과 주민등록번호로 가입된 계정이 존재합니다.", ApiStatus.BAD_REQUEST);
        } else if (usersRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("해당 아이디로 가입된 계정이 존재합니다.", ApiStatus.BAD_REQUEST);
        }

        UsersEntity entity = new UsersEntity(passwordEncoder,
                                                userId,
                                                password,
                                                name,
                                                regNo);
        usersRepository.save(entity);
    }

    @Override
    public UsersDto login(String userId, String password) {
        checkNotNull(userId, "ID must be provided");
        checkNotNull(password, "password must be provided");
        UsersEntity usersEntity = usersRepository.findByUserId(userId)
                .orElseThrow(InvalidUserIdException::new);

        if (usersEntity.login(passwordEncoder, password)) {
            return UsersDto.from(usersEntity);
        } else {
            throw new BadCredentialException();
        }
    }

    @Override
    public UsersDto getUsersDtoByUserId(String userId) {
        UsersEntity usersEntity = usersRepository.findByUserId(userId)
                .orElseThrow(NotUserIdFoundException::new);
        return UsersDto.from(usersEntity);
    }

    @Transactional
    @Override
    public ScrapResponseDto saveScrapData(String userId, ScrapResponseDto scrapResponseDto) {
        UsersEntity usersEntity = usersRepository.findByUserId(userId)
                .orElseThrow(NotUserIdFoundException::new);
        if (Boolean.TRUE.equals(usersEntity.isScrap())) {
            throw new BusinessException("이미 스크랩된 유저입니다.", ApiStatus.BAD_REQUEST);
        }

        /* 데이터 추출 */
        ScrapResponseDto.ResponseData data = scrapResponseDto.getData();
        ScrapResponseDto.JsonList jsonList = data.getJsonList();

        BigDecimal totalSalaryAmount = data.getJsonList().getIncomeInfoList().stream()
                .map(incomeInfo -> {
                    return new BigDecimal(incomeInfo.getTotalSalaryAmount());
                })
                .reduce(BigDecimal::add)
                .get();
        BigDecimal calculatedIncomeTax = new BigDecimal(jsonList.getCalculatedIncomeTax());

        List<IncomeTaxDeductionEntity> incomeTaxDeductionEntities = jsonList.getIncomeTaxDeductionList().stream()
                .map(incomeTaxDeduction -> {
                    IncomeTaxDeductionEntity incomeTaxDeductionEntity = IncomeTaxDeductionEntity.from(incomeTaxDeduction);
                    usersEntity.addIncomeTaxDeductionEntity(incomeTaxDeductionEntity);
                    return incomeTaxDeductionEntity;
                })
                .collect(Collectors.toList());

        /* 변경사항 적용 */
        incomeTaxDeductionRepository.saveAll(incomeTaxDeductionEntities);
        usersEntity.setTotalSalaryAmount(totalSalaryAmount);
        usersEntity.setCalculatedIncomeTax(calculatedIncomeTax);
        usersEntity.setScrap(true);

        return scrapResponseDto;
    }

    @Override
    public RefundDto refund(String userId) {
        UsersEntity usersEntity = usersRepository.findByUserIdJoinFetchIncomeTaxDeductionEntities(userId)
                .orElseThrow(NotUserIdFoundException::new);

        if (Boolean.FALSE.equals(usersEntity.isScrap())) {
            throw new BusinessException("환급액 조회 전에 스크랩을 먼저 진행해주십시오.", ApiStatus.BAD_REQUEST);
        }

        List<IncomeTaxDeductionEntity> incomeTaxDeductionEntities = usersEntity.getIncomeTaxDeductionEntities();

        BigDecimal taxDetermined = usersEntity.getCalculatedIncomeTax().subtract(usersEntity.getCalculatedIncomeTax().multiply(BigDecimal.valueOf(0.55)));
        BigDecimal totalSalaryAmount = usersEntity.getTotalSalaryAmount();
        BigDecimal retirementPensionTaxCredit = BigDecimal.ZERO;
        BigDecimal totalSpecialTaxCredit = BigDecimal.ZERO;
        BigDecimal standardTaxCredit = BigDecimal.ZERO;

        for (IncomeTaxDeductionEntity incomeTaxDeductionEntity: incomeTaxDeductionEntities) {
            String classifier = incomeTaxDeductionEntity.getDeductionClassification();
            Deduction deduction = Deduction.of(classifier)
                    .orElseThrow(() -> new IllegalStateException("해당 소득공제 타입과 일치하는 com.kyeongho.common.definitions.Deduction이 없습니다."));

            BigDecimal amount = incomeTaxDeductionEntity.getAmount();

            switch (deduction) {
                case MEDICAL_EXPENSES:
                    amount = amount.subtract(totalSalaryAmount.multiply(BigDecimal.valueOf(0.03)));
                    totalSpecialTaxCredit = totalSpecialTaxCredit.add(deduction.calculate(amount).max(BigDecimal.ZERO));
                    break;
                case RETIREMENT_PENSION:
                    retirementPensionTaxCredit = deduction.calculate(amount);
                    break;
                default:
                    totalSpecialTaxCredit = totalSpecialTaxCredit.add(deduction.calculate(amount));

            }
        }

        if (totalSpecialTaxCredit.compareTo(BigDecimal.valueOf(130000.0)) <= 0) {
            standardTaxCredit = BigDecimal.valueOf(130000.0);
            totalSpecialTaxCredit = BigDecimal.ZERO;
        }

        taxDetermined = taxDetermined.subtract(totalSpecialTaxCredit)
                .subtract(standardTaxCredit)
                .subtract(retirementPensionTaxCredit);

        if (taxDetermined.compareTo(BigDecimal.ZERO) < 0) {
            taxDetermined = BigDecimal.ZERO;
        }

        return RefundDto.of(
                usersEntity.getName(),
                decimalFormat.format(taxDetermined.doubleValue()),
                decimalFormat.format(retirementPensionTaxCredit.doubleValue())
        );
    }
}
