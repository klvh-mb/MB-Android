package miniBean.util;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import miniBean.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class MyWebViewClient extends WebViewClient {

    public static final String TERMS_URL = AppController.BASE_URL + "/terms";
    public static final String PRIVACY_URL = AppController.BASE_URL + "/privacy";
    public static final String FORGET_PASSWORD_URL = AppController.BASE_URL + "/login/password/forgot";
    //public static final String FORGET_PASSWORD_URL = AppController.BASE_URL + "/m-app#!/game/rules";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

}
