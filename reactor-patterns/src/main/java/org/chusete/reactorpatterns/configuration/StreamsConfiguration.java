package org.chusete.reactorpatterns.configuration;

import org.apache.kafka.streams.kstream.KStream;
import org.chusete.reactorpatterns.controller.ReactorProcessorExample;
import org.chusete.reactorpatterns.model.event.KStreamOutputEvent;
import org.chusete.reactorpatterns.model.event.ProcessCompletedEvent;
import org.chusete.reactorpatterns.model.event.ProcessStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class StreamsConfiguration {

    // Reactor processors
    @Bean
    public Function<Flux<String>, Flux<Message<ProcessStartedEvent>>> startFlow(ReactorProcessorExample reactorProcessorExample) {
        return reactorProcessorExample::startFlow;
    }

    @Bean
    public Function<Flux<ProcessStartedEvent>, Flux<Message<ProcessCompletedEvent>>> enrichProcessing(ReactorProcessorExample reactorProcessorExample) {
        return reactorProcessorExample::enrichProcessing;
    }

    @Bean
    public Function<KStream<String, ProcessCompletedEvent>, KStream<String, KStreamOutputEvent>> windowedProcessing(ReactorProcessorExample reactorProcessorExample) {
        return reactorProcessorExample::windowedProcessing;
    }

    @Bean
    public Function<KStream<String, ProcessCompletedEvent>, KStream<String, KStreamOutputEvent>> keyGroupingProcessing(ReactorProcessorExample reactorProcessorExample) {
        return reactorProcessorExample::keyGroupingProcessing;
    }

}
