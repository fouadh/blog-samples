package com.example.scf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@FunctionalSpringBootTest
class FunctionsTests {
    @Autowired
    private FunctionCatalog catalog;

    @Test
    void basic() {
        Function<String, String> hello = catalog.lookup("hello");
        assertThat(hello.apply("world")).isEqualTo("Hello, world");
    }

    @Test
    void composition() {
        Function<String, String> hello = catalog.lookup("capitalize|hello");
        assertThat(hello.apply("world")).isEqualTo("Hello, World");
    }

    @Test
    void typeConversion() {
        Function<String, String> hello = catalog.lookup("helloUser");
        var answer = hello.apply("{\n" +
                " \"firstName\": \"Jane\",\n" +
                " \"lastName\": \"Doe\"\n" +
                "}");
        assertThat(answer).isEqualTo("Hello, Jane");

    }

    @Test
    void pojo() {
        Function<String, String> hello = catalog.lookup("helloPojo");
        assertThat(hello.apply("world")).isEqualTo("Hello, world from POJO");
    }

    @Test
    void pojoComposedWithNonPojo() {
        Function<String, String> hello = catalog.lookup("capitalize|helloPojo");
        assertThat(hello.apply("world")).isEqualTo("Hello, World from POJO");
    }

    @Test
    void multipleInputs() {
        Function<Tuple2<String, String>, String> hello = catalog.lookup("helloFirstNameLastName");
        assertThat(hello.apply(Tuples.of("Jane", "Doe"))).isEqualTo("Hello, Jane Doe");
    }

    @Test
    void reactive() {
        Function<Flux<String>, Flux<String>> hello = catalog.lookup("helloReactive");
        StepVerifier.create(hello.apply(Flux.just("foo", "bar")))
                .expectNext("Hello, foo", "Hello, bar")
                .verifyComplete();
    }

    @Test
    void reactiveComposedWithNonReactive() {
        Function<Flux<String>, Flux<String>> hello = catalog.lookup("capitalize|helloReactive");
        StepVerifier.create(hello.apply(Flux.just("foo", "bar")))
                .expectNext("Hello, Foo", "Hello, Bar")
                .verifyComplete();
    }
}
