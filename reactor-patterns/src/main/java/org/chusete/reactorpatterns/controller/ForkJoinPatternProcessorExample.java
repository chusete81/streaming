package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.chusete.reactorpatterns.model.event.ForkedEvent;
import org.chusete.reactorpatterns.model.event.JointEvent;
import org.chusete.reactorpatterns.model.event.ProcessCompletedEvent;
import org.chusete.reactorpatterns.model.event.ProcessStartedEvent;
import org.chusete.reactorpatterns.util.CommonUtils;
import org.chusete.reactorpatterns.util.MessageUtils;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ForkJoinPatternProcessorExample {

    public Flux<Message<ForkedEvent>> forkPattern(Flux<ProcessStartedEvent> inbound) {
        return inbound
                .flatMap(this::forkFromSingleEvent)
                .map(MessageUtils::setKey);
    }

    private Flux<ForkedEvent> forkFromSingleEvent(ProcessStartedEvent event) {
        var words = event.getOriginalString().split(" ");

        AtomicInteger counter = new AtomicInteger(0);

        var forkedList = Arrays.stream(words)
                .map(word -> new ForkedEvent(word, counter.incrementAndGet(), words.length, event))
                .collect(Collectors.toList());

        log.debug("processId: \"{}\", fork pattern: emitted {} events", event.getProcessId(), forkedList.size());

        return Flux.fromIterable(forkedList);
    }

    public KStream<String, ProcessCompletedEvent> joinPattern(KStream<String, ForkedEvent> inbound) {
        return inbound
                .mapValues((k, v) -> new JointEvent(v))
                //.selectKey((k, v) -> v.getProcessId())
                .groupByKey() //Grouped.with(Serdes.String(), new JsonSerde<>(JointEvent.class)))
                .aggregate(
                        JointEvent::new,
                        this::joinAggregation,
                        Materialized
                                .<String, JointEvent, KeyValueStore<Bytes, byte[]>>as("joinAggregationKTable")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(JointEvent.class))
                                //.withRetention(Duration.ofMinutes(2))
                )
                .toStream()
                .filter((k, v) -> v.getTotal().compareTo(v.getForkedEvents().size()) == 0)
                .peek((k, v) -> log.debug("processId: \"{}\", join pattern: {}", v.getProcessId(), v))
                .mapValues(this::transformToOutputType);
    }

    private JointEvent joinAggregation (
            final String key,
            final JointEvent current,
            final JointEvent aggregate
    ) {
        var currentForkedEvent = current.getForkedEvents().get(0);

        aggregate.setProcessId(current.getProcessId());
        aggregate.setTimeStamp(CommonUtils.getUTCTimeStamp());
        aggregate.setForkedEvents(Optional.ofNullable(aggregate.getForkedEvents()).orElse(new ArrayList<>()));
        aggregate.setTotal(currentForkedEvent.getTotal());
        aggregate.getForkedEvents().add(currentForkedEvent);

        return aggregate;
    }

    private ProcessCompletedEvent transformToOutputType(JointEvent jointEvent) {
        var str = jointEvent
                .getForkedEvents()
                .stream()
                .sorted(Comparator.comparingInt(ForkedEvent::getCounter))
                .map(ForkedEvent::getWord)
                .reduce((prev, curr) -> (prev + " " + curr).trim())
                .orElse("");

        return new ProcessCompletedEvent(
                str,
                str.toUpperCase(),
                str.split(" ").length,
                str.replaceAll(" ", "").length(),
                jointEvent
        );
    }
}
