package dev.nano.herbclassifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class HerbClassifierApplication {
    public static void main(String[] args) {
        SpringApplication.run(HerbClassifierApplication.class, args);
    }

    @Bean
    public ApplicationRunner atStartup() {
        return args -> {
            log.info("Herb Classifier Application is Up & Running... ");
        };
    }
}
