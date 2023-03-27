package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.chusete.reactorpatterns.model.event.KStreamOutputEvent;
import org.chusete.reactorpatterns.model.event.ProcessCompletedEvent;
import org.chusete.reactorpatterns.model.event.ProcessStartedEvent;
import org.chusete.reactorpatterns.service.MiscService;
import org.chusete.reactorpatterns.util.CommonUtils;
import org.chusete.reactorpatterns.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Component
public class ReactorProcessorExample {
    @Autowired
    MiscService miscService;

    private final static Duration DURATION = Duration.ofSeconds(60);

    public Flux<Message<ProcessStartedEvent>> startFlow(Flux<String> inbound) {
        return inbound
                .map(s -> {
                    var res = new ProcessStartedEvent(s);
                    log.debug(res.toString());
                    return res;
                })
                .map(MessageUtils::setKey);
    }

    public Flux<Message<ProcessCompletedEvent>> enrichProcessing(Flux<ProcessStartedEvent> inbound) {
        return inbound
                .map(event -> {
                    var originalString = event.getOriginalString();
                    var res = new ProcessCompletedEvent(
                            originalString,
                            originalString.toUpperCase(),
                            miscService.wordCount(originalString),
                            miscService.letterCount(originalString),
                            event
                    );
                    log.debug(res.toString());
                    return res;
                })
                .map(MessageUtils::setKey);
    }

    /**
     * Groups all incoming events by the same fake key in order to apply window processing
     * @param inbound incoming events
     * @return outgoing events
     */
    public KStream<String, KStreamOutputEvent> windowedProcessing(KStream<String, ProcessCompletedEvent> inbound) {
        return inbound
                .selectKey((k, v) -> "word-count")
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(DURATION))
                .aggregate(
                        KStreamOutputEvent::new,
                        this::aggregation,
                        Materialized
                                .<String, KStreamOutputEvent, WindowStore<Bytes, byte[]>>as("words-per-min-windowed")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(KStreamOutputEvent.class))
                                .withRetention(DURATION.multipliedBy(2))
                )
                .toStream()
                .map((k, v) -> new KeyValue<>(v.getTimeStamp(), v));
    }

    /**
     * Groups incoming events by a timestamp key ('YYYY-MM-DDTHH:mm') in order to generate a single group every minute
     * @param inbound incoming events
     * @return outgoing events
     */
    public KStream<String, KStreamOutputEvent> keyGroupingProcessing(KStream<String, ProcessCompletedEvent> inbound) {
        return inbound
                .selectKey((k, v) -> v.getTimeStamp().substring(0, 16))
                .groupByKey()
                .aggregate(
                        KStreamOutputEvent::new,
                        this::aggregation,
                        Materialized
                                .<String, KStreamOutputEvent, KeyValueStore<Bytes, byte[]>>as("words-per-min-grouped")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(KStreamOutputEvent.class))
                                .withRetention(DURATION.multipliedBy(2))
                )
                .toStream()
                .filter((k, v) -> CommonUtils.isPastTimeInMinutes(v.getTimeStamp()));
    }

    private KStreamOutputEvent aggregation(
            final String key,
            final ProcessCompletedEvent current,
            final KStreamOutputEvent aggregate
    ) {
        var ac = Optional.ofNullable(aggregate.getWordCount()).orElse(0);
        var in = current.getWordCount();

        aggregate.setWordCount(ac + in);
        aggregate.setTimeStamp(current.getTimeStamp());

        var l = Optional.ofNullable(aggregate.getWords()).orElse(new ArrayList<>());
        l.add(current.getOriginalString());
        aggregate.setWords(l);

        return aggregate;
    }

}
