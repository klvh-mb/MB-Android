package miniBean.util;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by keithlei on 3/16/15.
 */
public class HtmlUtil {

    public static final String LINE_BREAK = "<br>";

    private HtmlUtil() {}

    public static Spanned fromHtml(String text, Html.ImageGetter imageGetter) {
        //Log.d(this.getClass().getSimpleName(), "getDisplayTextFromHtml: text="+text);
        text = text.replace("\n", "<br/>");
        Spanned spanned = Html.fromHtml(text, imageGetter, null);
        return spanned;
    }
}
