
package miniBean.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.Validation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends AbstractLoginActivity {

    private EditText lastName,firstName,email,password,repeatPassword;
    private Button signupButton;
    private PopupWindow signupSuccessPopup;
    private ImageView facebookButton;
    private ProgressBar spinner;
    public SharedPreferences session = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_activity);

        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setCustomView(R.layout.signup_actionbar);
        getActionBar().hide();

        lastName = (EditText) findViewById(R.id.lastNameEditText);
        firstName = (EditText) findViewById(R.id.firstNameEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        repeatPassword = (EditText) findViewById(R.id.repeatPasswordEditText);
        signupButton = (Button) findViewById(R.id.signupButton);
        facebookButton = (ImageView) findViewById(R.id.facebookButton);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int validation=showValidation();
                int passwordCheck=passwordCompare(password.getText().toString(),repeatPassword.getText().toString());

                if (validation == 0 && passwordCheck == 0) {
                    signUp(lastName.getText().toString(), firstName.getText().toString(), email.getText().toString(), password.getText().toString(), repeatPassword.getText().toString());
                } else {
                    Toast.makeText(SignupActivity.this,"Enter Data Again",Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToFacebook(spinner);
            }
        });

        facebookButton.requestFocus();

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

    private void signUp(String lname,String fname,String email,String password,String repeatPassword) {
        AppController.api.signUp(lname,fname,email,password,repeatPassword, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if(response.getStatus() == 200){
                    initiateSuccessPopup();
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

        if (signupSuccessPopup != null) {
            signupSuccessPopup.dismiss();
            signupSuccessPopup = null;
        }
        LoginActivity.startLoginActivity(SignupActivity.this);
    }

    private void initiateSuccessPopup() {
        try {
            LayoutInflater inflater = (LayoutInflater) SignupActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.signup_succeess_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            TextView emailText = (TextView) layout.findViewById(R.id.emailText);
            Button okButton = (Button) layout.findViewById(R.id.okButton);

            signupSuccessPopup = new PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            //signupSuccessPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            //signupSuccessPopup.setOutsideTouchable(false);
            signupSuccessPopup.setFocusable(true);
            signupSuccessPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            emailText.setText(email.getText().toString());

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SignupActivity.this.onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int showValidation() {
        int validData = 0;
        if(lastName.getText().toString().equals("")) {
            Validation.hasText(lastName);
            validData = 1;
        }
        if(email.getText().toString().equals("")) {
            Validation.hasText(email );
            validData = 1;
        }
        if(firstName.getText().toString().equals("")) {
            Validation.hasText(firstName );
            validData = 1;
        }
        if(password.getText().toString().equals("")) {
            Validation.hasText(password);
            validData = 1;
        }
        if(repeatPassword.getText().toString().equals("")) {
            Validation.hasText(repeatPassword);
            validData = 1;
        }

        return validData;
    }

    private int passwordCompare(String password,String rePassword) {
        if(password.equals(rePassword)){
            return 0;
        } else {
            repeatPassword.setError("INCORRECT");
            return 1;
        }
    }
}

