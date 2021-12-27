package com.example.scf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.function.Function;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<String, String> hello() {
        return input -> "Hello, " + input;
    }

    @Bean
    public Function<String, String> capitalize() {
        return input -> input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Bean
    public Function<User, String> helloUser() {
        return input -> "Hello, " + input.getFirstName();
    }

    @Bean
    public Hello helloPojo() {
        return new Hello();
    }

    @Bean
    public Function<Flux<String>, Flux<String>> helloReactive() {
        return input -> input.map(s -> "Hello, " + s);
    }

    @Bean
    public Function<Tuple2<String, String>, String> helloFirstNameLastName() {
        return input -> "Hello, " + input.getT1() + " " + input.getT2();
    }

    public static class Hello {
        public String doSomething(String input) {
            return "Hello, " + input + " from POJO";
        }
    }

    private static class User {
        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

}
