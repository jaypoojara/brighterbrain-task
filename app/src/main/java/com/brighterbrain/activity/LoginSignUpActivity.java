package com.brighterbrain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brighterbrain.AppController;
import com.brighterbrain.R;
import com.brighterbrain.util.Constants;
import com.brighterbrain.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvForgotPassword;
    private Button btnSignIn;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        findViews();
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-04-21 15:31:17 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-04-21 15:31:17 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnSignIn) {
            if (validate()) {
                Call<ResponseBody> call = AppController.getInstance().getApiInterface().login(edtUsername.getText().toString(), edtPassword.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getBoolean("status")) {
                                    Toasty.success(LoginSignUpActivity.this, jsonObject.getString("message")).show();
                                    AppController.getInstance().getMySharedPreferences().storeSharedValue(Constants.PREFERENCE_IS_LOGGED_IN, true);
                                    AppController.getInstance().getMySharedPreferences().storeSharedValue(Constants.PREFERENCE_USER_ID, jsonObject.getString("id"));
                                    startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                                    finish();
                                } else
                                    Toasty.error(LoginSignUpActivity.this, jsonObject.getString("message")).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        } else if (v == tvSignUp) {
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }

    private boolean validate() {
        if (!Utils.isEmptyEditText(edtUsername, "please enter username")
                && !Utils.isEmptyEditText(edtPassword, "please enter password")
                && Utils.isValidEmail(edtUsername.getText().toString(), edtUsername)) {
            return true;
        }
        return false;
    }


}
