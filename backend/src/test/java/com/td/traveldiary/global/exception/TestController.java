package com.td.traveldiary.global.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestController {
    @GetMapping("/test/business-exception")
    void throwBusinessException() {
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    @GetMapping("/test/unhandled-exception")
    void throwUnhandledException() {
        throw new RuntimeException("unexpected");
    }

    @PostMapping("/test/validation")
    void throwValidationException(@RequestBody @Valid TestRequest request) {}

    record TestRequest(@NotBlank String name) {}
}
