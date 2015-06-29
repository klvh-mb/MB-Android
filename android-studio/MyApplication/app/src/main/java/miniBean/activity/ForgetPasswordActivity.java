package miniBean.activity;

import miniBean.R;

public class ForgetPasswordActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.login_forgot_password);
    }

    protected String getLoadUrl() {
        return FORGET_PASSWORD_URL;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginActivity.startLoginActivity(this);
    }
}