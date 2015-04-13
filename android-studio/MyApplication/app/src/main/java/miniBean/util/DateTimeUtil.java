package miniBean.util;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class DateTimeUtil {

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    private static final String DATETIME_FORMAT = "yyyy-MM-dd h:mma";
    private static SimpleDateFormat sdtf = new SimpleDateFormat(DATETIME_FORMAT);

    private DateTimeUtil() {}

    private static long getTimeDiffFromNow(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new DateTime().getMillis();
        if (time > now || time <= 0) {
            return -1;
        }

        final long diff = now - time;
        return diff;
    }

    public static long getMinsAgo(long time) {
        long diff = getTimeDiffFromNow(time);
        if (diff == -1)
            return -1;
        return diff / MINUTE_MILLIS;
    }

    public static long getDaysAgo(long time) {
        long diff = getTimeDiffFromNow(time);
        if (diff == -1)
            return -1;
        return diff / DAY_MILLIS;
    }

    public static String getTimeAgo(long time) {
        return getTimeAgo(time, false);
    }

    public static String getTimeAgo(long time, boolean withHrMin) {
        long diff = getTimeDiffFromNow(time);
        if (diff == -1)
            return null;

        if (diff < MINUTE_MILLIS) {
            return AppController.getInstance().getString(R.string.timeago_just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return 1 + AppController.getInstance().getString(R.string.timeago_min);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + AppController.getInstance().getString(R.string.timeago_min);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return 1 +  AppController.getInstance().getString(R.string.timeago_hrs);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + AppController.getInstance().getString(R.string.timeago_hrs);
        } else if (diff < 48 * HOUR_MILLIS) {
            return AppController.getInstance().getString(R.string.timeago_yesterday);
        } else if (diff < 14 * DAY_MILLIS) {
            return diff / DAY_MILLIS + AppController.getInstance().getString(R.string.timeago_days);
        } else {
            Date date = new Date(time);
            if (withHrMin)
                return sdtf.format(date);
            return sdf.format(date);
        }
    }
}
