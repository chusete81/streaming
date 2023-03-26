package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ProcessStartedEvent extends FlowEvent implements Serializable {
    private String originalString;

    public ProcessStartedEvent(String originalString) {
        this.originalString = originalString;
    }
}
