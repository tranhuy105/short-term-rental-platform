package com.huy.airbnbserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AirbnbServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbnbServerApplication.class, args);
    }

}
