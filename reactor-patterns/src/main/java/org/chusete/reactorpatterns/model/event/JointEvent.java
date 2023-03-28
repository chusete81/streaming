package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.Setter;
import org.chusete.reactorpatterns.util.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class JointEvent extends FlowEvent implements Serializable {
    private List<ForkedEvent> forkedEvents;
    private Integer total;

    public JointEvent() {
        this.setTimeStamp(CommonUtils.getUTCTimeStamp());
        forkedEvents = new ArrayList<>();
        total = 0;
    }

    public JointEvent(ForkedEvent event) {
        updateFromParent(event);
        this.setTimeStamp(CommonUtils.getUTCTimeStamp());

        forkedEvents = new ArrayList<>();
        forkedEvents.add(event);
        total = event.getTotal();
    }
}
