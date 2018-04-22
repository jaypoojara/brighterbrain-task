package com.brighterbrain;

import android.app.Application;

import com.brighterbrain.api.APIClient;
import com.brighterbrain.api.APIInterface;
import com.brighterbrain.util.MySharedPreferences;

import retrofit2.Retrofit;

/**
 * Created by jaypoojara on 21-04-2018.
 */
public class AppController extends Application {
    private static AppController instance;
    private APIInterface apiInterface;
    private MySharedPreferences mySharedPreferences;
    public static AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mySharedPreferences = new MySharedPreferences(this);
        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
    }

    public MySharedPreferences getMySharedPreferences() {
        return mySharedPreferences;
    }

    public APIInterface getApiInterface() {
        return apiInterface;
    }
}
