package miniBean.util;

import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import miniBean.app.MyImageGetter;

/**
 * Created by keithlei on 3/16/15.
 */
public class HtmlUtil {

    public static final String LINE_BREAK = "<br>";

    private HtmlUtil() {}

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView) {
        setHtmlText(text, imageGetter, textView, false);
    }

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView, boolean longClickSelectAll) {
        setHtmlText(text, imageGetter, textView, true, longClickSelectAll);
    }

    public static void setHtmlText(String text, MyImageGetter imageGetter, TextView textView, boolean longClickSelectAll, boolean linkMovement) {
        //Log.d(this.getClass().getSimpleName(), "getDisplayTextFromHtml: text="+text);

        text = text.replace("\n", LINE_BREAK);

        imageGetter.setTextView(textView);
        Spanned spanned = Html.fromHtml(text, imageGetter, null);

        textView.setText(spanned);
        if (linkMovement) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (longClickSelectAll) {
            ActivityUtil.setLongClickSelectAll(textView);
        }
    }
}
