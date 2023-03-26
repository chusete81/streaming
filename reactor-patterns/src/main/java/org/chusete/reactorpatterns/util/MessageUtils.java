package org.chusete.reactorpatterns.util;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.model.event.FlowEvent;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class MessageUtils {
    public static <T extends FlowEvent> Message<T> setKey(T event) {
        log.debug("Setting key '{}' for {}", event.getProcessId(), event.getClass().getSimpleName());

        return MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.MESSAGE_KEY, event.getProcessId())
                .build();
    }
}
