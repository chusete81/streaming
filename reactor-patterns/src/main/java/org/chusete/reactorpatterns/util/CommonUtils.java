package org.chusete.reactorpatterns.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CommonUtils {
    public static String getCompactUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getUTCTimeStamp() {
        return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static boolean isPastTimeInMinutes(final String cTime) {
        var sNow = CommonUtils.getUTCTimeStamp().substring(0, 16);
        return sNow.compareTo(cTime) > 0;
    }
}
