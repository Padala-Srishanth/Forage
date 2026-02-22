package com.jpmc.midascore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MidasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidasCoreApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate(
            org.springframework.boot.web.client.RestTemplateBuilder builder) {
        return builder.build();
    }

}
