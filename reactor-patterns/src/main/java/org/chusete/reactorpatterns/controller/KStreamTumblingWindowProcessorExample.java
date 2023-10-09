package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class KStreamTumblingWindowProcessorExample {

    public KStream<String, String> tumblingWindow(KStream<String, String> inbound) {
        final var DURATION = Duration.ofSeconds(10L);

        final var store = Stores.persistentWindowStore("storeName", DURATION.multipliedBy(2), DURATION, false);
        return inbound
                .selectKey((k, str) -> str)

                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))

                .windowedBy(TimeWindows.ofSizeWithNoGrace(DURATION))

                .count()
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))

                .toStream()

                .peek((k, v) -> log.debug("(key, count) -> ({}, {})", k.key(), v))

                .map((k, v) -> KeyValue.pair(null, k.key()))
                ;
    }
}
