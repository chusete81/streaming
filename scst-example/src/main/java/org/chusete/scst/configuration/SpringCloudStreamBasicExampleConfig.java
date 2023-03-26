package org.chusete.scst.configuration;

import lombok.extern.slf4j.Slf4j;
import org.chusete.scst.service.MiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Spring Cloud Stream basic example: https://spring.io/guides/gs/spring-cloud-stream/
 */
@Slf4j
@Configuration
public class SpringCloudStreamBasicExampleConfig {

    // Java 8 Supplier ~ Scst Source: produces a new output value periodically
    @Bean
    public Supplier<String> sourceExample() {
        return () -> miscService.randomString();
    }

    // Java 8 Function ~ Scst Processor: processes every input value and sends it to the output
    @Bean
    public Function<String, String> functionExample() {
        return String::toUpperCase;
    }

    // Java 8 Consumer ~ Scst Sink: consumes every input value
    @Bean
    public Consumer<String> sinkExample() {
        return log::info;
    }


    @Autowired
    MiscService miscService;

}
