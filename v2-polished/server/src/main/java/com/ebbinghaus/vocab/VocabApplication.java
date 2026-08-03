package com.ebbinghaus.vocab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ebbinghaus.vocab.mapper")
public class VocabApplication {
    public static void main(String[] args) {
        SpringApplication.run(VocabApplication.class, args);
    }
}
