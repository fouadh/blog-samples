package com.fouadhamdi.scf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Supplier<String> supplier() {
        return () -> "hello from Supplier";
    }

    @Bean
    public Supplier<Welcome> pojoSupplier() {
        return () -> new Welcome("hello from POJO Supplier");
    }

    @AllArgsConstructor
    @Getter
    static class Welcome {
        String message;
    }

    @Bean
    public Consumer<String> consumer() {
        return log::info;
    }

    @Bean
    public Consumer<User> pojoConsumer() {
        return user -> log.info(user.toString());
    }

    @ToString
    @Setter
    static class User {
        String name;
    }

    @Bean
    public Function<String, String> function() {
        return input -> "Hello, " + input;
    }

    @Bean
    public Function<User, Welcome> pojoFunction() {
        return user -> new Welcome("Hello, " + user.name);
    }

    @Bean
    public Supplier<Flux<String>> reactiveSupplier() {
        return () -> Flux
                .interval(Duration.ofSeconds(1))
                .log()
                .map(Object::toString);
    }

    @Bean
    public Supplier<Flux<String>> finiteReactiveSupplier() {
        return () -> Flux.just("hello", "world");
    }
}
