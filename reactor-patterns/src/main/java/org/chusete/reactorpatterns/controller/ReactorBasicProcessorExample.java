package org.chusete.reactorpatterns.controller;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.model.event.ProcessCompletedEvent;
import org.chusete.reactorpatterns.model.event.ProcessStartedEvent;
import org.chusete.reactorpatterns.service.MiscService;
import org.chusete.reactorpatterns.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class ReactorBasicProcessorExample {
    @Autowired
    MiscService miscService;

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
}
