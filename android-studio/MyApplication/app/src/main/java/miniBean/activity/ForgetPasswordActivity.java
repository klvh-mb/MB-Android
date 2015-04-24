package miniBean.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

import miniBean.R;
import miniBean.util.MyWebViewClient;

public class ForgetPasswordActivity extends FragmentActivity {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);

        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new MyWebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(MyWebViewClient.FORGET_PASSWORD_URL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginActivity.startLoginActivity(this);
    }
}