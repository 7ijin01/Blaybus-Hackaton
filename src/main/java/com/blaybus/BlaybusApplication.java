package com.blaybus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BlaybusApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlaybusApplication.class, args);
    }
}
