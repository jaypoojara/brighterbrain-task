package com.brighterbrain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.brighterbrain.R;
import com.brighterbrain.util.Utils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.isLoggedIn(SplashScreenActivity.this)) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                } else
                    startActivity(new Intent(SplashScreenActivity.this, LoginSignUpActivity.class));
                finish();
            }
        }, 3000);
    }
}
