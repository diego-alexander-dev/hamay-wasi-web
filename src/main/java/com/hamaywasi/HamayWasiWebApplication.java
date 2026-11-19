package com.hamaywasi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// arranque del portal web
@EnableAsync
@SpringBootApplication
public class HamayWasiWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HamayWasiWebApplication.class, args);
    }
}
