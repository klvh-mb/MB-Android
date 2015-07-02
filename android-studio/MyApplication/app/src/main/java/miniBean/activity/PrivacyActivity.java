package miniBean.activity;

import miniBean.R;

public class PrivacyActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.signup_privacy);
    }

    protected String getLoadUrl() {
        return PRIVACY_URL;
    }
}