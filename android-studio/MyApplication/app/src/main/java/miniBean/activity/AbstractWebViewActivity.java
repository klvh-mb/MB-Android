package miniBean.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;

public abstract class AbstractWebViewActivity extends TrackedFragmentActivity {

    public static final String TERMS_URL = AppController.BASE_URL + "/terms";
    public static final String PRIVACY_URL = AppController.BASE_URL + "/privacy";
    public static final String FORGET_PASSWORD_URL = AppController.BASE_URL + "/login/password/forgot";
    public static final String GAME_RULES_URL = AppController.BASE_URL + "/m-app#!/game-rules";

    protected WebView webView;

    protected abstract String getActionBarTitle();

    protected abstract String getLoadUrl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_activity);
        
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.my_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        TextView titleText = (TextView) findViewById(R.id.actionbarTitle);
        titleText.setText(getActionBarTitle());

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress >= 100)
                    progressBar.setVisibility(View.GONE);
            }
        });
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getLoadUrl());

        ImageView backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     *
     */
    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}