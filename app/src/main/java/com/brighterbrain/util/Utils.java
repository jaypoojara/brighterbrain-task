package com.brighterbrain.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

/**
 * Created by jaypoojara on 21-04-2018.
 */
public class Utils {
    static ProgressDialog progressDialog;

    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void showProgressBar(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    public static void endProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static boolean isLoggedIn(Context activity) {
        return getSharedPreference(activity).getBoolean(Constants.PREFERENCE_IS_LOGGED_IN, false);
    }

    public static boolean isEmptyEditText(EditText edt, String msg) {
        if (edt.getText().toString().isEmpty()) {
            edt.setError(msg);
            edt.requestFocus();
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(CharSequence target, EditText editText) {
        if (target == null) {
            editText.setError("Please enter email address.");
            editText.requestFocus();
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
                editText.setError("Please enter valid email address.");
                editText.requestFocus();
            }
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
