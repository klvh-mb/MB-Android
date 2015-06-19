package miniBean.util;

import android.content.res.Resources;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import miniBean.R;
import miniBean.app.MyImageGetter;
import miniBean.viewmodel.CommunityPostVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class ViewUtil {

    public static final String HTML_LINE_BREAK = "<br>";

    private ViewUtil() {}

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