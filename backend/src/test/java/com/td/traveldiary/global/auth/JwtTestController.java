package com.td.traveldiary.global.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTestController {

    @GetMapping("/test/secured")
    public String secured() {
        return "ok";
    }
}
