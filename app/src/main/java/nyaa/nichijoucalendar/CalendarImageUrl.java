package nyaa.nichijoucalendar;

import android.text.format.Time;

/**
 * Created by Oscar on 19/01/2015.
 */
public class CalendarImageUrl {

    private static final String URL_HOST = "nichijou-calendar.s3.amazonaws.com";

    public static String getUrl() {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        return String.format("http://%s/1080/%02d/%d.jpg", URL_HOST, today.month + 1, today.monthDay);
    }

}
