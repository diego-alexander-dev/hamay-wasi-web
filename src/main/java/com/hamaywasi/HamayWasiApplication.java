package com.hamaywasi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// clase principal para iniciar la aplicacion
@EnableAsync
@SpringBootApplication
public class HamayWasiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HamayWasiApplication.class, args);
    }
}
