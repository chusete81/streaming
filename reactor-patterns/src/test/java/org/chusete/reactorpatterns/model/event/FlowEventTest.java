package org.chusete.reactorpatterns.model.event;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

@Slf4j
class FlowEventTest {
    public static final String REGEX_UUID_COMPACT = "[a-z0-9]{32}";

    public static final String REGEX_ISO_8601 =
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?";

    @Test
    void toStringTest() {
        var s = new FlowEvent().toString();

        Assertions.assertInstanceOf(String.class, s);
        Assertions.assertNotNull(s);
        Assertions.assertTrue(s.contains("timeStamp"));

        var flowEventRegex =
                "\\{\"processId\":\"" + REGEX_UUID_COMPACT + "\",\"timeStamp\":\"" + REGEX_ISO_8601 + "\"}";

        Assertions.assertTrue(Pattern.compile(flowEventRegex).matcher(s).find());
    }
}