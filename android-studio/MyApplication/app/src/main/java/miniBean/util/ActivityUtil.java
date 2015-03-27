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
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import miniBean.R;
import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class ActivityUtil {

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
        Drawable empty = AppController.getInstance().getResources().getDrawable(R.drawable.empty);
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
    // Device
    //

    public int getRealDimension(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, activity.getResources().getDisplayMetrics());
    }

    public Rect getDisplayDimensions() {
        int padding = 60;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels - padding;
        int height = displaymetrics.heightPixels - padding;
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
        ConnectivityManager conMgr = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(AppController.getInstance().getApplicationContext(), AppController.getInstance().getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
