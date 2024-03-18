package org.chusete.reactorpatterns.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.chusete.reactorpatterns.controller.*;
import org.chusete.reactorpatterns.model.event.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class StreamsConfiguration {

    // Reactor basic processors
    @Bean
    public Function<Flux<String>, Flux<Message<ProcessStartedEvent>>> startFlow(
            ReactorBasicProcessorExample reactorBasicProcessorExample
    ) {
        return reactorBasicProcessorExample::startFlow;
    }

    @Bean
    public Function<Flux<ProcessStartedEvent>, Flux<Message<ProcessCompletedEvent>>> enrichProcessing(
            ReactorBasicProcessorExample reactorBasicProcessorExample
    ) {
        return reactorBasicProcessorExample::enrichProcessing;
    }

    // KStream processors
    @Bean
    public Function<KStream<String, ProcessCompletedEvent>, KStream<String, KStreamOutputEvent>> windowedProcessing(
            KStreamProcessorExample kStreamProcessorExample
    ) {
        return kStreamProcessorExample::windowedProcessing;
    }

    @Bean
    public Function<KStream<String, ProcessCompletedEvent>, KStream<String, KStreamOutputEvent>> keyGroupingProcessing(
            KStreamProcessorExample kStreamProcessorExample
    ) {
        return kStreamProcessorExample::keyGroupingProcessing;
    }

    // Fork-join pattern
    @Bean
    public Function<Flux<ProcessStartedEvent>, Flux<Message<ForkedEvent>>> forkPattern(
            ForkJoinPatternProcessorExample forkJoinPatternProcessorExample
    ) {
        return forkJoinPatternProcessorExample::forkPattern;
    }

    @Bean
    public Function<KStream<String, ForkedEvent>, KStream<String, ProcessCompletedEvent>> joinPattern(
            ForkJoinPatternProcessorExample forkJoinPatternProcessorExample
    ) {
        return forkJoinPatternProcessorExample::joinPattern;
    }

    // Check duplicates pattern
    @Bean
    public Function<KStream<String, ForkedEvent>, KStream<String, ForkedEvent>>
    checkDuplicatesPattern(
            final CheckDuplicatesProcessorExample checkDuplicatesProcessorExample
    ) {
        return checkDuplicatesProcessorExample::checkDuplicatesPattern;
    };

    @Bean
    public Function<KStream<String, FlowEvent>, Function<KStream<String, FlowEvent>,
        Function<KStream<String, FlowEvent>, KStream<String, ProcessCompletedEvent>>>>
    flowEndJoiner(final FlowEndJoinerProcessor flowEndJoiner) {
        return sig -> (reqs -> (resps -> flowEndJoiner.waitForAsyncEventsFinalize(sig, reqs, resps)));
    }

    // Persistent key-value store (for duplicates check)
    @Bean
    public StoreBuilder<KeyValueStore<String, String>> duplicatedWordsStore() {
        return Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(CheckDuplicatesProcessorExample.CheckDuplicatesTransformer.STORE_NAME),
                Serdes.String(),
                Serdes.String()
        );
    }

    // Remove duplicates in tumbling window
    @Bean
    public Function<KStream<String, String>, KStream<String, String>> tumblingWindowDuplicates(
            KStreamTumblingWindowProcessorExample kStreamTumblingWindowProcessorExample
    ) {
        return kStreamTumblingWindowProcessorExample::tumblingWindowDuplicates;
    }

}
