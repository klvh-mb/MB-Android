package miniBean.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.IOException;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.Config;
import miniBean.app.GcmBroadcastReceiver;
import miniBean.util.ViewUtil;
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

    // For GCM uses
    private GoogleCloudMessaging gcm;
    private Context context;
    private String regId;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Login Activity";


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

        ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);

       // setSpinner(spinner);
        setLoginButton(loginButton);
        setFacebookButton(facebookButton);

        context=getApplicationContext();

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               // showSpinner(true);

                // GCM call to GCM server For RegID
                if (TextUtils.isEmpty(regId)) {
                    regId = registerGCM();
                    Log.d("RegisterActivity", "GCM RegId: " + regId);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Already Registered with GCM Server!",
                            Toast.LENGTH_LONG).show();
                }


                AppController.getApi().login(username.getText().toString(), password.getText().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        if (!saveToSession(response)) {
                            ViewUtil.alert(LoginActivity.this,
                                    getString(R.string.login_error_title),
                                    getString(R.string.login_error_message));
                        }
                        sendGCMKeyTOAppServer();
                        sendBroadcast(new Intent(getApplicationContext(), GcmBroadcastReceiver.class));

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        stopSpinner();
                        if (error.getResponse() != null &&
                                error.getResponse().getStatus() == 400) {
                            String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                            if (!StringUtils.isEmpty(errorMsg)) {
                                ViewUtil.alert(LoginActivity.this,
                                        getString(R.string.login_error_title),
                                        errorMsg);
                            } else {
                                ViewUtil.alert(LoginActivity.this,
                                        getString(R.string.login_error_title),
                                        getString(R.string.login_id_error_message));
                            }
                        } else {
                            ViewUtil.alert(LoginActivity.this,
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

        String key = ViewUtil.getResponseBody(response);
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

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }


    private void sendGCMKeyTOAppServer(){
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        AppController.getApi().saveGCMkey(registrationId,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

}