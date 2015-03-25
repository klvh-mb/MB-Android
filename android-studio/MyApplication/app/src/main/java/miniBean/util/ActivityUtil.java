package miniBean.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import miniBean.R;

/**
 * Created by keithlei on 3/16/15.
 */
public class ActivityUtil {

    private String DATE_FORMAT_NOW = "yyyy-MM-dd";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

    private Activity activity;

    public ActivityUtil(Activity activity) {
        this.activity = activity;
    }

    //
    // Html / Image
    //

    public Spanned getDisplayTextFromHtml(String text, Html.ImageGetter imageGetter) {
        //Log.d(this.getClass().getSimpleName(), "getDisplayTextFromHtml: text="+text);
        text = text.replace("\n", "<br/>");
        Spanned spanned = Html.fromHtml(text, imageGetter, null);
        return spanned;
    }

    public Drawable getEmptyDrawable() {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = activity.getResources().getDrawable(R.drawable.empty);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        return d;
    }

    //
    // Popup soft keyboard
    //

    public void popupInputMethodWindow() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    //
    // Date
    //

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new DateTime().getMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return this.activity.getString(R.string.timeago_just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return 1 + this.activity.getString(R.string.timeago_min);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + this.activity.getString(R.string.timeago_min);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return 1 +  this.activity.getString(R.string.timeago_hrs);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + this.activity.getString(R.string.timeago_hrs);
        } else if (diff < 48 * HOUR_MILLIS) {
            return this.activity.getString(R.string.timeago_yesterday);
        } else if (diff < 14 * DAY_MILLIS) {
            return diff / DAY_MILLIS + this.activity.getString(R.string.timeago_days);
        } else {
            Date date = new Date(time);
            return sdf.format(date);
        }
    }

    //
    // Device
    //

    public int getRealDimension(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, activity.getResources().getDisplayMetrics());
    }

    public Rect getDisplayDimensions() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;
        return new Rect(0, 0, width, height);
    }

    //
    // Retrofit
    //

    public String getResponseBody(retrofit.client.Response response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //
    // Network
    //

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
