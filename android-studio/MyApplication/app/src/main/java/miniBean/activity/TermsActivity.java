package miniBean.activity;

import miniBean.R;

public class TermsActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.signup_terms);
    }

    protected String getLoadUrl() {
        return TERMS_URL;
    }
}