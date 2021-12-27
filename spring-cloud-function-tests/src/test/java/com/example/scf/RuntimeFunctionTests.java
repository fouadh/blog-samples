package com.example.scf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest("spring.cloud.function.definition=capitalize|hello")
public class RuntimeFunctionTests {
    @Autowired
    public FunctionCatalog catalog;

    @Test
    void hello() {
        Function<String, String> hello = catalog.lookup(RoutingFunction.FUNCTION_NAME);
        assertThat(hello.apply("world")).isEqualTo("Hello, World");
    }
}
