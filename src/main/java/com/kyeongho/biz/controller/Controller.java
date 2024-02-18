package com.kyeongho.biz.controller;

import com.kyeongho.biz.controller.model.LoginRequest;
import com.kyeongho.biz.controller.model.LoginResponse;
import com.kyeongho.biz.controller.model.MeResponse;
import com.kyeongho.biz.controller.model.SignUpRequest;
import com.kyeongho.biz.model.RefundDto;
import com.kyeongho.biz.model.UsersDto;
import com.kyeongho.biz.service.UserService;
import com.kyeongho.common.ApiStatus;
import com.kyeongho.common.ScrapApiClient;
import com.kyeongho.common.model.ScrapRequest;
import com.kyeongho.common.model.ScrapResponseDto;
import com.kyeongho.errors.execption.BusinessException;
import com.kyeongho.security.Jwt;
import com.kyeongho.security.JwtAuthentication;
import com.kyeongho.security.JwtAuthenticationToken;
import com.kyeongho.security.JwtTokenProperties;
import com.kyeongho.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

import static com.kyeongho.utils.ApiUtils.success;
import static org.springframework.util.StringUtils.hasText;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 14
 */
@Slf4j
@Tag(name = "BIZ", description = "API 컨트롤러")
@RestController
@RequiredArgsConstructor
public class Controller {

    private final ScrapApiClient scrapApiClient;
    private final UserService userService;

    private final Jwt jwt;
    private final JwtTokenProperties jwtTokenProperties;
    private final AuthenticationManager authenticationManager;
    private final Map<String, String> usersCanSignUpMap;

    @Operation(summary = "로그인", tags = { "BIZ" })
    @PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<LoginResponse> login(
            HttpServletResponse response,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 요청 데이터", required = true)
            @Valid @RequestBody LoginRequest request
    ) {
        Authentication authentication = authenticationManager
                .authenticate(new JwtAuthenticationToken(
                        request.getUserId(),
                        request.getPassword()
                ));
        UsersDto usersDto = (UsersDto) authentication.getDetails();
        String token = jwt.create(Jwt.Payload.of(
                        usersDto.getName(),
                        usersDto.getUserId()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.setHeader(jwtTokenProperties.getHeader(), "Bearer " + token);
        return success(new LoginResponse(token));
    }

    @Operation(summary = "회원 가입", tags = { "BIZ" })
    @PostMapping(path = "signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiUtils.ApiResult<?> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원가입 요청 데이터", required = true)
            @Valid @RequestBody SignUpRequest request
    ) {
        if (isUserPossibleToSignUp(request.getName(), request.getRegNo())) {
            userService.signUp(request.getUserId(),
                    request.getPassword(),
                    request.getName(),
                    request.getRegNo());

            return success(null, "회원가입이 완료되었습니다.", ApiStatus.CREATED.getStatus());
        } else {
            throw new BusinessException("해당 이름과 주민등록번호는 서비스에 가입할 수 있는 대상이 아닙니다.", ApiStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "회원 정보 조회", tags = { "BIZ" }, security = @SecurityRequirement(name = "token"))
    @GetMapping(path = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<MeResponse> me(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        UsersDto usersDto = userService.getUsersDtoByUserId(authentication.userId);
        return success(MeResponse.from(usersDto));
    }

    @Operation(summary = "스크랩 API 서버에 회원 정보 스크랩", tags = { "BIZ" }, security = @SecurityRequirement(name = "token"))
    @PostMapping(path = "scrap", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<ScrapResponseDto> scrap(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        UsersDto usersDto = userService.getUsersDtoByUserId(authentication.userId);
        ScrapResponseDto scrapResponseDto = scrapApiClient.requestScrap(ScrapRequest.of(
                usersDto.getName(),
                usersDto.getRegNo()
        ));

        try {
            userService.saveScrapData(authentication.userId, scrapResponseDto);
        } catch (BusinessException exception) {
            log.error(exception.getMessage());
        }

        return success(scrapResponseDto);
    }

    @Operation(summary = "회원의 결정세액과 퇴직연금세액공제금액 조회", tags = { "BIZ" }, security = @SecurityRequirement(name = "token"))
    @GetMapping(path = "refund", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<RefundDto> refund(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        return success(userService.refund(authentication.userId));
    }

    private boolean isUserPossibleToSignUp(String name, String regNo) {
        String regNoFromMap = usersCanSignUpMap.get(name);
        return hasText(regNoFromMap) && regNoFromMap.equals(regNo);
    }
}
