package miniBean.util;

import android.widget.EditText;

import java.util.regex.Pattern;

import miniBean.R;
import miniBean.app.AppController;

public class Validation {

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    // Error Messages
    private static final String REQUIRED_MSG = AppController.getInstance().getString(R.string.signup_error_field_required);
    private static final String EMAIL_MSG = AppController.getInstance().getString(R.string.signup_error_email_format);

    public static boolean isEmailAddress(EditText editText) {
        return isEmailAddress(editText, true);
    }

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    // call this method when you need to check phone number validation


    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}