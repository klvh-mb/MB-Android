
package miniBean.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import miniBean.R;
import miniBean.app.AppController;
import miniBean.util.DefaultValues;
import miniBean.util.Validation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends AbstractLoginActivity {

    private EditText lastName,firstName,email,password,repeatPassword;
    private Button signupButton;
    private PopupWindow signupSuccessPopup;
    private ImageView facebookButton;
    private TextView errorMessage;
    private ProgressBar spinner;
    private CheckBox termsCheckbox, privacyCheckbox;

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
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        termsCheckbox = (CheckBox) findViewById(R.id.termsCheckbox);
        privacyCheckbox = (CheckBox) findViewById(R.id.privacyCheckbox);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);

        termsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent=new Intent(SignupActivity.this,TermsActivity.class);
                    startActivity(intent);
                }
            }
        });

        privacyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent=new Intent(SignupActivity.this,PrivacyActivity.class);
                    startActivity(intent);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    signUp(lastName.getText().toString(), firstName.getText().toString(), email.getText().toString(), password.getText().toString(), repeatPassword.getText().toString());
                } else {
                    Toast.makeText(SignupActivity.this,getString(R.string.signup_error_please_check),Toast.LENGTH_LONG).show();
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
                //Validation.isEmailAddress(email, true);
            }
        });
    }

    private void signUp(String lname,String fname,String email,String password,String repeatPassword) {
        showErrorMessage(false);

        AppController.getApi().signUp(lname,fname,email,password,repeatPassword, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if(response.getStatus() == 200){
                    initSuccessPopup();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.getResponse().getStatus() == 400){
                    showErrorMessage(true);
                }
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
        LoginActivity.startLoginActivity(this);
    }

    private void showErrorMessage(boolean show) {
        if (show)
            errorMessage.setVisibility(View.VISIBLE);
        else
            errorMessage.setVisibility(View.INVISIBLE);
    }

    private void initSuccessPopup() {
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

    private boolean isValid() {
        boolean valid = true;
        if (!Validation.hasText(lastName))
            valid = false;
        if (!Validation.hasText(firstName))
            valid = false;
        if (!Validation.hasText(email) || !Validation.isEmailAddress(email))
            valid = false;
        if (!Validation.hasText(password))
            valid = false;
        if (!Validation.hasText(repeatPassword))
            valid = false;
        if (!isPasswordValid(password.getText().toString(),repeatPassword.getText().toString()))
            valid = false;
        return valid;
    }

    private boolean isPasswordValid(String password,String rePassword) {
        if (password.length() < DefaultValues.MIN_CHAR_SIGNUP_PASSWORD) {
            repeatPassword.setError(getString(R.string.signup_error_password_min_char));
            return false;
        } else if (StringUtils.isEmpty(password) || StringUtils.isEmpty(rePassword) || !password.equals(rePassword)) {
            repeatPassword.setError(getString(R.string.signup_error_password_not_identical));
            return false;
        }
        repeatPassword.setError(null);
        return true;
    }
}

