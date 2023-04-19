package org.chusete.reactorpatterns.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CommonUtils {
    public static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    public static final String REGEX_HEX_DIGIT = "[0-9a-f]";

    public static final String REGEX_UUID =
            String.format("%s{8}-(%s{4}-){3}%s{12}", REGEX_HEX_DIGIT, REGEX_HEX_DIGIT, REGEX_HEX_DIGIT);

    public static final String REGEX_ISO_8601 =
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})?";

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getUTCTimeStamp() {
        return LocalDateTime.now(ZoneOffset.UTC).format(TIMESTAMP_FORMAT);
    }

    public static boolean isPastTimeInMinutes(final String cTime) {
        var sNow = CommonUtils.getUTCTimeStamp().substring(0, 16);
        return sNow.compareTo(cTime) > 0;
    }

    public static int randomInt(int limit) {
        return (int) (Math.random() * limit);
    }
}
