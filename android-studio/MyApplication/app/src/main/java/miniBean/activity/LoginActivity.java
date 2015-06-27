package miniBean.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AbstractLoginActivity {

    private EditText username = null;
    private EditText password = null;
    private TextView loginButton;
    private ImageView facebookButton;
    private TextView signup;
    private TextView forgetPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        facebookButton = (ImageView) findViewById(R.id.buttonFbLogin);
        loginButton = (TextView) findViewById(R.id.buttonLogin);
        signup = (TextView) findViewById(R.id.signupText);
        forgetPassword = (TextView) findViewById(R.id.forgetPasswordText);

        setLoginButton(loginButton);
        setFacebookButton(facebookButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showSpinner();

                AppController.getApi().login(username.getText().toString(), password.getText().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        if (!saveToSession(response)) {
                            ActivityUtil.alert(LoginActivity.this,
                                    getString(R.string.login_error_title),
                                    getString(R.string.login_error_message));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        stopSpinner();
                        loginButton.setEnabled(true);
                        if (error.getResponse() != null &&
                                error.getResponse().getStatus() == 400) {
                            String errorMsg = LoginActivity.this.activityUtil.getResponseBody(error.getResponse());
                            if (!StringUtils.isEmpty(errorMsg)) {
                                ActivityUtil.alert(LoginActivity.this,
                                        getString(R.string.login_error_title),
                                        errorMsg);
                            } else {
                                ActivityUtil.alert(LoginActivity.this,
                                        getString(R.string.login_error_title),
                                        getString(R.string.login_id_error_message));
                            }
                        } else {
                            ActivityUtil.alert(LoginActivity.this,
                                    getString(R.string.login_error_title),
                                    getString(R.string.login_error_message));
                        }

                        Log.e(LoginActivity.class.getSimpleName(), "api.login: failure", error);
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        /*
         * Login my_community_fragement Click event
		 * */
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToFacebook();
            }
        });
    }

    @Override
    protected boolean saveToSession(Response response) {
        if (response == null) {
            return false;
        }

        String key = activityUtil.getResponseBody(response);
        Log.d(this.getClass().getSimpleName(), "saveToSession: sessionID - " + key);
        AppController.getInstance().saveSessionId(key);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("flag", "FromLoginActivity");
        intent.putExtra("key", key);
        startActivity(intent);
        finish();

        return true;
    }

    public static void startLoginActivity(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearAll();
                            LoginActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }
}