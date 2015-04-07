
package miniBean.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.app.MyApi;
import miniBean.util.ActivityUtil;
import miniBean.util.Validation;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class SignupActivity extends Activity {

    private static String APP_ID = "798543453496777";
    private EditText lastName,firstName,email,password,repeatPassword;
    private Facebook facebook = new Facebook(APP_ID);
    private Button signupButton;
    private PopupWindow signupSuccessPopup;
    private ImageView facebookButton;
    private ProgressBar spinner;
    public SharedPreferences session = null;
    private ActivityUtil activityUtil;
    private static final String[] REQUEST_FACEBOOK_PERMISSIONS = {
            "public_profile","email","user_friends"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.base_url))
                .setClient(new OkClient())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        AppController.api = restAdapter.create(MyApi.class);

        setContentView(R.layout.signup);


        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.signup_actionbar);

        lastName= (EditText) findViewById(R.id.lastNameEdittext);
        firstName= (EditText) findViewById(R.id.firstNameEdittext);
        email= (EditText) findViewById(R.id.emailEdittext);
        password= (EditText) findViewById(R.id.passwordEdittext);
        repeatPassword= (EditText) findViewById(R.id.repeatPassword);
        signupButton= (Button) findViewById(R.id.signupButton);
        facebookButton= (ImageView) findViewById(R.id.facebookButton);
        spinner= (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);

        session = getSharedPreferences("prefs", 0);

        activityUtil = new ActivityUtil(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int validation=showValidation();
                int passwordCheck=passwordCompare(password.getText().toString(),repeatPassword.getText().toString());

                if(validation==0&&passwordCheck==0) {
                    signUp(lastName.getText().toString(), firstName.getText().toString(), email.getText().toString(), password.getText().toString(), repeatPassword.getText().toString());
                }else{
                    Toast.makeText(SignupActivity.this,"Enter Data Again",Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginToFacebook();

            }
        });

        signupViews();

    }
    public void signUp(String lname,String fname,String email,String password,String repeatPassword){
        AppController.api.signUp(lname,fname,email,password,repeatPassword,new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if(response.getStatus()==200){
                        intiateSuccessPopup();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void intiateSuccessPopup() {
        try {

            LayoutInflater inflater = (LayoutInflater) SignupActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.signupsucceess_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            TextView emailText= (TextView) layout.findViewById(R.id.emailText);

            signupSuccessPopup = new PopupWindow(
                    layout,300, ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            signupSuccessPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            signupSuccessPopup.setOutsideTouchable(false);
            signupSuccessPopup.setFocusable(true);
            signupSuccessPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            emailText.setText(email.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signupViews()
    {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Validation.isEmailAddress(email, true);

            }
        });

    }

    public int showValidation()
    {
        int validData = 0;
        if(lastName.getText().toString().equals(""))
        {
            Validation.hasText(lastName);
            validData = 1;
        }
        if(email .getText().toString().equals(""))
        {
            Validation.hasText(email );
            validData = 1;
        }
        if(firstName.getText().toString().equals(""))
        {
            Validation.hasText(firstName );
            validData = 1;
        }
        if(password.getText().toString().equals(""))
        {
            Validation.hasText(password);
            validData = 1;
        }
        if(repeatPassword.getText().toString().equals(""))
        {
            Validation.hasText(repeatPassword);
            validData = 1;
        }

        return validData;
    }

    public int passwordCompare(String password,String rePassword)
    {
        if(password.equals(rePassword)){

            return 0;
        }else {
            repeatPassword.setError("INCORRECT");
            return 1;
        }

    }


    private void loginToFacebook() {
        String access_token = session.getString("access_token", null);
        long expires = session.getLong("access_expires", 0);

        Log.d(this.getClass().getSimpleName(), "loginToFacebook: access_token - " + access_token);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
            doLoginUsingAccessToken(access_token);
            facebookButton.setVisibility(View.INVISIBLE);
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
        spinner.setVisibility(View.VISIBLE);
        spinner.bringToFront();

        Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken: access_token - " + access_token);
        AppController.api.loginByFacebbok(access_token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(this.getClass().getSimpleName(), "doLoginUsingAccessToken.success");
                if (saveToSession(response)) {

                    /*
                    Intent i = new Intent(LoginActivity.this, ActivityMain.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                } else {
                    alert(R.string.login_error_title, R.string.login_error_message);
                }

                spinner.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                spinner.setVisibility(View.GONE);
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
    private void alert(int title, int message) {
        alert(getString(title), getString(message));
    }
}

