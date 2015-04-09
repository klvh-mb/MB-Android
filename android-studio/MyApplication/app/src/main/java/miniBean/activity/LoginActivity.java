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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.AnimationUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AbstractLoginActivity {

    private ProgressBar spinner;
    private EditText username = null;
    private EditText password = null;
    private TextView login;
    private ImageView fbLoginButton;
    private TextView signup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        fbLoginButton = (ImageView) findViewById(R.id.buttonFbLogin);
        login = (TextView) findViewById(R.id.buttonLogin);
        signup = (TextView) findViewById(R.id.signupText);

        spinner = (ProgressBar)findViewById(R.id.spinner);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnimationUtil.show(spinner);

                AppController.api.login(username.getText().toString(), password.getText().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        if (!saveToSession(response)) {
                            alert(R.string.login_error_title, R.string.login_error_message);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        AnimationUtil.cancel(spinner);
                        if (error.getResponse() != null &&
                                error.getResponse().getStatus() == 400) {
                            String errorMsg = LoginActivity.this.activityUtil.getResponseBody(error.getResponse());
                            if (!StringUtils.isEmpty(errorMsg)) {
                                alert(getString(R.string.login_error_title), errorMsg);
                            } else {
                                alert(R.string.login_error_title, R.string.login_id_error_message);
                            }
                        } else {
                            alert(R.string.login_error_title, R.string.login_error_message);
                        }

                        error.printStackTrace();
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        /*
         * Login my_community_fragement Click event
		 * */
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToFacebook(spinner);
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
        AppController.getInstance().savePreferences(key);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("flag","FromLoginActivity");
        intent.putExtra("key",key);
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

