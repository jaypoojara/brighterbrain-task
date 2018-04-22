package com.brighterbrain.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by jaypoojara on 21-04-2018.
 */
public interface APIInterface {
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email,@Field("password") String password);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUp(@Field("full_name") String fullName,@Field("email") String email,@Field("mobile_number") String mobileNumber);

    @Multipart
    @POST("save-item")
    Call<ResponseBody> uploadData(@Part("user_id") RequestBody userId
    , @Part("name") RequestBody name
            , @Part("description") RequestBody description
            , @Part("latitude") RequestBody latitude
            , @Part("longitude") RequestBody longitude
            , @Part("cost") RequestBody cost
            , @Part MultipartBody.Part[] images);
}
