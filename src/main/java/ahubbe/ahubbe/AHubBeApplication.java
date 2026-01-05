package ahubbe.ahubbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AHubBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AHubBeApplication.class, args);
    }
}
