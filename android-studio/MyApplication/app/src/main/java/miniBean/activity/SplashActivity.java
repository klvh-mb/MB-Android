package miniBean.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.NotificationCache;
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

        String sessionId = AppController.getInstance().getSessionId();

        Intent intent = getIntent();
        if (intent != null &&
                intent.getStringExtra("flag") != null &&
                intent.getStringExtra("flag").equals("FromLoginActivity")) {
            fromLoginActivity = true;
            sessionId = intent.getStringExtra("key");
        }

        if (sessionId == null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            Log.d(this.getClass().getSimpleName(), "onStart: sessionID - " + sessionId);
            startMainActivity(sessionId, this);
        }
    }

    private void startMainActivity(final String sessionId, final Context context) {
        Log.d(this.getClass().getSimpleName(), "getUserInfo");
        AppController.api.getUserInfo(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                Log.d(this.getClass().getSimpleName(), "startMainActivity: getUserInfo.success: user="+user.getDisplayName()+" id="+user.getId()+" newUser="+user.newUser);

                // clear session id, redirect to login page
                if (user.getId() == -1) {
                    Toast.makeText(context, "Cannot find user. Please login again.", Toast.LENGTH_LONG).show();
                    AppController.getInstance().clearPreferences();
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                }

                // new user flow
                if(user.isNewUser() || StringUtils.isEmpty(user.getDisplayName())) {
                    if (!user.isEmailValidated()) {
                        Toast.makeText(context, "Please verify your email and login again.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    } else {
                        Intent intent = new Intent(context, SignupDetailActivity.class);
                        intent.putExtra("first_name", user.firstName);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // save to preferences
                    if (AppController.getInstance().getSessionId() == null) {
                        AppController.getInstance().savePreferences(sessionId);
                    }

                    AppController.setUser(user);

                    NotificationCache.refresh();

                    // display splash
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }
                    }, DefaultValues.SPLASH_DISPLAY_MILLIS);
                }
            }

            @Override
            public void failure(RetrofitError error) {
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
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog)
                .setTitle(getString(R.string.connection_timeout_title))
                .setMessage(getString(R.string.connection_timeout_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
}
