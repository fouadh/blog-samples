package scf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.Function;

@Configuration
@Slf4j
public class ScatterGather {
    @Bean
    public Function<Flux<Integer>, Tuple2<Flux<Integer>, Flux<Integer>>> scatter() {
        return order -> {
            var flux = order.publish().autoConnect(2);

            Sinks.Many<Integer> multipleOf3 = Sinks.many().unicast().onBackpressureError();
            var multipleOf3Flux = flux
                    .filter(n -> n % 3 == 0)
                    .doOnNext(multipleOf3::tryEmitNext);

            Sinks.Many<Integer> nonMultipleOf3 = Sinks.many().unicast().onBackpressureError();
            var nonMultipleOf3Flux = flux
                    .filter(n -> n % 3 != 0)
                    .doOnNext(nonMultipleOf3::tryEmitNext);

            return Tuples.of(
                    multipleOf3.asFlux().doOnSubscribe(x -> multipleOf3Flux.subscribe()),
                    nonMultipleOf3.asFlux().doOnSubscribe(x -> nonMultipleOf3Flux.subscribe())
            );
        };
    }

    @Bean
    public Function<Tuple2<Flux<Integer>, Flux<Integer>>, Flux<Integer>> gather() {
        return tuple -> {
            Flux<Integer> multipleOf3 = tuple.getT1();
            Flux<Integer> nonMultipleOf3 = tuple.getT2();

            return Flux
                    .merge(multipleOf3, nonMultipleOf3)
                    .log();
        };
    }
}
