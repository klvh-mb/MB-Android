package miniBean.activity;

import android.os.Bundle;
import android.webkit.WebView;

import miniBean.R;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.MyWebViewClient;

public class PrivacyActivity extends TrackedFragmentActivity {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(MyWebViewClient.PRIVACY_URL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}