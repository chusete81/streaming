package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.chusete.reactorpatterns.model.event.ForkedEvent;
import org.chusete.reactorpatterns.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckDuplicatesProcessorExample {


    @Autowired
    private MessageUtils messageUtils;

    public KStream<String, ForkedEvent> checkDuplicatesPattern(KStream<String, ForkedEvent> inbound) {
        return inbound
                .transform(CheckDuplicatesTransformer::new, CheckDuplicatesTransformer.STORE_NAME)
                .peek((found, event) -> {
                    if (found) {
                        notifyDuplicatedWord(event);
                    }
                })
                .filterNot((found, event) -> found)
                .selectKey((k, event) -> event.getProcessId());
    }

    private void notifyDuplicatedWord(ForkedEvent event) {
        messageUtils.sendMessage(event.getProcessId(), event.toString(), "checkDuplicatesPattern-out-1");
    }

    public static class CheckDuplicatesTransformer implements Transformer<String, ForkedEvent, KeyValue<Boolean, ForkedEvent>> {
        public static final String STORE_NAME = "wordDuplicatesKVStore";

        private KeyValueStore<String, String> store;

        @Override
        public KeyValue<Boolean, ForkedEvent> transform(String key, ForkedEvent event) {
            final var word = event.getWord();
            final var pid = event.getProcessId();

            // check for word in kv-store, put it with its PID if absent
            final var found = store.putIfAbsent(word, pid);

            log.debug(
                    found == null ? "New word '{}' with PID '{}'" : "Duplicated word '{}' found with original PID '{}'",
                    word,
                    found == null ? pid : found
            );

            // return KeyValue pair with 'found' boolean flag and given input data
            return KeyValue.pair(found != null, event);
        }

        @Override
        public void init(ProcessorContext context) {
            this.store = context.getStateStore(STORE_NAME);
        }

        @Override
        public void close() {
            // No resources to clean on close
        }
    }
}
