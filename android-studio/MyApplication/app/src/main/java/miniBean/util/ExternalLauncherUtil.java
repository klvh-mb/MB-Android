package miniBean.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by keithlei on 3/16/15.
 */
public class ExternalLauncherUtil {

    public static final String LAUNCH_MAP_PREFIX = "http://maps.google.com.hk/maps?q=";

    public static void launchCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public static void launchBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        context.startActivity(intent);
    }

    public static void launchMap(Context context, String address) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LAUNCH_MAP_PREFIX+address));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch(ActivityNotFoundException ex) {
            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LAUNCH_MAP_PREFIX+address));
            context.startActivity(unrestrictedIntent);
        }
    }

    public static void launchMap(Context context, double latitude, double longitude) {
        String uri = String.format("geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try {
            context.startActivity(intent);
        } catch(ActivityNotFoundException ex) {
            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(unrestrictedIntent);
        }
    }
}
