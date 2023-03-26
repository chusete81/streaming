package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.chusete.reactorpatterns.util.CommonUtils;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ProcessCompletedEvent extends FlowEvent implements Serializable {
    private String originalString;
    private String upperCase;
    private Integer wordCount;
    private Integer letterCount;

    public ProcessCompletedEvent(
            String originalString,
            String upperCase,
            Integer wordCount,
            Integer letterCount,
            FlowEvent parentFlow
    ) {
        updateFromParent(parentFlow);
        this.setTimeStamp(CommonUtils.getUTCTimeStamp());

        this.originalString = originalString;
        this.upperCase = upperCase;
        this.wordCount = wordCount;
        this.letterCount = letterCount;
    }
}
