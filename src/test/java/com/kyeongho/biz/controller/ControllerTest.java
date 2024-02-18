package com.kyeongho.biz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyeongho.biz.controller.model.LoginRequest;
import com.kyeongho.biz.controller.model.SignUpRequest;
import com.kyeongho.biz.entity.UsersEntity;
import com.kyeongho.biz.exception.BadCredentialException;
import com.kyeongho.biz.exception.NotUserIdFoundException;
import com.kyeongho.biz.model.RefundDto;
import com.kyeongho.biz.model.UsersDto;
import com.kyeongho.biz.service.UserService;
import com.kyeongho.common.ApiStatus;
import com.kyeongho.common.ScrapApiClient;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.config.ControllerTestConfig;
import com.kyeongho.config.ScrapApiClientTestConfig;
import com.kyeongho.config.SecurityConfig;
import com.kyeongho.security.EntryPointUnauthorizedHandler;
import com.kyeongho.security.Jwt;
import com.kyeongho.security.JwtAccessDeniedHandler;
import com.kyeongho.security.JwtTokenProperties;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@WebMvcTest(Controller.class)
@AutoConfigureMockMvc
@EnableConfigurationProperties(JwtTokenProperties.class)
@Import({
        ScrapApiClientTestConfig.class,
        ControllerTestConfig.class,
        SecurityConfig.class,
        EntryPointUnauthorizedHandler.class,
        JwtAccessDeniedHandler.class,
        RestTemplateAutoConfiguration.class
})
class ControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    Jwt jwt;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ScrapApiClient scrapApiClient;
    @MockBean
    UserService userService;

    @Test
    void 로그인() throws Exception {
        // given
        String userId = "hong12";
        String password = "testpass";
        String name = "홍길동";
        String regNo = "860824-1655068";
        given(userService.login(userId, password))
                .willReturn(UsersDto.from(new UsersEntity(new BCryptPasswordEncoder(), userId, password, name, regNo)));

        String loginRequestBody = new ObjectMapper()
                .writeValueAsString(new LoginRequest(userId, password));

        // when…then
        mvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response.token", Matchers.notNullValue()));
    }

    @Test
    void 로그인_예외_잘못된_회원_아이디_전달() throws Exception {
        // given
        String worngUserId = "worngUserId";
        String password = "testpass";

        given(userService.login(worngUserId, password))
                .willThrow(new NotUserIdFoundException());

        String loginRequestBody = new ObjectMapper()
                .writeValueAsString(new LoginRequest(worngUserId, password));

        // when…then
        mvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", equalTo(ApiStatus.NOT_USER_ID_FOUND.getMessage())));
    }

    @Test
    void 로그인_예외_잘못된_회원_비밀번호_전달() throws Exception {
        // given
        String userId = "hong12";
        String worngtestpass = "worngtestpass";
        given(userService.login(userId, worngtestpass))
                .willThrow(new BadCredentialException());
        String loginRequestBody = new ObjectMapper()
                .writeValueAsString(new LoginRequest(userId, worngtestpass));

        // when…then
        mvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", equalTo(ApiStatus.BAD_CREDENTIAL.getMessage())));
    }

    @Test
    void 회원가입() throws Exception {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(
                "hong12",
                "123456",
                "홍길동",
                "860824-1655068"
        );

        String requestJsonBody = new ObjectMapper()
                .writeValueAsString(signUpRequest);

        // when…then
        mvc.perform(post("/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJsonBody))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 회원가입_예외_가입_불가능한_회원_정보_전달() throws Exception {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(
                "ykh6242",
                "123456",
                "유경호",
                "950427-1000000"
        );

        String requestJsonBody = new ObjectMapper()
                .writeValueAsString(signUpRequest);

        // when…then
        mvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void 회원_정보_조회() throws Exception {
        // given
        String userId = "hong12";
        String password = "testpass";
        String name = "홍길동";
        String regNo = "860824-1655068";
        given(userService.getUsersDtoByUserId(userId))
                .willReturn(UsersDto.from(new UsersEntity(new BCryptPasswordEncoder(), userId, password, name, regNo)));
        String token = jwt.create(Jwt.Payload.of(
                name,
                userId
        ));

        // when…then
        mvc.perform(get("/me")
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", notNullValue()));
    }

    @Test
    void 스크랩() throws Exception {
        // given
        String userId = "hong12";
        String name = "홍길동";

        File currentFile = new File(".");
        File file = new File(currentFile.getAbsolutePath() + "/src/test/resources/ScrapResponse.json");
        ScrapResponseDto scrapResponseDto = new ObjectMapper().readValue(file, ScrapResponseDto.class);

        given(userService.getUsersDtoByUserId(userId))
                .willReturn(UsersDto.of(null, userId, name, "860824-1655068", false));
        given(userService.saveScrapData(userId, scrapResponseDto))
                .willReturn(scrapResponseDto);

        String token = jwt.create(Jwt.Payload.of(
                name,
                userId
        ));

        // when…then
        mvc.perform(post("/scrap")
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", notNullValue()));
    }

    @Test
    void 환급액_조회() throws Exception {
        // given
        String userId = "hong12";
        String name = "홍길동";

        given(userService.refund(userId))
                .willReturn(RefundDto.of(name, "0", "900,000"));
        String token = jwt.create(Jwt.Payload.of(
                name,
                userId
        ));

        // when…then
        mvc.perform(get("/refund")
                        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.response", notNullValue()));
    }
}
