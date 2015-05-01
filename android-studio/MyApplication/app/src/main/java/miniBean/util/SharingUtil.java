package miniBean.util;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.viewmodel.CommunityPostVM;
import miniBean.viewmodel.KindergartenVM;
import miniBean.viewmodel.PreNurseryVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharingUtil {

    public static enum SharingType {
        WHATSAPP
    }

    public static final String SHARING_MESSAGE_NOTE = AppController.getInstance().getString(R.string.sharing_message_note);
    //public static final String SHARING_MESSAGE_NOTE = AppController.getInstance().getString(R.string.sharing_message_note) + HtmlUtil.LINE_BREAK + createAndroidAppDownloadUrl();

    private SharingUtil() {}

    public static void shareToWhatapp(CommunityPostVM post, Context context) {
        shareTo(createMessage(post), SharingType.WHATSAPP, context);
    }

    public static void shareToWhatapp(PreNurseryVM school, Context context) {
        shareTo(createMessage(school), SharingType.WHATSAPP, context);
    }

    public static void shareToWhatapp(KindergartenVM school, Context context) {
        shareTo(createMessage(school), SharingType.WHATSAPP, context);
    }

    /**
     * http://www.whatsapp.com/faq/en/android/28000012
     *
     * @param message
     * @param type
     * @param context
     */
    public static void shareTo(String message, SharingType type, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message).toString());
        sendIntent.setType("text/plain");

        switch(type) {
            case WHATSAPP:
                sendIntent.setPackage("com.whatsapp");
                break;
        }

        try {
            context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AppController.getInstance(),
                    getSharingTypeName(type) + AppController.getInstance().getString(R.string.sharing_app_not_installed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static String createMessage(CommunityPostVM post) {
        String message = post.getPtl();
        String url = UrlUtil.createPostLandingUrl(post);
        message = message +
                HtmlUtil.LINE_BREAK +
                url +
                HtmlUtil.LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    private static String createMessage(PreNurseryVM school) {
        String message = school.getN();
        if (!StringUtils.isEmpty(school.getNe())) {
            message += " "+school.getNe();
        }
        String url = UrlUtil.createSchoolUrl(school);
        message = message +
                HtmlUtil.LINE_BREAK +
                url +
                HtmlUtil.LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    private static String createMessage(KindergartenVM school) {
        String message = school.getN();
        String url = UrlUtil.createSchoolUrl(school);
        message = message +
                HtmlUtil.LINE_BREAK +
                url +
                HtmlUtil.LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    private static String getSharingTypeName(SharingType type) {
        switch (type) {
            case WHATSAPP:
                return "Whatsapp";
        }
        return "";
    }
}
