package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.Setter;
import org.chusete.reactorpatterns.util.CommonUtils;
import org.chusete.reactorpatterns.util.JsonUtils;

import java.io.Serializable;

@Getter
@Setter
public class FlowEvent implements Serializable {

    private String processId;
    private String timeStamp;

    public FlowEvent() {
        this.processId = CommonUtils.randomUUID();
        this.timeStamp = CommonUtils.getUTCTimeStamp();
    }

    protected void updateFromParent(FlowEvent parentFlow) {
        this.setProcessId(parentFlow.getProcessId());
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
