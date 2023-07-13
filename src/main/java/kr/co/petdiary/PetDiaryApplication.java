package kr.co.petdiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class PetDiaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetDiaryApplication.class, args);
    }
}
