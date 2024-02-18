package com.kyeongho.biz.service;

import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.config.ScrapApiClientTestConfig;
import com.kyeongho.errors.execption.BusinessException;
import com.kyeongho.biz.entity.UsersEntity;
import com.kyeongho.biz.model.RefundDto;
import com.kyeongho.biz.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 15
 */
@SpringBootTest
@Transactional
@Import(ScrapApiClientTestConfig.class)
class UserServiceTest {

    @Autowired
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    CacheManager cacheManager;
    @Autowired
    UserService userService;
    @Autowired
    UsersRepository usersRepository;

    @Test
    void 회원가입() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";

        // when
        assertDoesNotThrow(() -> userService.signUp(userId, password, name, regNo));

        // then
        Optional<UsersEntity> usersEntityOptional = usersRepository.findByUserId(userId);
        assertThat(usersEntityOptional.isPresent(), is(true));

        UsersEntity usersEntity = usersEntityOptional.get();
        assertThat(usersEntity, notNullValue());
        assertThat(usersEntity.getName(), equalTo(name));
        assertThat(usersEntity.login(passwordEncoder, password), is(true));
        assertThat(usersEntity.getRegNo(), equalTo(regNo));
    }

    @Test
    void 회원가입_예외_테스트_중복된_아이디() throws Exception {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";

        String newPassword = "654321";
        String newName = "김둘리";
        String newRegNo = "921108-1582816";

        // when…then
        userService.signUp(userId, password, name, regNo);
        assertThrows(BusinessException.class, () -> userService.signUp(userId, newPassword, newName, newRegNo))
                .printStackTrace();
    }

    @Test
    void 회원가입_예외_테스트_중복된_이름과_주민등록번호() throws Exception {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";

        String newUserId = "hong21";
        String newPassword = "654321";

        // when…then
        userService.signUp(userId, password, name, regNo);
        assertThrows(BusinessException.class, () -> userService.signUp(newUserId, newPassword, name, regNo))
                .printStackTrace();
    }

    @Test
    void 스크랩() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";
        userService.signUp(userId, password, name, regNo);

        // when
        ScrapResponseDto scrapResponseDto = assertDoesNotThrow(() -> userService.saveScrapData(userId, userService.scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        ))));

        // then
        assertThat(scrapResponseDto, notNullValue());
        assertThat(scrapResponseDto.getStatus(), is("success"));
    }

    @Test
    void 스크랩_캐싱_됐는지() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";
        userService.signUp(userId, password, name, regNo);

        // when
        ScrapResponseDto scrapResponseDto = userService.saveScrapData(userId, userService.scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        )));

        // then
        Cache simpleCache = checkNotNull(cacheManager.getCache("scrap"));
        ScrapResponseDto cachedScrapResponseDto
                = simpleCache.get(SimpleKeyGenerator.generateKey(userId), ScrapResponseDto.class);
        assertThat(cachedScrapResponseDto, notNullValue());
        assertThat(cachedScrapResponseDto, is(scrapResponseDto));
    }

    @Test
    void 스크랩_예외_스크랩_중복_수행() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";
        userService.signUp(userId, password, name, regNo);
        userService.saveScrapData(userId, userService.scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        )));
        Cache simpleCache = checkNotNull(cacheManager.getCache("scrap"));
        simpleCache.evictIfPresent(SimpleKeyGenerator.generateKey(userId));

        // when…then
        assertThrows(BusinessException.class, () -> userService.saveScrapData(userId, userService.scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        ))))
                .printStackTrace();
    }

    @Test
    void 환급액_계산() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";
        userService.signUp(userId, password, name, regNo);
        userService.saveScrapData(userId, userService.scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        )));

        // when
        RefundDto refund = userService.refund(userId);
        // then
        assertThat(refund, notNullValue());
    }

    @Test
    @DirtiesContext
    void 환급액_계산_예외_스크랩_하지_않음() {
        // given
        String userId = "hong12";
        String password = "123456";
        String name = "홍길동";
        String regNo = "860824-1655068";
        userService.signUp(userId, password, name, regNo);

        // when…then
        assertThrows(BusinessException.class, () -> userService.refund(userId))
                .printStackTrace();
    }
}