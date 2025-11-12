package com.likedandylion.prome.global.exception;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.likedandylion.prome")
public class GlobalExceptionHandler {
    // --- 비즈니스 예외 공통 처리 ---
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        HttpStatus status;
        if (e instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (e instanceof ConflictException) {
            status = HttpStatus.CONFLICT;
        } else if (e instanceof PreconditionFailedException) {
            status = HttpStatus.PRECONDITION_FAILED; // 412
        } else if (e instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
        } else { // BadRequestException 등 기본
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, e.getCode(), e.getMessage(), null));
    }

    // --- Bean Validation (@Valid) 바디 에러 ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (a, b) -> a
                ));
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "VALIDATION_ERROR", "요청 값이 올바르지 않습니다.", errors));
    }

    // --- QueryParam/Form 바인딩 에러 ---
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBind(BindException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (a, b) -> a
                ));
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "BIND_ERROR", "요청 파라미터가 올바르지 않습니다.", errors));
    }

    // --- 경로/쿼리 파라미터 검증 (javax/jakarta Validation) ---
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "CONSTRAINT_VIOLATION", msg, null));
    }

    // --- 필수 파라미터 누락 ---
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "MISSING_PARAM", "필수 파라미터가 누락되었습니다: " + e.getParameterName(), null));
    }

    // --- JSON 파싱 실패 ---
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "INVALID_JSON", "요청 본문(JSON)을 읽을 수 없습니다.", null));
    }

    // --- 인증/인가 ---
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "ACCESS_DENIED", "접근 권한이 없습니다.", null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "UNAUTHORIZED", "인증이 필요합니다.", null));
    }

    // --- DB 제약/중복 ---
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, "DATA_INTEGRITY", "데이터 제약 조건을 위반했습니다.", null));
    }

    // --- 예상 못한 에러 ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception e) {
        // log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "INTERNAL_ERROR", "서버 오류가 발생했습니다.", null));
    }
}
