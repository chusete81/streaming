package org.chusete.reactorpatterns.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void pastTimeInMinutes() {
        var sNow = CommonUtils.getUTCTimeStamp().substring(0, 16);
        Assertions.assertFalse(CommonUtils.isPastTimeInMinutes(sNow));

        var past = "2023-01-01T00:00";
        Assertions.assertTrue(CommonUtils.isPastTimeInMinutes(past));
    }
}