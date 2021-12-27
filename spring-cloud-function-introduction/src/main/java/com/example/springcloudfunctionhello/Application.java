package com.example.springcloudfunctionhello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.Function;

@SpringBootApplication
public class SpringCloudFunctionHelloApplication {

    public static void main(String[] args) {
//        var context = SpringApplication.run(SpringCloudFunctionHelloApplication.class, args);
//        var catalog = context.getBean(FunctionCatalog.class);

        // Basic
//        Function<String, String> hello = catalog.lookup("hello");
//        System.out.println(hello.apply("world"));

        // Composition
//        Function<String, String> function = catalog.lookup("capitalize|hello");
//        System.out.println(function.apply("world"));

        // Type Conversion
//        Function<String, String> function = catalog.lookup("helloUser");
//        System.out.println(function.apply("{\n" +
//                " \"firstName\": \"Jane\",\n" +
//                " \"lastName\": \"Doe\"\n" +
//                "}"));

        // POJO Function
//        Function<String, String> hello = catalog.lookup("helloPojo");
//        System.out.println(hello.apply("World"));

        // POJO Function composed with a non POJO
//        Function<String, String> hello = catalog.lookup("capitalize|helloPojo");
//        System.out.println(hello.apply("world"));

        // Multiple inputs
//        Function<Tuple2<String, String>, String> hello = catalog.lookup("helloFirstNameLastName");
//        System.out.println(hello.apply(Tuples.of("Jane", "Doe")));

        // Reactive
//        Function<Flux<String>, Flux<String>> hello = catalog.lookup("helloReactive");
//        hello.apply(Flux.just("Jane Doe", "John Doe"))
//                .subscribe(System.out::println);

        // Reactive & non-reactive composition
//        Function<Flux<String>, Flux<String>> hello = catalog.lookup("capitalize|helloReactive");
//        hello.apply(Flux.just("foo", "bar"))
//                .subscribe(System.out::println);

        // RoutingFunction.FUNCTION_NAME
        var context = SpringApplication.run(SpringCloudFunctionHelloApplication.class,
                "--spring.cloud.function.definition=capitalize|hello");
        var catalog = context.getBean(FunctionCatalog.class);
        Function<String, String> hello = catalog.lookup(RoutingFunction.FUNCTION_NAME);
        System.out.println(hello.apply("world"));
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
