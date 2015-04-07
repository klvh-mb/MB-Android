package miniBean.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class SplashActivity extends Activity {

    public SharedPreferences session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        init();

        session = getSharedPreferences("prefs", 0);

        if (AppController.getInstance().getSessionId() != null) {
            Log.d(this.getClass().getSimpleName(), "onCreate: sessionID - " + AppController.getInstance().getSessionId());
            startMainActivity();
        }
    }

    public static void init() {
        // set user info to check for role specific actions, and others
        AppController.getInstance().setUserInfo();
    }

    private void startMainActivity() {
        AppController.api.getUserInfo(AppController.getInstance().getSessionId(), new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                System.out.println("splash isNewUser::::"+user.isNewUser());

                if(user.isNewUser()) {
                    if (user.isFbLogin() == false) {
                        System.out.println("splash fblogin false::::::::::");
                        Intent intent = new Intent(SplashActivity.this, SignupDetailActivity.class);
                        intent.putExtra("first_name", user.firstName);
                        startActivity(intent);
                    } else if (user.isFbLogin() == true) {
                        Intent intent = new Intent(SplashActivity.this, SignupDetailActivity.class);
                        intent.putExtra("first_name", user.firstName);
                        startActivity(intent);
                    }
                } else{
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
                error.printStackTrace();
            }
        });
    }
}
