package org.chusete.reactorpatterns.configuration;

import org.chusete.reactorpatterns.service.MiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class ProducerConfiguration {
    @Autowired
    MiscService miscService;

    // Stream Cloud Stream standard producer
    @Bean
    public Supplier<String> randomProducer() {
        return () -> miscService.randomPhrase();
    }
}
