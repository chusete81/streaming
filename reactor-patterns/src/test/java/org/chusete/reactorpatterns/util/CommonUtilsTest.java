package org.chusete.reactorpatterns.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

class CommonUtilsTest {

    @Test
    void randomUUID() {
        var s = CommonUtils.randomUUID();

        Assertions.assertTrue(Pattern.compile(CommonUtils.REGEX_UUID).matcher(s).find());
    }

    @Test
    void getUTCTimeStamp() {
        var s = CommonUtils.getUTCTimeStamp();

        Assertions.assertDoesNotThrow(() -> CommonUtils.TIMESTAMP_FORMAT.parse(s));
    }

    @Test
    void isPastTimeInMinutes() {
        var sNow = CommonUtils.getUTCTimeStamp().substring(0, 16);
        Assertions.assertFalse(CommonUtils.isPastTimeInMinutes(sNow));

        var past = "2023-01-01T00:00";
        Assertions.assertTrue(CommonUtils.isPastTimeInMinutes(past));
    }

    @Test
    void randomInt() {
        int limit = (int) (Math.random() * 10000);

        var r = CommonUtils.randomInt(limit);

        Assertions.assertFalse(r > limit);
    }
}