/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package miniBean.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.ActivityUtil;
import miniBean.util.AnimationUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    // Your Facebook APP ID
    private static String APP_ID = "798543453496777"; // Replace with your App ID

    // Instance of Facebook Class
    private Facebook facebook = new Facebook(APP_ID);
    private ProgressBar spinner;
    public SharedPreferences session = null;
    private EditText username = null;
    private EditText password = null;
    private TextView login;
    private ImageView btnFbLogin;

    private ActivityUtil activityUtil;

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

    private static final String[] REQUEST_FACEBOOK_PERMISSIONS = {
            "public_profile","email","user_friends"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        SplashActivity.init();

        topicCommunityTabLoaded = false;
        yearCommunityTabLoaded = false;

        session = getSharedPreferences("prefs", 0);
        spinner = (ProgressBar)findViewById(R.id.spinner);

        activityUtil = new ActivityUtil(this);

        APP_ID = getResources().getString(R.string.app_id);

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        btnFbLogin = (ImageView) findViewById(R.id.buttonFbLogin);
        login = (TextView) findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnimationUtil.show(spinner);

                AppController.api.login(username.getText().toString(), password.getText().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        if (saveToSession(response)) {
                            startMainActivity();
                            /*
                            Intent i = new Intent(LoginActivity.this, ActivityMain.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            */
                        } else {
                            alert(R.string.login_error_title, R.string.login_error_message);
                        }
                        AnimationUtil.cancel(spinner);
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

        /*
         * Login my_community_fragement Click event
		 * */
        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToFacebook();
            }
        });
    }

    private void loginToFacebook() {
        String access_token = session.getString("access_token", null);
        long expires = session.getLong("access_expires", 0);

        Log.d(this.getClass().getSimpleName(), "loginToFacebook: access_token - " + access_token);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
            doLoginUsingAccessToken(access_token);
            btnFbLogin.setVisibility(View.INVISIBLE);
        }

        Log.d(this.getClass().getSimpleName(), "loginToFacebook: expires - " + expires);
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        Log.d(this.getClass().getSimpleName(), "loginToFacebook: isSessionValid - " + facebook.isSessionValid());
        if (!facebook.isSessionValid()) {
            Log.d(this.getClass().getSimpleName(), "loginToFacebook: authorize");
            facebook.authorize(
                    this,
                    REQUEST_FACEBOOK_PERMISSIONS,
                    Facebook.FORCE_DIALOG_AUTH,     // force traditional dialog box instead of new integrated login dialogbox
                    new Facebook.DialogListener() {

                        @Override
                        public void onComplete(Bundle values) {
                            Log.d(this.getClass().getSimpleName(), "loginToFacebook.onComplete: fb doLoginUsingAccessToken");
                            doLoginUsingAccessToken(facebook.getAccessToken());
                        }

                        @Override
                        public void onError(DialogError error) {
                            alert(R.string.login_error_title, R.string.login_error_message);
                            error.printStackTrace();
                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            alert(R.string.login_error_title, R.string.login_error_message);
                            fberror.printStackTrace();
                        }

                        @Override
                        public void onCancel() {
                            Log.d(this.getClass().getSimpleName(), "loginToFacebook.onCancel: fb login cancelled");
                        }

                    });
            Log.d(this.getClass().getSimpleName(), "loginToFacebook: completed");
        }
    }

    private void doLoginUsingAccessToken(String access_token) {
        AnimationUtil.show(spinner);

        Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken: access_token - " + access_token);
        AppController.api.loginByFacebbok(access_token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken.success");
                if (saveToSession(response)) {
                    startMainActivity();
                    /*
                    Intent i = new Intent(LoginActivity.this, ActivityMain.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                } else {
                    alert(R.string.login_error_title, R.string.login_error_message);
                }

                AnimationUtil.cancel(spinner);
            }

            @Override
            public void failure(RetrofitError error) {
                AnimationUtil.cancel(spinner);
                alert(R.string.login_error_title, R.string.login_error_message);
                error.printStackTrace();
            }
        });
    }

    private boolean saveToSession(Response response) {
        if (response == null) {
            return false;
        }

        String key = activityUtil.getResponseBody(response);
        Log.d(this.getClass().getSimpleName(), "saveToSession: sessionID - " + key);
        session.edit().putString("sessionID", key).apply();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(this.getClass().getSimpleName(), "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getSimpleName(), "onActivityResult: facebook.authorizeCallback - requestCode:"+requestCode+" resultCode:"+resultCode+" data:"+data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_app)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppController.getInstance().exitApp();
                        //finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alert(int title, int message) {
        alert(getString(title), getString(message));
    }

    private void alert(String title, String message) {
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //LoginActivity.this.finish();
                    }
                })
                .show();
    }
}

