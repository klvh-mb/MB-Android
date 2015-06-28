package miniBean.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyImageGetter;
import miniBean.viewmodel.CommunityPostVM;
import retrofit.RetrofitError;

/**
 * Created by keithlei on 3/16/15.
 */
public class ViewUtil {

    public static final String HTML_LINE_BREAK = "<br>";

    private static Rect displayDimensions = null;

    private ViewUtil() {}

    //
    // View
    //

    public static void showSpinner(Activity activity) {
        ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
        if (spinner != null) {
            AnimationUtil.show(spinner);
        }
    }

    public static void stopSpinner(Activity activity) {
        ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
        if (spinner != null) {
            AnimationUtil.cancel(spinner);
        }
    }

    public static int getRealDimension(int size, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, resources.getDisplayMetrics());
    }

    public static Rect getDisplayDimensions(Activity activity) {
        if (displayDimensions == null) {
            int padding = 60;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels - padding;
            int height = displaymetrics.heightPixels - padding;
            displayDimensions = new Rect(0, 0, width, height);
        }
        return displayDimensions;
    }

    public static void setHeightBasedOnChildren(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +
                (listView.getDividerHeight() * (listAdapter.getCount()-1)) +
                ViewUtil.getRealDimension(5, listView.getResources());  // extra margin
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void fullscreenImagePopup(Activity activity, String source) {
        try {
            //frameLayout.getForeground().setAlpha(20);
            //frameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.image_popup_window,(ViewGroup) activity.findViewById(R.id.popupElement));
            ImageView fullImage= (ImageView) layout.findViewById(R.id.fullImage);

            PopupWindow imagePopup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            imagePopup.setOutsideTouchable(false);
            imagePopup.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), ""));
            imagePopup.setFocusable(true);
            imagePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ImageUtil.displayImage(source, fullImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // HTML
    //

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView) {
        setHtmlText(text, imageGetter, textView, false);
    }

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView, boolean longClickSelectAll) {
        setHtmlText(text, imageGetter, textView, longClickSelectAll, false);
    }

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView, boolean longClickSelectAll, boolean linkMovement) {
        //Log.d(this.getClass().getSimpleName(), "getDisplayTextFromHtml: text="+text);

        text = text.replace("\n", HTML_LINE_BREAK);

        imageGetter.setTextView(textView);
        Spanned spanned = Html.fromHtml(text, imageGetter, null);

        textView.setText(spanned);
        if (linkMovement) {
            setLinksClickable(textView);
        }

        if (longClickSelectAll) {
            setLongClickSelectAll(textView);
        }
    }

    //
    // Text
    //

    public static boolean copyToClipboard(TextView textView) {
        return ClipboardUtil.copyToClipboard(AppController.getInstance(), textView.getText().toString());
    }

    public static void readFromClipboardTo(TextView textView) {
        String text = ClipboardUtil.readFromClipboard(AppController.getInstance());
        if (!StringUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    public static void setLinksClickable(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setLinksClickable(true);
        textView.setLinkTextColor(textView.getResources().getColor(R.color.link));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setLongClickSelectAll(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    //textView.setSelectAllOnFocus(true);
                    //textView.setSelected(true);
                    Selection.selectAll((Spannable) textView.getText());
                    textView.setCursorVisible(true);
                    textView.requestFocus();
                }
                return false;
            }
        });
    }

    //
    // Popup soft keyboard
    //

    public static void popupInputMethodWindow(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }

    public static void hideInputMethodWindow(final Activity activity, final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 100);
    }

    //
    // Alert dialog
    //

    public static void alert(Context context, String message) {
        alert(context, null, message);
    }

    public static void alert(Context context, String title, String message) {
        alert(context, title, message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public static void alert(Context context, String title, String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, onClick);
        if (!StringUtils.isEmpty(title)) {
            alertBuilder.setTitle(title);
        }
        alertBuilder.show();
    }

    public static Dialog alert(Context context, int dialogResourceId) {
        return alert(context, dialogResourceId, -1, null);
    }

    public static Dialog alert(Context context, int dialogResourceId, int buttonResourceId, final View.OnClickListener onClick) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(dialogResourceId, null);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);
        //dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.yes), onClick);
        if (buttonResourceId != -1) {
            Button button = (Button) dialogView.findViewById(buttonResourceId);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (onClick != null) {
                        onClick.onClick(view);
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        dialog.show();
        return dialog;
    }

    public static Dialog alertGamePoints(Context context, String desc, int points) {
        return alertGamePoints(context, desc, points, 3000);
    }

    public static Dialog alertGamePoints(Context context, String desc, int points, long delayMillis) {
        final Dialog dialog = alert(context, R.layout.game_points_popup_window);
        TextView descText = (TextView) dialog.findViewById(R.id.descText);
        TextView pointsText = (TextView) dialog.findViewById(R.id.pointsText);
        ImageView dismissImage = (ImageView) dialog.findViewById(R.id.dismissImage);
        descText.setText(desc);
        pointsText.setText("+"+points);
        dismissImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        // auto dismiss
        if (delayMillis != -1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, delayMillis);
        }
        return dialog;
    }

    //
    // Retrofit
    //

    public static String getResponseBody(retrofit.client.Response response) {
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

    public static int getErrorStatusCode(RetrofitError error) {
        if (error.isNetworkError()) {
            return 550; // Use another code if you'd prefer
        }

        return error.getResponse().getStatus();
    }

    //
    // Post
    //

    public static boolean isNewPost(CommunityPostVM post) {
        return post.getN_c() <= DefaultValues.NEW_POST_NOC &&
                DateTimeUtil.getDaysAgo(post.t) <= DefaultValues.NEW_POST_DAYS_AGO;
    }

    public static boolean isHotPost(CommunityPostVM post) {
        return post.getN_c() > DefaultValues.HOT_POST_NOC ||
                post.getNol() > DefaultValues.HOT_POST_NOL ||
                post.getNov() > DefaultValues.HOT_POST_NOV;
    }

    public static boolean isImagePost(CommunityPostVM post) {
        return post.isHasImage();
    }

    //
    // Schools
    //

    public static String translateClassTime(String classTime, Resources resources) {
        return classTime.replace("AM", resources.getString(R.string.filter_schools_time_am)).
                replace("PM", resources.getString(R.string.filter_schools_time_pm)).
                replace("WD", resources.getString(R.string.filter_schools_time_wd));
    }
}
