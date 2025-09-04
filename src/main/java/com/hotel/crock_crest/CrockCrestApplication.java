package com.hotel.crock_crest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrockCrestApplication {

    public static void main(String[] args) {
        EnvLoader.load();
        SpringApplication.run(CrockCrestApplication.class, args);
    }

}
