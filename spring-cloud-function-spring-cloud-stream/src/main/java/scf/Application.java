package scf;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.batch.BatchingStrategy;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Supplier<String> supplier() {
        return () -> {
            log.info(">> supplier called");
            return "hello from Supplier";
        };
    }

    @Bean
    public Supplier<Flux<String>> reactiveSupplier() {
        return () -> {
            log.info(">> reactive supplier called");
            return Flux.fromStream(Stream.generate(
                    () -> {
                        try {
                            Thread.sleep(2000);
                            return "hello from reactive supplier";
                        } catch (Exception e) {
                        }
                        return "end of reactive supplier";
                    }
            ));
        };
    }

    @PollableBean
    public Supplier<Flux<String>> finiteReactiveSupplier() {
        return () -> {
            log.info(">> reactive supplier called");
            return Flux.just("hello", "from", "finite", "reactive", "supplier");
        };
    }

    @Bean
    public Consumer<String> consumer() {
        return log::info;
    }

    @Bean
    public Consumer<User> pojoConsumer() {
        return user -> log.info(user.toString());
    }

    @Bean
    public Function<String, String> function() {
        return input -> "Hello, " + input;
    }

    @Bean
    public Function<Flux<String>, Flux<String>> reactiveFunction() {
        return input -> input
                .log()
                .map(s -> s.toUpperCase());
    }

    @Bean
    public Function<Order, List<Message<OrderItem>>> multipleMessages() {
        return input -> input
                .getItems()
                .stream()
                .map(item -> MessageBuilder.withPayload(item).build())
                .collect(Collectors.toList());
    }

    @Bean
    public Function<Flux<Order>, Flux<OrderItem>> reactiveMultipleMessages() {
        return input -> input
                .flatMap(order -> Flux.fromIterable(order.getItems()));
    }

    @Bean
    public Function<List<String>, String> batchConsumer() {
        return input -> {
            log.info("Consuming: {}", input);
            return String.join(";", input);
        };
    }


    @Bean
    @ConditionalOnExpression("'${spring.cloud.function.definition}'.contains('batchConsumer')" )
    public ApplicationRunner runner(RabbitTemplate rabbitTemplate) {
        BatchingStrategy strategy = new SimpleBatchingStrategy(5, 25_000, 1_000);
        TaskScheduler scheduler = new ConcurrentTaskScheduler();
        BatchingRabbitTemplate template = new BatchingRabbitTemplate(strategy, scheduler);
        template.setConnectionFactory(rabbitTemplate.getConnectionFactory());

        return args -> {
            for (var i = 0; i < 12; i++) {
                var props = new MessageProperties();
                props.setContentType("text/plain");
                org.springframework.amqp.core.Message message = new org.springframework.amqp.core.Message(("message " + i).getBytes(StandardCharsets.UTF_8), props);
                template.send("batchConsumer-in-0", "", message, null);
            }
        };
    }



    @ToString
    @Data
    static class Order {
        private List<OrderItem> items;
    }

    @ToString
    @Data
    static class OrderItem {
        private String id;
    }

    @ToString
    @Setter
    static class User {
        String name;
    }
}
