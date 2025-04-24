package com.plus.service.user.presentation.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignInRequestTest {
    private final Validator validator;

    SignInRequestTest() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("생성 성공")
    void constructorSuccess() {
        // Given
        SignInRequest request = new SignInRequest("test@test.com", "password");

        // When & Then
        assertEquals(0, validator.validate(request).size());
    }

    @Test
    @DisplayName("이메일 형식이 다르면 예외 발생")
    void emailFormatFail() {
        // Given
        SignInRequest request = new SignInRequest("invalid-email", "password");

        // When & Then
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("유효한 이메일 형식이 아닙니다.", validate.iterator().next().getMessage());
    }

    @Test
    @DisplayName("이메일이 비어있으면 예외 발생")
    void emailEmptyFail() {
        // Given
        SignInRequest request = new SignInRequest("", "password");

        // When & Then
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("이메일은 필수 입력 값입니다.", validate.iterator().next().getMessage());
    }

    @Test
    @DisplayName("비밀번호가 비어있으면 예외 발생")
    void passwordEmptyFail() {
        // Given
        SignInRequest request = new SignInRequest("test@test.com", "");

        // When & Then
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);
        assertEquals(2, validate.size());

        assertTrue(validate.stream().anyMatch(v -> v.getMessage().equals("비밀번호는 필수 입력 값입니다.")));
        assertTrue(validate.stream().anyMatch(v -> v.getMessage().equals("비밀번호는 최소 8자 이상이어야 합니다.")));
    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 예외 발생")
    void passwordTooShortFail() {
        // Given
        SignInRequest request = new SignInRequest("test@test.com", "pass");

        // When & Then
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("비밀번호는 최소 8자 이상이어야 합니다.", validate.iterator().next().getMessage());
    }
}