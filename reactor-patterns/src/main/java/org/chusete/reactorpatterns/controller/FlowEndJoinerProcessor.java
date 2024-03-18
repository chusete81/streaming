package org.chusete.reactorpatterns.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.chusete.reactorpatterns.model.event.FlowEvent;
import org.chusete.reactorpatterns.model.event.ProcessCompletedEvent;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Duration;

@Slf4j
@Component
public class FlowEndJoinerProcessor {

    public KStream<String, ProcessCompletedEvent> waitForAsyncEventsFinalize(
        KStream<String, FlowEvent> processSignalStream,
        KStream<String, FlowEvent> asyncSentRequestsStream,
        KStream<String, FlowEvent> asyncReceivedResponsesStream
    ) {
        // save signals in ktable
        var signalKTable = processSignalStream
            .selectKey((k, v) -> v.getProcessId())
            .peek((k, v) -> log.debug("End-of-flow signal received for PID: {}", k))
            .toTable(Materialized
                .<String, FlowEvent, KeyValueStore<Bytes, byte[]>>as("flowEndJoinerSignalsKTable")
                .withKeySerde(Serdes.String())
                .withValueSerde(new JsonSerde<>(FlowEvent.class))
                .withRetention(Duration.ofMinutes(1440L))
            );

        // aggregate requests in ktable
        var requestsKTable = asyncSentRequestsStream
            .selectKey((k, v) -> v.getProcessId())
            .peek((k, v) -> log.trace("Async request received for PID: {}", k))
            .groupByKey()
            .aggregate(
                FlowEndJoinerAggregateDto::new,
                this::aggregateRequests,
                Materialized
                    .<String, FlowEndJoinerAggregateDto, KeyValueStore<Bytes, byte[]>>as("flowEndJoinerRequestsKTable")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new JsonSerde<>(FlowEndJoinerAggregateDto.class))
                    .withRetention(Duration.ofMinutes(1440L))
            );

        // aggregate responses in ktable
        var responsesKTable = asyncReceivedResponsesStream
            .selectKey((k, v) -> v.getProcessId())
            .peek((k, v) -> log.trace("Async response received for PID: {}", k))
            .groupByKey()
            .aggregate(
                FlowEndJoinerAggregateDto::new,
                this::aggregateResponses,
                Materialized
                    .<String, FlowEndJoinerAggregateDto, KeyValueStore<Bytes, byte[]>>as("flowEndJoinerResponsesKTable")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new JsonSerde<>(FlowEndJoinerAggregateDto.class))
                    .withRetention(Duration.ofMinutes(1440L))
            );

        // first join received signal with previously aggregated requests
        var signalRequestsJoin = signalKTable.join(
            requestsKTable,
            (signal, requests) -> {
                var res = FlowEndJoinerAggregateDto.builder()
                    .processId(signal.getProcessId())
                    .sentAsyncRequests(requests.getSentAsyncRequests())
                    .build();

                log.debug("Joined requests and signal for PID {}, total received requests: {}",
                    res.getProcessId(),
                    res.getSentAsyncRequests());

                return res;
            },
            Materialized
                .<String, FlowEndJoinerAggregateDto, KeyValueStore<Bytes, byte[]>>as("flowEndJoinerSignalsRequestsJoinKTable")
                .withKeySerde(Serdes.String())
                .withValueSerde(new JsonSerde<>(FlowEndJoinerAggregateDto.class))
                .withRetention(Duration.ofMinutes(1440L))
            );

        // then join aggregated responses with previously aggregated signal and responses
        var signalRequestsResponsesJoin = signalRequestsJoin.join(
            responsesKTable,
            (signalRequests, responses) -> {
                var res = FlowEndJoinerAggregateDto.builder()
                    .processId(signalRequests.getProcessId())
                    .sentAsyncRequests(signalRequests.getSentAsyncRequests())
                    .receivedAsyncResponses(responses.getReceivedAsyncResponses())
                    .build();

                log.debug("Joined requests/signal and responses for PID {}, responses expected: {}, responses received: {}",
                    res.getProcessId(),
                    res.getSentAsyncRequests(),
                    res.getReceivedAsyncResponses());

                return res;
            },
            Materialized
                .<String, FlowEndJoinerAggregateDto, KeyValueStore<Bytes, byte[]>>as("flowEndJoinerSignalsRequestsResponsesJoinKTable")
                .withKeySerde(Serdes.String())
                .withValueSerde(new JsonSerde<>(FlowEndJoinerAggregateDto.class))
                .withRetention(Duration.ofMinutes(1440L))
        );

        // send final event when number of received responses reaches expected
        return signalRequestsResponsesJoin
            .toStream()
            .filter((k, v) -> v.getSentAsyncRequests().equals(v.getReceivedAsyncResponses()))
            .peek(((k, v) -> log.info("Generated final output event for PID: {}, v: {}", k, v.toString())))
            .mapValues((k, v) -> {
                var res = new ProcessCompletedEvent();
                res.setProcessId(v.getProcessId());
                return res;
            });
    }

    private FlowEndJoinerAggregateDto aggregateRequests(
        final String key,
        final FlowEvent current,
        final FlowEndJoinerAggregateDto aggregate
    ) {
        aggregate.setProcessId(current.getProcessId());
        aggregate.setSentAsyncRequests(1 + aggregate.getSentAsyncRequests());
        log.debug("Request aggregated for PID {}, total {}", current.getProcessId(), aggregate.getSentAsyncRequests());
        return aggregate;
    }

    private FlowEndJoinerAggregateDto aggregateResponses(
        final String key,
        final FlowEvent current,
        final FlowEndJoinerAggregateDto aggregate
    ) {
        aggregate.setProcessId(current.getProcessId());
        aggregate.setReceivedAsyncResponses(1 + aggregate.getReceivedAsyncResponses());
        log.debug("Response aggregated for PID {}, total {}", current.getProcessId(), aggregate.getReceivedAsyncResponses());
        return aggregate;
    }
}

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
class FlowEndJoinerAggregateDto implements Serializable {
    private String processId;
    private Integer sentAsyncRequests = 0;
    private Integer receivedAsyncResponses = 0;
}
