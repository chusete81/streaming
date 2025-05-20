package org.chusete.reactorpatterns.controller;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.chusete.reactorpatterns.model.event.FlowEvent;
import org.chusete.reactorpatterns.model.event.ForkedEvent;
import org.chusete.reactorpatterns.util.MessageUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CheckDuplicatesProcessorExampleTest {

    private TopologyTestDriver testDriver = null;
    private TestInputTopic<String, ForkedEvent> inputTopic;
    private TestOutputTopic<String, ForkedEvent> outputTopic;

    @Mock
    private MessageUtils messageUtils;

    @BeforeEach
    void setup() {
        CheckDuplicatesProcessorExample checkDuplicatesProcessorExample = new CheckDuplicatesProcessorExample(messageUtils);

        Serde<String> stringSerde = Serdes.String();
        JsonSerde<ForkedEvent> forkedEventSerde = new JsonSerde<>(ForkedEvent.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, ForkedEvent> kStream = builder.stream("input-topic", Consumed.with(stringSerde, forkedEventSerde));
        builder.addStateStore(Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("wordDuplicatesKVStore"),
                Serdes.String(),
                Serdes.String()
        ));
        KStream<String, ForkedEvent> joinedKStream = checkDuplicatesProcessorExample.checkDuplicatesPattern(kStream);
        joinedKStream.to("output-topic", Produced.with(stringSerde, forkedEventSerde));

        testDriver = new TopologyTestDriver(builder.build(), new Properties());

        inputTopic = testDriver.createInputTopic("input-topic", stringSerde.serializer(), forkedEventSerde.serializer());
        outputTopic = testDriver.createOutputTopic("output-topic", stringSerde.deserializer(), forkedEventSerde.deserializer());
    }

    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    void checkDuplicatesPatternFiltersOutDuplicates() {
        // Mock input stream with duplicated values
        var inputList = List.of(
                new KeyValue<>("key1", new ForkedEvent("word1", 1, 8, new FlowEvent())),
                new KeyValue<>("key2", new ForkedEvent("word1", 2, 8, new FlowEvent())),
                new KeyValue<>("key3", new ForkedEvent("word2", 3, 8, new FlowEvent())),
                new KeyValue<>("key4", new ForkedEvent("word2", 4, 8, new FlowEvent())),
                new KeyValue<>("key5", new ForkedEvent("word1", 5, 8, new FlowEvent())),
                new KeyValue<>("key6", new ForkedEvent("word3", 6, 8, new FlowEvent())),
                new KeyValue<>("key7", new ForkedEvent("word3", 7, 8, new FlowEvent())),
                new KeyValue<>("key8", new ForkedEvent("word2", 8, 8, new FlowEvent()))
        );

        AtomicInteger timestamp = new AtomicInteger(1);

        // Process the stream
        inputList.forEach(e -> inputTopic.pipeInput(e.key, e.value, 500L * timestamp.getAndIncrement()));

        var results = outputTopic.readKeyValuesToList();

        // Assert only unique values are in the output
        assertEquals(3, results.size());
        assertEquals("word1", results.get(0).value.getWord());
        assertEquals("word2", results.get(1).value.getWord());
        assertEquals("word3", results.get(2).value.getWord());
    }

    @Test
    void checkDuplicatesPatternHandlesEmptyStream() {
        // Process an empty stream
        var results = outputTopic.readKeyValuesToList();

        // Assert no output
        assertTrue(results.isEmpty());
    }
}