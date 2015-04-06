package miniBean.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        /*
        if( getIntent().getBooleanExtra("EXIT", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        */

        init();

        if (AppController.getInstance().getSessionId() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreate: sessionID - " + AppController.getInstance().getSessionId());
            startMainActivity();
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

    public static void init() {
        // set user info to check for role specific actions, and others
        AppController.getInstance().setUserInfo();
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, DefaultValues.SPLASH_DISPLAY_MILLIS);
    }
}
