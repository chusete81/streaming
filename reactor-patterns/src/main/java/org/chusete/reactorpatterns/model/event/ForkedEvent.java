package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.chusete.reactorpatterns.util.CommonUtils;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ForkedEvent extends FlowEvent implements Serializable {
    private String word;
    private Integer counter;
    private Integer total;

    public ForkedEvent(
            String word,
            Integer counter,
            Integer total,
            FlowEvent parentFlow
    ) {
        updateFromParent(parentFlow);
        this.setTimeStamp(CommonUtils.getUTCTimeStamp());

        this.word = word;
        this.counter = counter;
        this.total = total;
    }
}
