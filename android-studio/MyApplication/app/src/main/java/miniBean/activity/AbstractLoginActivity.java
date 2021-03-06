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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

// FB API v4.0
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.TrackedFragmentActivity;
import miniBean.util.SharedPreferencesUtil;
import miniBean.util.ViewUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * For Hosting activity, set the no history property as false if have
 * android:noHistory="false"
 *
 * For Activity to be started, set the launch mode as standard if have
 * android:launchMode="standard"
 *
 * http://stackoverflow.com/a/14088099
 */
public abstract class AbstractLoginActivity extends TrackedFragmentActivity {

    private View loginButton;
    private View facebookButton;

    protected void setLoginButton(View loginButton) {
        this.loginButton = loginButton;
    }

    protected void setFacebookButton(View facebookButton) {
        this.facebookButton = facebookButton;
    }

    // Your Facebook APP ID
    protected static String APP_ID = "798543453496777"; // Replace with your App ID

    protected static final String[] REQUEST_FACEBOOK_PERMISSIONS = {
            "public_profile","email","user_friends"
    };

    // Instance of Facebook Class
    protected Facebook facebook = new Facebook(APP_ID);

    // FB API v4.0
    //protected CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FB API v4.0
        /*
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onComplete: fb doLoginUsingAccessToken");
                doLoginUsingAccessToken(loginResult.getAccessToken().getToken(), spinner);
            }

            @Override
            public void onCancel() {
                stopSpinner();
                Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onCancel: fb login cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                stopSpinner();
                ActivityUtil.alert(AbstractLoginActivity.this,
                        getString(R.string.login_error_title),
                        getString(R.string.login_error_message));
                e.printStackTrace();
            }
        });
        */
    }

    protected void loginToFacebook() {
        showSpinner();

        // FB API v4.0
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(REQUEST_FACEBOOK_PERMISSIONS));

        // FB API v3.0
        String access_token = SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.FB_ACCESS_TOKEN);
        long expires = SharedPreferencesUtil.getInstance().getLong(SharedPreferencesUtil.FB_ACCESS_EXPIRES);

        Log.d(this.getClass().getSimpleName(), "loginToFacebook: access_token - " + access_token);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
            doLoginUsingAccessToken(access_token);
            //fbLoginButton.setVisibility(View.INVISIBLE);
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
                    //Facebook.FORCE_DIALOG_AUTH,     // force traditional dialog box instead of new integrated login dialogbox
                    new Facebook.DialogListener() {

                        @Override
                        public void onComplete(Bundle values) {
                            Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onComplete: fb doLoginUsingAccessToken");
                            doLoginUsingAccessToken(facebook.getAccessToken());
                        }

                        @Override
                        public void onError(DialogError error) {
                            stopSpinner();
                            ViewUtil.alert(AbstractLoginActivity.this,
                                    getString(R.string.login_error_title),
                                    getString(R.string.login_error_message));
                            Log.e(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onError", error);
                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            stopSpinner();
                            ViewUtil.alert(AbstractLoginActivity.this,
                                    getString(R.string.login_error_title),
                                    getString(R.string.login_error_message));
                            Log.e(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onFacebookError", fberror);
                        }

                        @Override
                        public void onCancel() {
                            stopSpinner();
                            Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onCancel");
                        }

                    });
            Log.d(this.getClass().getSimpleName(), "loginToFacebook: completed");
        }
    }

    protected void doLoginUsingAccessToken(String access_token) {
        showSpinner();

        Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken: access_token - " + access_token);
        AppController.getApi().loginByFacebook(access_token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                stopSpinner();

                Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken.success");
                if (!saveToSession(response)) {
                    ViewUtil.alert(AbstractLoginActivity.this,
                            getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                stopSpinner();
                ViewUtil.alert(AbstractLoginActivity.this,
                        getString(R.string.login_error_title),
                        getString(R.string.login_error_message));
                Log.e(AbstractLoginActivity.class.getSimpleName(), "doLoginUsingAccessToken: failure", error);
            }
        });
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getSimpleName(), "onActivityResult: callbackManager - requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        try {
            // FB API v3.0
            facebook.authorizeCallback(requestCode, resultCode, data);

            // FB API v4.0
            //callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), "onActivityResult: callbackManager exception");
            e.printStackTrace();
        }
    }

    protected void showSpinner() {
        showSpinner(true);
    }

    protected void stopSpinner() {
        showSpinner(false);
    }

    private void showSpinner(boolean show) {
        if (show) {
            ViewUtil.showSpinner(this);
        } else {
            ViewUtil.stopSpinner(this);
        }

        if (loginButton != null) {
            loginButton.setEnabled(!show);
        }
        if (facebookButton != null) {
            facebookButton.setEnabled(!show);
        }
    }
}

