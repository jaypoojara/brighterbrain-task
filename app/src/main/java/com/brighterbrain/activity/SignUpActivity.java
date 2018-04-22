package com.brighterbrain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtMobileNumber;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnSave;

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            if (validate()) {
                signUp();
            }
        }
    }

    private void signUp() {
        Utils.showProgressBar(this);
        Call<ResponseBody> call = AppController.getInstance().getApiInterface().signUp(edtFullName.getText().toString(), edtEmail.getText().toString(), edtMobileNumber.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.endProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.getBoolean("status")) {
                            Toasty.success(SignUpActivity.this, jsonObject.getString("message")).show();
                            AppController.getInstance().getMySharedPreferences().storeSharedValue(Constants.PREFERENCE_IS_LOGGED_IN, true);
                            AppController.getInstance().getMySharedPreferences().storeSharedValue(Constants.PREFERENCE_USER_ID, jsonObject.getJSONObject("user").getString("id"));
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else
                            Toasty.error(SignUpActivity.this, jsonObject.getString("message")).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.endProgressDialog();
                t.printStackTrace();
            }
        });
    }

    private boolean validate() {

        if (!Utils.isEmptyEditText(edtFullName, "Please enter your full name")
                && !Utils.isEmptyEditText(edtPassword, "Please enter password")
                && !Utils.isEmptyEditText(edtFullName, "Please confirm your password")
                && !Utils.isEmptyEditText(edtFullName, "Please enter your full name")
                && Utils.isValidEmail(edtEmail.getText().toString(), edtEmail)) {
            if (edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                return true;
            } else {
                edtConfirmPassword.setError("Password does not match");
            }
        }
        return false;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
    }
}
