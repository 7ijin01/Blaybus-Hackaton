package com.blaybus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class BlaybusApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlaybusApplication.class, args);
    }
}
