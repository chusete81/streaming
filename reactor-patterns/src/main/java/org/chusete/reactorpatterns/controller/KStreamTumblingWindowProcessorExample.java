package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class KStreamTumblingWindowProcessorExample {

    public KStream<String, String> tumblingWindowDuplicates(KStream<String, String> inbound) {
        final var DURATION = Duration.ofSeconds(30L);

        return inbound
                .selectKey((k, str) -> str)
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))

                .windowedBy(TimeWindows.ofSizeWithNoGrace(DURATION))
                .count(Materialized.as("tumblingWindowStore"))
                .toStream()
                .peek((k, c) -> log.debug("count(k, c) -> ({}, {})", k.key(), c))
                .filter((k, c) -> c == 1)
                .map((k, v) -> KeyValue.pair(null, k.key()));
    }
}
