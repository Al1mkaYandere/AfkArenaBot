package com.example.alimka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlimkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlimkaApplication.class, args);
    }

}
