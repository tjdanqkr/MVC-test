package com.plus.service.user.presentation.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SignUpRequestTest {
    private final Validator validator;
    SignUpRequestTest() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("생성 성공")
    void constructorSuccess(){
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "username", "password");

        // When & Then
        assertEquals(0, validator.validate(request).size());
    }
    @Test
    @DisplayName("이메일 형식이 다르면 예외 발생")
    void emailFormatFail(){
        // Given
        SignUpRequest request = new SignUpRequest("test", "username", "password");

        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("유효한 이메일 형식이 아닙니다.", validate.iterator().next().getMessage());
    }
    @Test
    @DisplayName("이메일이 비어있으면 예외 발생")
    void emailEmptyFail(){
        // Given
        SignUpRequest request = new SignUpRequest("", "username", "password");

        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("이메일은 필수 입력 값입니다.", validate.iterator().next().getMessage());
    }
    @Test
    @DisplayName("사용자 이름이 비어있으면 예외 발생")
    void usernameEmptyFail(){
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "", "password");
        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(2, validate.size());
        assertTrue(validate.stream().anyMatch(v-> v.getMessage().equals("사용자 이름은 필수 입력 값입니다.")));
        assertTrue(validate.stream().anyMatch(v-> v.getMessage().equals("사용자 이름은 3자 이상, 20자 이하여야 합니다.")));
    }
    @Test
    @DisplayName("사용자 이름이 3자 미만이면 예외 발생")
    void usernameTooShortFail(){
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "us", "password");
        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("사용자 이름은 3자 이상, 20자 이하여야 합니다.", validate.iterator().next().getMessage());
    }
    @Test
    @DisplayName("사용자 이름이 20자 초과면 예외 발생")
    void usernameTooLongFail(){
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "usernameusernameusername", "password");
        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("사용자 이름은 3자 이상, 20자 이하여야 합니다.", validate.iterator().next().getMessage());
    }
    @Test
    @DisplayName("비밀번호가 비어있으면 예외 발생")
    void passwordEmptyFail() {
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "username", "");

        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(2, validate.size());

        assertTrue(validate.stream().anyMatch(v-> v.getMessage().equals("비밀번호는 최소 8자 이상이어야 합니다.")));
        assertTrue(validate.stream().anyMatch(v-> v.getMessage().equals("비밀번호는 필수 입력 값입니다.")));

    }

    @Test
    @DisplayName("비밀번호가 8자 미만이면 예외 발생")
    void passwordTooShortFail() {
        // Given
        SignUpRequest request = new SignUpRequest("test@test.com", "username", "pass");

        // When & Then
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);
        assertEquals(1, validate.size());
        assertEquals("비밀번호는 최소 8자 이상이어야 합니다.", validate.iterator().next().getMessage());
    }
}