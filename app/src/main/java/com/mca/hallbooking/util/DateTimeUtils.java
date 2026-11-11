package com.mca.hallbooking.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeUtils {

    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.US);

    /** Formats a Calendar into a storage/display friendly "dd MMM yyyy" string. */
    public static String format(Calendar calendar) {
        return DISPLAY_FORMAT.format(calendar.getTime());
    }

    public static Calendar today() {
        return Calendar.getInstance();
    }
}
