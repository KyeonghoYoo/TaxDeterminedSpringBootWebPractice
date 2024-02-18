package com.kyeongho.errors;

import com.kyeongho.common.ApiStatus;
import com.kyeongho.errors.execption.BusinessException;
import com.kyeongho.errors.execption.UnauthorizedException;
import com.kyeongho.utils.ApiUtils;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.kyeongho.utils.ApiUtils.error;

/**
 * Exception 핸들링을 위한 ControllerAdvice
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 11
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 */
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(Exception e) {
        log.error("handleUsernameNotFoundException", e);
        return newResponse(e.getMessage(), HttpStatus.NOT_FOUND);
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
