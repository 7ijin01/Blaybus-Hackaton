package com.blaybus;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class EnvCheck {

    @Value("${GOOGLE_CLIENT_ID:NOT_SET}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET:NOT_SET}")
    private String googleClientSecret;

    @PostConstruct
    public void checkEnv() {
        System.out.println("GOOGLE_CLIENT_ID: " + googleClientId);
        System.out.println("GOOGLE_CLIENT_SECRET: " + googleClientSecret);
    }
}