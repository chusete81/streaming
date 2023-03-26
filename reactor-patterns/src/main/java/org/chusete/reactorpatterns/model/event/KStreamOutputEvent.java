package org.chusete.reactorpatterns.model.event;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class KStreamOutputEvent implements Serializable {
    private Integer wordCount;
    private String timeStamp;
    private List<String> words;
}
