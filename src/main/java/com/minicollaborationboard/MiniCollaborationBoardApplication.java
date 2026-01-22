package com.minicollaborationboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MiniCollaborationBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniCollaborationBoardApplication.class, args);
    }

}
