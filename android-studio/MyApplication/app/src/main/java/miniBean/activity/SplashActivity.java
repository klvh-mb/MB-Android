package miniBean.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.UserInfoCache;
import miniBean.util.ActivityUtil;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class SplashActivity extends Activity {

    private boolean fromLoginActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EasyTracker.getInstance(this).activityStart(this);
        String sessionId = AppController.getInstance().getSessionId();

        Intent intent = getIntent();
        if (intent != null &&
                intent.getStringExtra("flag") != null &&
                intent.getStringExtra("flag").equals("FromLoginActivity")) {
            fromLoginActivity = true;
            sessionId = intent.getStringExtra("key");
        }

        if (sessionId == null) {
            LoginActivity.startLoginActivity(SplashActivity.this);
        } else {
            Log.d(this.getClass().getSimpleName(), "onStart: sessionID - " + sessionId);
            startMainActivity(sessionId);
        }
    }

    private void startMainActivity(final String sessionId) {
        Log.d(this.getClass().getSimpleName(), "getUserInfo");

        UserInfoCache.refresh(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "startMainActivity: getUserInfo.success: user="+user.getDisplayName()+" id="+user.getId()+" newUser="+user.newUser);

                // clear session id, redirect to login page
                if (user.getId() == -1) {
                    Toast.makeText(SplashActivity.this, "Cannot find user. Please login again.", Toast.LENGTH_LONG).show();
                    AppController.getInstance().clearPreferences();
                    LoginActivity.startLoginActivity(SplashActivity.this);
                }

                // new user flow
                if(user.isNewUser() || StringUtils.isEmpty(user.getDisplayName())) {
                    if (!user.isEmailValidated()) {
                        Toast.makeText(SplashActivity.this, SplashActivity.this.getString(R.string.signup_error_email_unverified)+user.email, Toast.LENGTH_LONG).show();
                        AppController.getInstance().clearPreferences();
                        LoginActivity.startLoginActivity(SplashActivity.this);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, SignupDetailActivity.class);
                        intent.putExtra("first_name", user.firstName);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // save to preferences
                    if (AppController.getInstance().getSessionId() == null) {
                        AppController.getInstance().saveSessionId(sessionId);
                    }

                    AppController.initCaches();

                    // display splash
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }, DefaultValues.SPLASH_DISPLAY_MILLIS);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AppController.getInstance().clearPreferences();

                showNetworkProblemAlert();

                /*
                if (RetrofitError.Kind.NETWORK.equals(retrofitError.getKind().name()) ||
                        RetrofitError.Kind.HTTP.equals(retrofitError.getKind().name())) {

                } else {

                }

                if (!isOnline()) {
                    SplashActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                */

                error.printStackTrace();
            }
        });
    }

    private void showNetworkProblemAlert() {
        ActivityUtil.alert(SplashActivity.this,
                getString(R.string.connection_timeout_title),
                getString(R.string.connection_timeout_message));
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }


}
