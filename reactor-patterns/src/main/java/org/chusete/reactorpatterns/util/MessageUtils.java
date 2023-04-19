package org.chusete.reactorpatterns.util;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.model.event.FlowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageUtils {

    @Autowired
    private StreamBridge streamBridge;

    public static <T extends FlowEvent> Message<T> setKey(T event) {
        log.trace("Setting key '{}' for {}", event.getProcessId(), event.getClass().getSimpleName());

        return MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.MESSAGE_KEY, event.getProcessId())
                .build();
    }

    public void sendMessage(String key, String message, String outputBindingName) {
        log.debug("Sending message to binding '{}'", outputBindingName);

        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(message);

        if (key != null) {
            messageBuilder.setHeader(KafkaHeaders.MESSAGE_KEY, key);
        }

        streamBridge.send(outputBindingName, messageBuilder.build());
    }
}
