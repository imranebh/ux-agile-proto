package net.est.uxagile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UxagileApplication {

    public static void main(String[] args) {
        SpringApplication.run(UxagileApplication.class, args);
    }
}
