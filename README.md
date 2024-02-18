# Spring Boot Web 연습 프로젝트

---
## 어플리케이션 정보

- port: 8080
- swagger-ui url path: /swagger-ui.html 
- h2 database info 
    - console url path: /h2-console
    - driver-class-name: org.h2.Driver
    - url: jdbc:h2:mem:localhost;MODE=MYSQL;DB_CLOSE_DELAY=-1
    - username: sa
    - password:
---

# Project Description
### 목차
- [개발 환경](#개발환경)
- [프로젝트 구조](#프로젝트-구조)
- [공통 로직](#공통-로직)
   - [일괄된 스키마로 API 반환하기](#일괄된-스키마로-api-반환하기)
   - [ApiStatus 정의](#apistatus-정의)
   - [예외 핸들링](#예외-핸들링)
   - [JWT 기반의 토큰 인증/인가 구조](#jwt-기반의-토큰-인증인가-구조)
   - [엔티티 정보](#엔티티-정보)
- [API 구현 상세 내용](#api-구현-상세-내용)
  - [회원 가입](#1-회원-가입)
  - [로그인](#2-로그인)
  - [회원 정보 조회](#3-회원-정보-조회)
  - [스크랩 요청](#4-스크랩-요청)
  - [결정세액과 퇴직연금세액공제금액 계산 및 조회](#5-결정세액과-퇴직연금세액공제금액-계산-및-조회)
- [테스트](#테스트)
- [스웨거를 통한 api 테스트 참고 사항](#스웨거를-통한-api-테스트-참고-사항)

## 개발환경
```text
tool: intelliJ IDEA
Project name: yookyeongho
build tool: gradle
java version: 11
springboot version: 2.7.18

Dependencies:
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation
spring-boot-starter-web
spring-boot-starter-cache
lombok
h2
spring-boot-starter-test
spring-security-test
commons-lang3
guava
jsonwebtoken
springdoc-openapi-ui
springdoc-openapi-security
p6spy-spring-boot-starter
```

## 프로젝트 구조
```text
.
├── README.md
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── kyeongho
│   │   │           ├── Application.java
│   │   │           ├── common
│   │   │           │   ├── ApiStatus.java
│   │   │           │   ├── BaseEntity.java
│   │   │           │   ├── RestTemplateScrapApiClient.java
│   │   │           │   ├── ScrapApiClient.java
│   │   │           │   ├── definitions
│   │   │           │   │   └── Deduction.java
│   │   │           │   └── model
│   │   │           │       ├── ScrapRequest.java
│   │   │           │       └── ScrapResponseDto.java
│   │   │           ├── config
│   │   │           │   ├── CacheConfig.java
│   │   │           │   ├── JpaConfig.java
│   │   │           │   ├── OpenapiConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── ServiceConfig.java
│   │   │           │   └── WebConfig.java
│   │   │           ├── errors
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── execption
│   │   │           │       ├── BusinessException.java
│   │   │           │       └── UnauthorizedException.java
│   │   │           ├── security
│   │   │           │   ├── EntryPointUnauthorizedHandler.java
│   │   │           │   ├── Jwt.java
│   │   │           │   ├── JwtAccessDeniedHandler.java
│   │   │           │   ├── JwtAuthentication.java
│   │   │           │   ├── JwtAuthenticationProvider.java
│   │   │           │   ├── JwtAuthenticationToken.java
│   │   │           │   ├── JwtAuthenticationTokenFilter.java
│   │   │           │   └── JwtTokenProperties.java
│   │   │           ├── biz
│   │   │           │   ├── controller
│   │   │           │   │   ├── Controller.java
│   │   │           │   │   └── model
│   │   │           │   │       ├── LoginRequest.java
│   │   │           │   │       ├── LoginResponse.java
│   │   │           │   │       ├── MeResponse.java
│   │   │           │   │       └── SignUpRequest.java
│   │   │           │   ├── entity
│   │   │           │   │   ├── IncomeTaxDeductionEntity.java
│   │   │           │   │   └── UsersEntity.java
│   │   │           │   ├── exception
│   │   │           │   │   ├── BadCredentialException.java
│   │   │           │   │   ├── InvalidUserIdException.java
│   │   │           │   │   └── NotUserIdFoundException.java
│   │   │           │   ├── model
│   │   │           │   │   ├── RefundDto.java
│   │   │           │   │   └── UsersDto.java
│   │   │           │   ├── repository
│   │   │           │   │   ├── IncomeTaxDeductionRepository.java
│   │   │           │   │   └── UsersRepository.java
│   │   │           │   └── service
│   │   │           │       ├── UserService.java
│   │   │           │       └── UserServiceImpl.java
│   │   │           └── utils
│   │   │               ├── ApiUtils.java
│   │   │               └── EncryptionUtils.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── schema-h2.sql
│   │       └── static
│   │           └── UserList.json
│   └── test
│       ├── java
│       │   └── com
│       │       └── kyeongho
│       │           ├── common
│       │           │   └── ScarpApiTest.java
│       │           ├── config
│       │           │   ├── ScrapApiClientTestConfig.java
│       │           │   └── ControllerTestConfig.java
│       │           ├── security
│       │           │   ├── WithMockJwtAuthentication.java
│       │           │   └── WithMockJwtAuthenticationSecurityContextFactory.java
│       │           └── biz
│       │               ├── controller
│       │               │   └── ControllerTest.java
│       │               └── service
│       │                   └── UserServiceTest.java
│       └── resources
│           └── ScrapResponse.json
```
- `/src/main/java/**`
    - `com.kyeongho.Application.java`: `@SpringBootApplication`이 적용된 root 클래스
    - `com.kyeongho.common`: 공통 요소들 포함
    - `com.kyeongho.config`: `@Configuration`이 적용된 설정 프로젝트 로직들 포함
    - `com.kyeongho.errors`: 예외 처리와 관련된 작업물들 포함
    - `com.kyeongho.security`: Spring Security&Jwt를 활용한 인증/인가 작업물들 포함
    - `com.kyeongho.biz`: 핵심 비즈니스 로직 포함
    - `com.kyeongho.utils`: 유틸리티성 클래스들 포함
- `/src/main/java/resources`: 프로젝트 구성에 필요한 리소스들 포함
- `src/test/**`: 테스트 관련 작업물들 포함

# 공통 로직

## 일괄된 스키마로 API 반환하기

```java
package com.kyeongho.utils;

import ...

public class ApiUtils {

    private ApiUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static <T> ApiResult<T> success(T response) {
        return success(response, ApiStatus.SUCCESS);
    }

    public static <T> ApiResult<T> success(T response, ApiStatus apiStatus) {
        return success(response, apiStatus.getMessage(), apiStatus.getStatus());
    }

    public static <T> ApiResult<T> success(T response, String message, HttpStatus status) {
        return new ApiResult<>(true, response, status.value(), message);
    }

    public static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, status.value(), message);
    }

    @Getter
    @ToString
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final int status;
        private final String message;

        private ApiResult(boolean success, T response, int status, String message) {
            this.success = success;
            this.response = response;
            this.status = status;
            this.message = message;
        }
    }
}
```
- 모든 API, 에러 반환 스키마는 정적 메소드를 통해 `ApiResult<T>` 타입의 Response를 반환하게 됨.

###### **성공 반환 예시**
```json
{
  "success": true,
  "response": {
    "이름": "홍길동",
    "결정세액": "0",
    "퇴직연금세액공제": "900,000"
  },
  "status": 200,
  "message": "작업을 완료하였습니다."
}
```
###### **에러 반환 예시**
```json
{
  "success": false,
  "response": null,
  "status": 404,
  "message": "해당 아이디를 가진 유저가 존재하지 않습니다."
}
```
## ApiStatus 정의
```java
package com.kyeongho.common;

import ...

@Getter
public enum ApiStatus {
    //// 2 Series
    SUCCESS("작업을 완료하였습니다.", HttpStatus.OK),
    CREATED("생성을 완료하였습니다.", HttpStatus.CREATED),

    //// 4 Series
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    BAD_USER_ID_PROVIDED("잘못된 아이디입니다.", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIAL("잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    // 401 UNAUTHORIZED
    UNAUTHORIZED("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    BAD_JWT_PROVIDED("토큰에 문제가 있어 접근할 수 없습니다.", HttpStatus.UNAUTHORIZED),
    // 403 FORBIDDEN
    FORBIDDEN("요청한 리소스에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    NOT_USER_ID_FOUND("해당 아이디를 가진 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    //// 5 Series
    INTERNAL_SERVER_ERROR("요청 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    OCCURRED_ERROR_WHILE_CALLING_API("외부 API와 통신중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    ApiStatus(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    private final String message;
    private final HttpStatus status;

    public static Optional<ApiStatus> of(String name) {
        for (ApiStatus apiStatus : ApiStatus.values()) {
            if(apiStatus.name().equalsIgnoreCase(name)){
                return Optional.of(apiStatus);
            }
        }
        return Optional.empty();
    }

    public static Optional<ApiStatus> of(HttpStatus status) {
        for (ApiStatus apiStatus : ApiStatus.values()) {
            if(apiStatus.getStatus().equals(status)){
                return Optional.of(apiStatus);
            }
        }
        return Optional.empty();
    }
}
```
- 본 API에서 발생할 수 있는 API 요청 처리 결과의 상태값을 정의하는 Enum 클래스
- 해당 상태에 대한 `message`와 `HttpStatus` 데이터로 구성됨.
- 기본 룰은 HTTP Status를 따름.

## 예외 핸들링

##### Business Exception 처리

본 프로젝트에서 Business Exception은 시스템적으로 오류가 나 발생시키는 Exception이 아닌 요구사항에 맞지 않아 더 이상 정상적인 흐름을 이어나갈 수 없을 때 발생하는 Exception으로 정의합니다.

```java
package com.kyeongho.errors.execption;

import ...

@Getter
public class BusinessException extends RuntimeException {

  private final ApiStatus apiStatus;

  public BusinessException(ApiStatus apiStatus) {
    super(apiStatus.getMessage());
    this.apiStatus = apiStatus;
  }

  public BusinessException(String message, ApiStatus apiStatus) {
    super(message);
    this.apiStatus = apiStatus;
  }
}

}
``` 

본 서비스에서 발생하는 모든 BusinessException은 [BusinessException.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Ferrors%2Fexecption%2FBusinessException.java)을 상속받아 구현하였습니다. 상속 받아 구현된 Exception들은 아래와 같습니다.

- UnauthorizedException.java; 인증이 실패하여 더 이상 흐름을 가져갈 수 없을 때 발생
- BadCredentialException.java; 로그인 시 잘못된 비밀번호가 전달되어 로그인 처리 흐름을 가져갈 수 없을 때 발생 
- InvalidUserIdException.java; 로그인 시 잘못된 회원 아이디가 전달되어 로그인 처리 흐름을 가져갈 수 없을 때 발생
- NotUserIdFoundException.java; 회원 아이디로 회원 데이터를 색인할 때, 색인이 불가하여 정상적인 흐름을 이어나갈 수 없을 때 발생 
- ExternalApiCallErrorOccurredException.java; 외부 API 호출 중 에러가 발생하였을 때 발생

위 항목에 나열된 Exception 외의 다수 상황에서도 Business Exception이 발생하나, 이번 프로젝트에서는 구현 시간 및 편의 관계상 `BusinessException`을 직접 thorw하여 처리하였습니다.

하지만 실무에서는 될 수 있으면 구체적으로 Exception이 정의 되는 것이 좋다는 의견입니다.
- 같은 상황에 대한 예외 처리시 중복 코드를 막을 수 있음.
- 자바 코드를 통한 테스트 검증에서 명시적으로 예외 테스트 결과를 도출 할 수 있음.
  ```json
  assertThrows(ExternalApiCallErrorOccurredException.class, () -> scrapApiClient.requestScrap(request))
   ```

##### @ControllerAdvice로 모든 Exception을 핸들링

[GlobalExceptionHandler.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Ferrors%2FGlobalExceptionHandler.java)

```java
package com.kyeongho.errors;

import ...

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ApiUtils.ApiResult<?>> newResponse(Throwable throwable, HttpStatus status) {
    return newResponse(throwable.getMessage(), status);
  }

  private ResponseEntity<ApiUtils.ApiResult<?>> newResponse(ApiStatus apiStatus) {
    return newResponse(apiStatus.getMessage(), apiStatus.getStatus());
  }

  private ResponseEntity<ApiUtils.ApiResult<?>> newResponse(String message, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return new ResponseEntity<>(error(message, status), headers, status);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiUtils.ApiResult<?>> handleBusinessException(BusinessException e) {
    log.error("handleBusinessException", e);
    return newResponse(e.getMessage(), e.getApiStatus().getStatus());
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiUtils.ApiResult<?>> handleJwtException(JwtException e) {
    log.error("handleJwtException", e);
    return newResponse(ApiStatus.BAD_JWT_PROVIDED);
  }

  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<ApiUtils.ApiResult<?>> handleRestClientException(JwtException e) {
    log.error("handleRestClientException", e);
    return newResponse(ApiStatus.OCCURRED_ERROR_WHILE_CALLING_API);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiUtils.ApiResult<?>> handleException(Exception e) {
    log.error("handleException", e);
    return newResponse(ApiStatus.INTERNAL_SERVER_ERROR);
  }
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<?> handleNotFoundException(Exception e) {
    return newResponse(e, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<?> handleUnauthorizedException(Exception e) {
    return newResponse(ApiStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({
          MethodArgumentNotValidException.class,
          HttpMessageNotReadableException.class
  })
  public ResponseEntity<?> handleBadRequestException(Exception e) {
    log.debug("Bad request exception occurred: {}", e.getMessage(), e);
    if (e instanceof MethodArgumentNotValidException) {
      return newResponse(
              ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage(),
              HttpStatus.BAD_REQUEST
      );
    }
    return newResponse(e, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMediaTypeException.class)
  public ResponseEntity<?> handleHttpMediaTypeException(Exception e) {
    return newResponse(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> handleMethodNotAllowedException(Exception e) {
    return newResponse(e, HttpStatus.METHOD_NOT_ALLOWED);
  }
}

```
- @ControllerAdvice을 통해 본 프로젝트의 Controller 외에도 추가될 수 있는 모든 Controller의 예외를 처리할 수 있도록 한다.
- @ExceptionHandler 어노테이션이 사용된 메소드를 통해 각각 적절한 예외 처리를 구현

## JWT 기반의 토큰 인증/인가 구조

Spring Security의 인증/인를 활용하여 각 API별 권한 검증을 실행하도록 구성하였습니다.

jwt 토큰을 생성하고 검증하는 로직은 jjwt 라이브러리를 이용해 구현되었습니다.

본 api에서 security를 구성하는 핵심 요소들은 다음과 같습니다.
- [Jwt.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwt.java); 인증/인가에 사용되는 jwt 토큰의 생성/검증 로직을 포함하며 jwt 토큰 내에 담기는 데이터의 스키마를 포함함.
- [JwtTokenProperties.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtTokenProperties.java); Jwt 빈 객체를 등록할 때 사용되는 설정 요소들을 담은 Properties 클래스
- [JwtAuthentication.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAuthentication.java); login 인증시 사용되는 스키마
- [JwtAuthenticationToken.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAuthenticationToken.java); org.springframework.security.authentication.AbstractAuthenticationToken의 구현체
- [JwtAuthenticationProvider.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAuthenticationProvider.java); AuthenticationManager를 통해 회원 인증을 수행할 때 사용될 로직을 제공함. 요청된 회원 아이디, 비밀번호를 검증함.
- [JwtAuthenticationTokenFilter.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAuthenticationTokenFilter.java); 인증이 필요한 api에 요청이 오면 유효한 jwt 토큰이 헤더에 담겨 요청이 되는지 검증하고 검증이 성공하면 요청에 권한을 부여함.
  - JwtAuthenticationTokenFilter에서 인증이 실패하면 발생할 수 있는 에러를 핸들링할 때 사용되는 핸들러
    - [JwtAccessDeniedHandler.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAccessDeniedHandler.java)
    - [EntryPointUnauthorizedHandler.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FEntryPointUnauthorizedHandler.java)
- [SecurityConfig.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fconfig%2FSecurityConfig.java); Spring Security 설정 클래스

## 엔티티 정보
###### 회원 엔티티 (UsersEntity.java)

```java
package com.kyeongho.biz.entity;

import ...

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
  @Getter(AccessLevel.NONE)
  @Setter
  private Boolean scrap;
  @Setter
  private Double totalSalaryAmount;
  @Setter
  private Double calculatedIncomeTax;

  @OneToMany(mappedBy = "usersEntity")
  private final List<IncomeTaxDeductionEntity> incomeTaxDeductionEntities = new ArrayList<>();

  public UsersEntity(PasswordEncoder passwordEncoder, String userId, String password, String name, String regNo) {
    this.userId = userId;
    this.password = passwordEncoder.encode(password);
    this.name = name;
    this.regNo = EncryptionUtils.encryptAES256(regNo);
    this.scrap = false;
  }

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

  public Boolean isScraped() {
    return scrap;
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
```
- 아래의 필드들로 구성됨.
  - `userNo`; 자동 증가하는 PK 칼럼
  - `userId`; 회원 아이디
  - `password`; 비밀번호
  - `name`; 회원 이름
  - `regNo`; 주민등록 번호
  - `scrap`; 스크랩여부
  - `totalSalaryAmount`; 후에 스크랩을 통해 받은 총지급액이 저장될 필드
  - `calculatedIncomeTax`; 후에 스크랩을 통해 받은 산출세액이 저장될 필드
- 소득공제 엔티티([IncomeTaxDeductionEntity](#소득공제-엔티티-incometaxdeductionentityjava))와 1:n 연관관계 매핑이 되어 있음.

###### 소득공제 엔티티 (IncomeTaxDeductionEntity.java)

```java
package com.kyeongho.biz.entity;

import ...

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
  private Double amount;
  private String deductionClassification;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @Setter
  @ToString.Exclude
  private UsersEntity usersEntity;

  public IncomeTaxDeductionEntity(
          Double amount,
          String deductionClassification) {
    this.amount = amount;
    this.deductionClassification = deductionClassification;
  }

  public static IncomeTaxDeductionEntity from(ScrapResponseDto.IncomeTaxDeduction incomeTaxDeduction) {
    try {
      return new IncomeTaxDeductionEntity(
              format.parse(incomeTaxDeduction.getAmount()).doubleValue(),
              incomeTaxDeduction.getDeductionClassification()
      );
    } catch (ParseException e) {
      throw new IllegalStateException("스크랩 api로 부터 반환된 amount 값이 format에 맞지않습니다.");
    }
  }
}
```
- 아래의 필드들로 구성됨.
  - `incomeTaxDeductionNo`; 자동 증가하는 PK 칼럼 
  - `deductionClassification`; 소득공제 구분 데이터 { 보험료, 교육비, 기부금, 의료비, 퇴직연금 }  
  - `amount`; 소득공제 금액 
- 회원 엔티티([UsersEntity](#회원-엔티티-usersentityjava))와 n:1 연관관계 매핑이 되어 있음.

# API 구현 상세 내용

## 1. 회원 가입

- api path: /signup
- http method: POST
- Request body 예시
  ```json
  {
    "userId": "hong12",
    "password": "123456",
    "name": "홍길동",
    "regNo": "860824-1655068"
  }
  ```
- Responses 예시
  - 성공 (201)
    ```json
    {
      "success": true,
      "response": null,
      "status": 201,
      "message": "회원가입이 완료되었습니다."
    }
    ```
  - 중복된 이름과 주민등록번호 (400)
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "해당 이름과 주민등록번호로 가입된 계정이 존재합니다."
    }
    ``` 
  - 중복된 아이디 (400)
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "해당 아이디로 가입된 계정이 존재합니다."
    }
    ```
  - 가입할 수 없는 회원 정보 (400)
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "해당 이름과 주민등록번호는 서비스에 가입할 수 있는 대상이 아닙니다."
    }
    ```
#### Presentation layer

###### controller (Controller.java)
```java
@RestController
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;

    private final Map<String, String> usersCanSignUpMap;
    
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
    
    private boolean isUserPossibleToSignUp(String name, String regNo) {
        String regNoFromMap = usersCanSignUpMap.get(name);
        return hasText(regNoFromMap) && regNoFromMap.equals(regNo);
    }
}
```
- usersCanSignUpMap; 정해진 회원 정보로만 가입할 수 있도록 검증하기 위해 `Map<String, String> usersCanSignUpMap`을 빈 등록하여 활용함. 
  ```java
  @Bean
  public Map<String, String> usersCanSignUpMap() throws IOException {
      File currentFile = new File(".");
      File file = new File(currentFile.getAbsolutePath() + "/src/main/resources/static/UserList.json");
      return new ObjectMapper().readValue(file, new TypeReference<List<Map<String, String>>>() {})
              .stream()
              .collect(toMap(
                      mapToKey -> mapToKey.get("name"),
                      mapToValue -> mapToValue.get("regNo")
              ));
  }
  ```
  - [UserList.json](src%2Fmain%2Fresources%2Fstatic%2FUserList.json); 회원 가입 가능한 회원 정보를 json 파일 내 `Array`로 포함함.
    ```json
    [
      {
        "name": "홍길동",
        "regNo": "860824-1655068"
      },
      {
        "name": "김둘리",
        "regNo": "921108-1582816"
      },
      {
        "name": "마징가",
        "regNo": "880601-2455116"
      },
      {
        "name": "베지터",
        "regNo": "910411-1656116"
      },
      {
        "name": "손오공",
        "regNo": "820326-2715702"
      }
    ]
    ```

###### model
SignUpRequest.java; 로그인 요청시 사용자가 요청하게 될 파라미터의 스키마
```java
@Data
public class SignUpRequest {
    
  @NotBlank
  private final String userId;
  @NotBlank
  private final String password;
  @NotBlank
  private final String name;
  @NotBlank
  private final String regNo;

  @Override
  public String toString() {
      return new ToStringBuilder(this)
              .append("userId", userId)
              .append("password", "[PROTECTED]")
              .append("name", name)
              .append("regNo", "[PROTECTED]")
              .toString();
  }
}
```
- `@NotBlank`를 적용한 필수 파라미터 설정
  - userId (String): 아이디
  - password (String): 패스워드
  - name (String): 이름
  - regNo (String): 주민등록번호

#### business layer

###### UserService(UserServiceImpl.java)

```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    
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
}
```
- controller로 부터 넘어온 파라미터를 통해 가입 가능한 회원 정보인지 먼저 검증함.
- 가입 가능한 회원 정보라면 `UsersEntity`를 새로 생성하고 `UsersRepository`를 통해 저장함.
- `UsersEntity`는 다음과 같이 생성됨.
  ```java
  public UsersEntity(PasswordEncoder passwordEncoder, String userId, String password, String name, String regNo) {
      this.userId = userId;
      this.password = passwordEncoder.encode(password);
      this.name = name;
      this.regNo = EncryptionUtils.encryptAES256(regNo);
      this.scrap = false;
  }
  ```
  - `password(비밀번호)`와 `regNo(주민등록번호)`와 같이 민감 데이터에 대한 암호화 요구가 있었기에 위와 같이 암호화를 수행함.
  - `password(비밀번호)`의 경우 단방향 암호화로 향후 누구도 그 값이 무엇인지 직접 확인할 수 없음.
    - `org.springframework.security.crypto.password.PasswordEncoder` 빈을 등록하여 받아 암호화에 활용함. 그 중 `BCryptPasswordEncoder`를 활용함.
  - `regNo(주민등록번호)`의 경우 추후 `/scrap` api에 온전히 복구된 주민등록번호를 전달해야 하므로 양방향 암호화를 수행함.
    - `regNo(주민등록번호)`의 경우 회원가입시 중복 체크에 사용하기 위해 동일입력 동일출력이 보장되도록 암호화를 실시하였음.

#### persistence layer

UsersRepository.java

```java
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    // ...
}
```
- Spring Data JPA의 `JpaRepository`를 활용함.
- `save()`를 통해 회원 엔티티를 영속화함.

## 2. 로그인

- api path: /login
- http method: POST
- Request body 예시
  ```json
  {
    "userId": "hong12",
    "password": "123456"
  }
  ```
- Responses 예시
  - 성공 (200)
    ```json
    {
      "success": true,
      "response": {
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzenNfeW9va3llbmdobyIsImlhdCI6MTcwNTM5NDk1MCwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMiJ9.7nLtGztJEsSFCubvLOFzRNTBQqd2JiiZEKFlVKEJrIo"
      },
      "status": 200,
      "message": "작업을 완료하였습니다."
    }
    ```
    - Header; `authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzenNfeW9va3llbmdobyIsImlhdCI6MTcwNTM5NDk1MCwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMiJ9.7nLtGztJEsSFCubvLOFzRNTBQqd2JiiZEKFlVKEJrIo`
  - 요청된 회원 아이디를 찾을 수 없음 (404)
    ```json 
    {
      "success": false,
      "response": null,
      "status": 404,
      "message": "잘못된 아이디입니다."
    }
    ```
  - 요청된 비밀번호가 조회된 회원이 비밀번호와 일치하지 않음 (400)
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "잘못된 비밀번호입니다."
    }
    ```

#### Presentation layer

###### Controller(Controller.java)
```java
@RestController
@RequiredArgsConstructor
public class Controller {

    private final Jwt jwt;
    private final JwtTokenProperties jwtTokenProperties;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<LoginResponse> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest request
    ) {
      Authentication authentication = authenticationManager
              .authenticate(new JwtAuthenticationToken(
                      request.getUserId(),
                      request.getPassword()
              ));
      UsersEntity usersEntity = (UsersEntity) authentication.getDetails();
      String token = jwt.create(Jwt.Payload.of(
              usersEntity.getName(),
              usersEntity.getUserId()
      ));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      response.setHeader(jwtTokenProperties.getHeader(), "Bearer " + token);
      return success(new LoginResponse(token));
    }    
}
```
- 요청되어 넘어온 파라미터를 이용하여 `AuthenticationManager`를 통해 인증을 수행함.
  - [JwtAuthenticationProvider.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fsecurity%2FJwtAuthenticationProvider.java); 실제 실행되는 로그인 인증 로직을 담당
    ```java
    @RequiredArgsConstructor
    public class JwtAuthenticationProvider implements AuthenticationProvider {
    
        private final UserService userService;
    
        @Override
        public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
            JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
    
            Object userId = authenticationToken.getPrincipal();
            String password = authenticationToken.getCredentials();
    
             return processUserAuthentication(String.valueOf(userId), password);
        }
    
        private JwtAuthenticationToken processUserAuthentication(final String userId, final String password) {
            try {
                UsersEntity user = userService.login(userId, password);
                JwtAuthenticationToken authenticated =
                        new JwtAuthenticationToken(
                                new JwtAuthentication(user.getName(),
                                                        user.getUserId()),
                                null
                        );
                authenticated.setDetails(user);
                return authenticated;
            } catch (InvalidUserIdException e) {
                throw new UsernameNotFoundException(e.getMessage());
            } catch (DataAccessException e) {
                throw new AuthenticationServiceException(e.getMessage(), e);
            }
        }
    
        @Override
        public boolean supports(final Class<?> authentication) {
            return isAssignable(JwtAuthenticationToken.class, authentication);
        }
    }
    ```
- 인증이 성공하면 `Authentication` 객체에 로그인이 성공한 `UsersEntity`가 담겨져서 옴.
- `UsersEntity`를 통해 새로운 token을 생성하고 이를 활용하여 반환함.
- 생성되는 jwt token은 다음과 같이 생성되어짐.
  ```java
  public String create(Payload payload) {
      JwtBuilder builder = Jwts.builder()
              .header()
                  .type("JWT")
              .and()
              .issuer(this.issuer)
              .issuedAt(new Date())
              .expiration(new Date(new Date().getTime() + this.expirySeconds * 1_000L))
              .claim("name", payload.getName())
              .claim("userId", payload.getUserId())
              .signWith(clientSecret, signatureAlgorithm);
      return builder.compact();
  }
  ```


###### model

LoginRequest.java

```java
@Data
public class LoginRequest {

    @NotBlank
    private final String userId;
    @NotBlank
    private final String password;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("password", "[PROTECTED]")
                .toString();
    }
}
```

LoginResponse.java

```java
@Data
public class LoginResponse {

    private final String token;
}
```

#### business layer

###### UserService(UserServiceImpl.java)
```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UsersEntity login(String userId, String password) {
      checkNotNull(userId, "ID must be provided");
      checkNotNull(password, "password must be provided");
      UsersEntity usersEntity = usersRepository.findByUserId(userId)
              .orElseThrow(InvalidUserIdException::new);
  
      if (usersEntity.login(passwordEncoder, password)) {
        return usersEntity;
      } else {
        throw new BadCredentialException();
      }
    }    
}
```
- `AuthenticationManager`로 인증 로직을 수행하면 실제 `JwtAuthenticationProvider`에서 `UserServiceImpl#login`을 호출하게 됨.
- `UsersEntity#login`은 다음과 같음
  ```java
  public boolean login(PasswordEncoder passwordEncoder, String credential) throws BadCredentialException {
      return passwordEncoder.matches(credential, this.password);
  }
  ```
- 잘못된 계정이 전달되거나 조회된 계정과 비밀번호가 일치하지 않으면 예외처리 발생

#### persistence layer

```java
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findByUserId(@NonNull String userId);
}
```
- Spring Data JPA의 메소드 쿼리 기능을 활용해, 인터페이스에 `findByUserId` 쿼리를 생성함.

## 3. 회원 정보 조회

- api path: /me
- http method: GET
- Request 예시
  - Headers:
    - `Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzenNfeW9va3llbmdobyIsImlhdCI6MTcwNTQwODcyMSwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMiJ9.TzPmARdk4ARU_Mk3WLSLGXCxFOArlx9_suvQ2GMQiiQ'`
- Responses 예시
  - 성공 (200)
    ```json
    {
      "success": true,
      "response": {
        "userNo": 1,
        "userId": "hong12",
        "name": "홍길동",
        "regNo": "860824-1655068"
      },
      "status": 200,
      "message": "작업을 완료하였습니다."
    }
    ```
  - Authentication 헤더에 토큰을 담지 않고 요청했을 경우 (401)
    ```json
    {
      "success": false,
      "response": null,
      "status": 401,
      "message": "인증에 실패하였습니다."
    }
    ```

#### Presentation layer
###### Controller(Controller.java)
```java
@RestController
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;
    
    @Operation(summary = "회원 정보 조회", tags = { "BIZ" }, security = @SecurityRequirement(name = "token"))
    @GetMapping(path = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<MeResponse> me(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        UsersDto usersDto = userService.getUsersDtoByUserId(authentication.userId);
        return success(MeResponse.from(usersDto));
    }
}
```
- `@AuthenticationPrincipal JwtAuthentication authentication`를 통해 스프링 시큐리티 컨텍스트에서 인증된 회원의 정보를 파라미터로 바인딩 받는다.
- 인증된 토큰을 통해 회원 아이디를 받아 요청한 회원의 정보를 조회하여 반환함. 

###### model

MeResponse.java

```java
@Data
@AllArgsConstructor(staticName = "of")
public class MeResponse {

    private Long userNo;
    private String userId;
    private String name;
    private String regNo;

    public static MeResponse from(UsersDto usersDto) {
        return MeResponse.of(
                usersDto.getUserNo(),
                usersDto.getUserId(),
                usersDto.getName(),
                usersDto.getRegNo()
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userNo", userNo)
                .append("userId", userId)
                .append("name", name)
                .append("regNo", "[PROTECTED]")
                .toString();
    }
}
```

#### business layer

###### UserService(UserServiceImpl.java)
```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    
    @Override 
    public UsersDto getUsersDtoByUserId(String userId) {
        UsersEntity usersEntity = usersRepository.findByUserId(userId)
                .orElseThrow(NotUserIdFoundException::new);
        return UsersDto.from(usersEntity);
    }
}
```
- 회원 아이디로 회원 엔티티를 조회해 UsersDto.java로 변환하여 반환함.

## 4. 스크랩 요청

- api path: /scrap
- http method: POST
- Request 예시
  - Headers: 
    - `Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzenNfeW9va3llbmdobyIsImlhdCI6MTcwNTQwODcyMSwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMiJ9.TzPmARdk4ARU_Mk3WLSLGXCxFOArlx9_suvQ2GMQiiQ'`
- Responses 예시
    - 성공 (200)
      ```json
      {
        "success": true,
        "response": {
          "status": "success",
          "data": {
            "jsonList": {
              "incomeInfoList": [
                {
                  "salaryType": "급여",
                  "totalSalaryAmount": "60,000,000",
                  "serviceStartDate": "2020.10.02",
                  "companyName": "(주)활빈당",
                  "incomeEarnerName": "홍길동",
                  "paymentDate": "2020.11.02",
                  "serviceEndDate": "2021.11.02",
                  "residentRegistrationNumber": "860824-1655068",
                  "incomeClassification": "근로소득(연간)",
                  "businessRegistrationNumber": "012-34-56789"
                }
              ],
              "calculatedIncomeTax": "3,000,000",
              "incomeTaxDeductionList": [
                {
                  "amount": "100,000",
                  "deductionClassification": "보험료"
                },
                {
                  "amount": "200,000",
                  "deductionClassification": "교육비"
                },
                {
                  "amount": "150,000",
                  "deductionClassification": "기부금"
                },
                {
                  "amount": "4,400,000",
                  "deductionClassification": "의료비"
                },
                {
                  "amount": "6,000,000",
                  "deductionClassification": "퇴직연금"
                }
              ]
            },
            "appVer": "2021112501",
            "errMsg": "",
            "company": "",
            "svcCd": "test01",
            "hostNm": "jobis-codetest",
            "workerResDt": "2022-08-16T06:27:35.160789",
            "workerReqDt": "2022-08-16T06:27:35.160851"
          },
          "errors": {
            "code": null,
            "message": null
          }
        },
        "status": 200,
        "message": "작업을 완료하였습니다."
      }
      ```
  - 스크랩을 이미 실행한 회원에 대해 스크랩 요청이 들어왔을 때 (400)
    - 기본적으로 발생할 수 없는 에러임. 이미 스크랩을 수행했다면 캐싱된 결과값이 나가기 때문임. 하지만 만일을 대비하여 예외처리를 해놓음.
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "이미 스크랩된 유저입니다."
    }
    ```
#### Presentation layer

###### Controller(Controller.java)

```java
@RestController
@RequiredArgsConstructor
public class Controller {

    private final UserService userService;
    
    @PostMapping(path = "scrap", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<ScrapResponseDto> scrap(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        return success(userService.scrap(authentication.userId));
    }
}

```
- Spring Security Context로 부터 인증된 회원의 정보를 `authentication` 파라미터로 넘겨 받음.
- `authentication` 파라미터에서 회원 아이디를 추출하여 `userService#scrap`을 호출함.


#### business layer

###### UserService(UserServiceImpl.java)

```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
    private final ScrapApiClient scrapApiClient;
    private final UsersRepository usersRepository;
    private final IncomeTaxDeductionRepository incomeTaxDeductionRepository;
    
    @Cacheable("scrap")
    @Transactional
    @Override
    public ScrapResponseDto scrap(String userId) {
        UsersEntity usersEntity = usersRepository.findByUserId(userId)
                .orElseThrow(NotUserIdFoundException::new);
        if (Boolean.TRUE.equals(usersEntity.isScraped())) {
          throw new BusinessException("이미 스크랩된 유저입니다.", ApiStatus.BAD_REQUEST);
        }
    
        /* scrap api 요청 */
        ScrapResponseDto scrapResponseDto = scrapApiClient.requestScrap(ScrapRequest.of(
                usersEntity.getName(),
                usersEntity.getRegNo()
        ));
    
        /* 데이터 추출 */
        ScrapResponseDto.ResponseData data = scrapResponseDto.getData();
        ScrapResponseDto.JsonList jsonList = data.getJsonList();
    
        double totalSalaryAmount = data.getJsonList().getIncomeInfoList().stream()
                .mapToDouble(incomeInfo -> {
                  try {
                    return decimalFormat.parse(incomeInfo.getTotalSalaryAmount()).doubleValue();
                  } catch (ParseException e) {
                    throw new IllegalStateException("스크랩 api로 부터 반환된 값이 format에 맞지않습니다.");
                  }
                })
                .sum();
        double calculatedIncomeTax;
        try {
          calculatedIncomeTax = decimalFormat.parse(jsonList.getCalculatedIncomeTax()).doubleValue();
        } catch (ParseException e) {
          throw new IllegalStateException("스크랩 api로 부터 반환된 값이 format에 맞지않습니다.");
        }
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
}
```
- scrap api는 여러 번 호출해도 결과값이 변하지 않는 멱등성을 띄기 때문에 본 api에서는 해당 결과를 `@Cacheable("scrap")`를 통해 캐싱처리 하였음. 조회시 길게는 20초까지도 걸리는 api를 매번 호출하기에는 리소스 낭비가 발생할 것으로 사료되어 캐싱 처리함.
  - 캐싱은 **스프링 캐시 추상화**에서 지원하는 `SimpleCache`를 활용하였으며 `ConcurrentHashMap` 자료구조를 활용해 메모리에 담김. 실제로는 `Redis`와 같은 제품을 이용해 구성하는 것이 바람직함.
  - `application.yml`에 `spring.cache.cache-names: scrap` 설정값을 주입해주어, scrap 이름의 캐시를 생성하였음.
- `ScrapApiClient#scrap`를 통해 외부 scrap api 서버와 통신하여 데이터를 조회함.
- 조회된 데이터를 담은 `ScrapResponseDto` 객체에서 편의상 계산에 필요한 결과값들만 추출하여 JPA를 활용해 영속화함.
  - `UsersEntity`에 포함된 `totalSalaryAmount`에 **총지급액**을 고 `calculatedIncomeTax`에 **산출세액**을 담음.
  - 또한 계산에 필요한 소득공제 데이터를 `IncomeTaxDeductionEntity`로 생성하여 `UsersEntity`와 연관관계 매핑을 맺어주고 `JpaRepository#save`를 통해 DB에 저장함. 
- 마지막으로 `UsersEntity`의 `scrap(boolean)` 상태값을 `true`로 변경해주고 조회된 스크랩 데이터를 반환함.
- 금액 데이터를 다룰 때는 `#,###.###` 형식의 `DecimalFormat`를 활용함. 이유는 scrap api 데이터를 조회해보다가 소수점이 포함된 데이터가 반환되는 케이스도 있기 때문임.

###### 외부 scrap api를 호출하기 위한 Client 클래스 정의

본 api에서는 `RestTemplate`을 이용한 http 통신을 활용하여 scrap api 서버와 통신함. scrap api 서버와 통신할 수 있는 전략은 언제든지 변경되거나 추가될 수 있기 때문에 전략 패턴으로 구성하였음.

[ScrapApiClient.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fcommon%2FScrapApiClient.java)
```java
public interface ScrapApiClient {

    ScrapResponseDto requestScrap(ScrapRequest request);
}

```

[RestTemplateScrapApiClient.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fcommon%2FRestTemplateScrapApiClient.java)
```java
@Component
@RequiredArgsConstructor
public class RestTemplateScrapApiClient implements ScrapApiClient {

    private final RestTemplate restTemplate;

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

```

###### model

[ScrapRequest.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fcommon%2Fmodel%2FScrapRequest.java)

```java
@Data
@AllArgsConstructor(staticName = "of")
public class ScrapRequest {

   private String name;
   private String regNo;

   @Override
   public String toString() {
      return new ToStringBuilder(this)
              .append("name", name)
              .append("regNo", "[PROTECTED]")
              .toString();
   }
}
```

[ScrapResponseDto.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fcommon%2Fmodel%2FScrapResponseDto.java)

```java
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
```
- `@JsonAlias`로 외부 scrap api로 부터 반환되는 json 요소들과 매핑함.
- `IncomeTaxDeduction`는 `$.data.jsonlist.소득공제`에 담기는 값들과 매핑되며 본 api에서는 { 총납임금액, 금액 } 두 필드를 같은 필드에 매핑함.

#### persistence layer
- IncomeTaxDeductionRepository.java; `UsersRepository`와 마찬가지로 `JpaRepository<IncomeTaxDeductionEntity, Long>`를 상속받은 인터페이스로 작성됨.
- UsersRepository.java


## 5. 결정세액과 퇴직연금세액공제금액 계산 및 조회

- api path: /refund
- http method: GET
- Request 예시
  - Headers
  - `Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzenNfeW9va3llbmdobyIsImlhdCI6MTcwNTM5NDk1MCwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMiJ9.7nLtGztJEsSFCubvLOFzRNTBQqd2JiiZEKFlVKEJrIo`
- Responses 예시
  - 성공 (200)
    ```json
    {
      "success": true,
      "response": {
        "이름": "홍길동",
        "결정세액": "0",
        "퇴직연금세액공제": "900,000"
      },
      "status": 200,
      "message": "작업을 완료하였습니다."
    }
    ```
  - 스크랩을 한 번도 실행하지 않은 유저가 refund 조회 요청을 한 경우 (400)
    ```json
    {
      "success": false,
      "response": null,
      "status": 400,
      "message": "환급액 조회 전에 스크랩을 먼저 진행해주십시오."
    }
    ```
    
#### Presentation layer

###### Controller(Controller.java)

```java
@RestController
@RequiredArgsConstructor
public class Controller {
  
    private final UserService userService;
    
    @GetMapping(path = "refund", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiUtils.ApiResult<RefundDto> refund(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
      return success(userService.refund(authentication.userId));
    }
}
```

#### business layer

###### UserService(UserServiceImpl.java)

```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.###");
    private final UsersRepository usersRepository;
    
    @Override
    public RefundDto refund(String userId) {
        UsersEntity usersEntity = usersRepository.findByUserIdJoinFetchIncomeTaxDeductionEntities(userId)
                .orElseThrow(NotUserIdFoundException::new);
        if (Boolean.FALSE.equals(usersEntity.isScraped())) {
          throw new BusinessException("환급액 조회 전에 스크랩을 먼저 진행해주십시오.", ApiStatus.BAD_REQUEST);
        }
        List<IncomeTaxDeductionEntity> incomeTaxDeductionEntities = usersEntity.getIncomeTaxDeductionEntities();
  
        double taxDetermined = usersEntity.getCalculatedIncomeTax() - usersEntity.getCalculatedIncomeTax() * 0.55;
        double totalSalaryAmount = usersEntity.getTotalSalaryAmount();
        double retirementPensionTaxCredit = 0.0;
        double totalSpecialTaxCredit = 0.0;
        double standardTaxCredit = 0.0;
  
        for (IncomeTaxDeductionEntity incomeTaxDeductionEntity: incomeTaxDeductionEntities) {
          String classifier = incomeTaxDeductionEntity.getDeductionClassification();
          Deduction deduction = Deduction.of(classifier)
                  .orElseThrow(() -> new IllegalStateException("해당 소득공제 타입과 일치하는 com.kyeongho.common.definitions.Deduction이 없습니다."));
          double amount = incomeTaxDeductionEntity.getAmount();
          if (deduction == Deduction.MEDICAL_EXPENSES) {
            amount = amount - totalSalaryAmount * 0.03;
            amount = Math.max(amount, 0.0);
          }
          double result = deduction.calculate(amount);
          if (deduction == Deduction.RETIREMENT_PENSION) {
            retirementPensionTaxCredit = result;
          } else {
            totalSpecialTaxCredit += result;
          }
        }
  
        if (totalSpecialTaxCredit < 130000.0) {
          standardTaxCredit = 130000.0;
          totalSpecialTaxCredit = 0;
        }
        taxDetermined = taxDetermined - totalSpecialTaxCredit - standardTaxCredit - retirementPensionTaxCredit;
        if (taxDetermined < 0) {
          taxDetermined = 0.0;
        }
        return RefundDto.of(
                usersEntity.getName(),
                decimalFormat.format(taxDetermined),
                decimalFormat.format(retirementPensionTaxCredit)
        );
    }
}
```
- 회원의 아이디로 `UsersEntity`를 조회하여 페치 조인을 통해 연관관계가 있는 `IncomeTaxDeductionEntity`들도 함께 조회함
- 조회된 데이터를 계산하여 결과를 도출함.
  - 계산 중 소득공제 및 퇴직연금세액공제금액에 대한 계산은 [Deduction.java](src%2Fmain%2Fjava%2Fcom%2Fkyeongho%2Fcommon%2Fdefinitions%2FDeduction.java) Enum 클래스에 정의하여 활용하였음.
    ```java
    public enum Deduction {
        INSURANCE_PREMIUM(         
                "보험료",         
                amount -> amount * 0.12 
        ),   
        EDUCATION_EXPENSES(         
                "교육비",         
                amount -> amount * 0.15 
        ),   
        DONATIONS(         
                "기부금",         
                amount -> amount * 0.15 
        ),   
        MEDICAL_EXPENSES(         
                "의료비",         
                amount -> amount * 0.15 
        ),   
        RETIREMENT_PENSION(         
                "퇴직연금",         
                amount -> amount * 0.15 
        ),   
        ;
        private final String classifier;
        private final DoubleUnaryOperator calculator;
    
        Deduction(String classifier, DoubleUnaryOperator calculator) {     
            this.classifier = classifier;     
            this.calculator = calculator; 
        }
        
        public double calculate(Double amount) {     
            return calculator.applyAsDouble(amount); 
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
    ```


#### persistence layer

UsersRepository.java

```java

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    // ...
    @Query("select u from UsersEntity u join fetch u.incomeTaxDeductionEntities where u.userId = :userId ")
    Optional<UsersEntity> findByUserIdJoinFetchIncomeTaxDeductionEntities(@NonNull String userId);
    // ...
}

```
- `UsersEntity` 조회시 연관관계가 있는 `IncomeTaxDeductionEntity`를 한 번에 들고오도록 jpql을 이용해 fetch join 실행 
# 테스트
테스트와 관련된 작업물들이 모두 포함된 `/src/test`의 구조는 다음과 같습니다.
```text
test
├── java
│   └── com
│       └── kyeongho
│           ├── common
│           │   └── ScarpApiTest.java
│           ├── config
│           │   ├── ScrapApiClientTestConfig.java
│           │   └── ControllerTestConfig.java
│           ├── security
│           │   ├── WithMockJwtAuthentication.java
│           │   └── WithMockJwtAuthenticationSecurityContextFactory.java
│           └── biz
│               ├── controller
│               │   └── ControllerTest.java
│               └── service
│                   └── UserServiceTest.java
└── resources
    └── ScrapResponse.json
```
##### 테스트 목록

![테스트 결과 캡쳐.png](static%2Fimages%2F%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EA%B2%B0%EA%B3%BC%20%EC%BA%A1%EC%B3%90.png)

테스트 클래스는 크게 3가지가 존재합니다.
- ControllerTest.java; Controller를 테스트하는 클래스, `@WebMvcTest` 어노테이션을 적용하여 간소화된 스프링 컨텍스트로 테스트를 실행함. 발생 가능한 BusinessException들을 포함하여 테스트함
- UserServiceTest.java]; UserService를 테스트 하는 클래스, `@SpringBootTest` 어노테이션으로 통합 테스트를 실시하며 정상적인 흐름 외에도 UsersService에서 발생할 수 있는 BusinessException들도 포함하여 테스트함.
- ScarpApiTest.java; 외부 scrap API를 호출하는 테스트 클래스

또한 `UserServiceTest`와 `ControllerTest`에 필요한 추가적인 정의를 위해 아래의 클래스들도 정의되어 있습니다.
- ScrapApiClientTestConfig.java; 실제 외부 scrap api와 통신하며 테스트를하면 각 테스트가 최대 20초가 걸릴 수 있으므로 `ScrapApiClient`의 빈을 Mokito를 활용하여 MockBean으로 등록함. 미리 '홍길동' 회원의 스크랩 결과값을 반환하도록 설정함.
- ControllerTestConfig.java; `UserServiceTest`에만 적용되며, 로그인 테스트에 필요한 `Jwt` 빈과 `usersCanSignUpMap` 빈을 등록함.

## 스웨거를 통한 API 테스트 참고 사항
- 스웨거 UI 접근 url: /swagger-ui.html
##### 토큰 인증 적용 방법
![토큰 테스트 방법.png](static%2Fimages%2F%ED%86%A0%ED%81%B0%20%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EB%B0%A9%EB%B2%95.png)
1. 로그인 요청을 성공하여 받은 토큰을 등록하기 위해 Authorize 버튼을 클릭합니다.
2. value 텍스트 필드에 토큰을 입력해줍니다.
3. Authorize 버튼을 클릭하여 토큰 인증을 적용하여 나머지 api들을 테스트 하십시오.