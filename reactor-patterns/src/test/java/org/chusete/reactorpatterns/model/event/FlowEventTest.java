package org.chusete.reactorpatterns.model.event;

import lombok.extern.slf4j.Slf4j;
import org.chusete.reactorpatterns.util.CommonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

@Slf4j
class FlowEventTest {

    @Test
    void toStringTest() {
        var s = new FlowEvent().toString();

        Assertions.assertInstanceOf(String.class, s);
        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("timeStamp"));

        var flowEventRegex =
                "\\{\"processId\":\"" + CommonUtils.REGEX_UUID + "\",\"timeStamp\":\"" + CommonUtils.REGEX_ISO_8601 + "\"}";

        Assertions.assertTrue(Pattern.compile(flowEventRegex).matcher(s).find());
    }
}