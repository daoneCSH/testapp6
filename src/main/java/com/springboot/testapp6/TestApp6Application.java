package com.springboot.testapp6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {
//        org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration.class
//})
@SpringBootApplication
public class TestApp6Application {
    public static void main(String[] args) {
        SpringApplication.run(TestApp6Application.class, args);
    }

}
