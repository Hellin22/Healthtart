package com.dev5ops.healthtart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDiscoveryClient
//@EnableFeignClients
public class HealthtartApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthtartApplication.class, args);
    }
}
