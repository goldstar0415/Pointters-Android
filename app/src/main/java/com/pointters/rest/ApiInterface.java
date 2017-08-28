package com.pointters.rest;



import com.pointters.model.ResetPasswordModel;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.request.UserEmailSignUpRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.request.UserPutRequest;
import com.pointters.model.response.GetUserResponse;
import com.pointters.model.response.ResponseBySendOtpApi;
import com.pointters.model.response.ResponsePutUser;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.model.response.UserEmailSignUpResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
import com.pointters.model.response.GetUserSettingsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;


/**
 * Created by Anil Jha on 08-Aug-16.
 */

public interface ApiInterface {

    @POST("user/signup")
    Call<UserEmailSignUpResponse>
    userSignUpViaEmail(@Body UserEmailSignUpRequest params);

    @POST("user/login")
    Call<UserEmailLoginResponse>
    userLoginViaEmail(@Body UserEmailLoginRequest params);

    @POST("user/otp")
    Call<ResponseBySendOtpApi>
    sendOtp(@Body UserEmailLoginRequest params);

    @POST("user/reset/password")
    Call<UserEmailLoginResponse>
    resetPassword(@Body ResetPasswordModel params);

    @POST("user/facebook/token")
    Call<UserFacebookLoginResponse>
    userLoginViaFacebook( @Body UserFacebookLoginRequest params);

    @GET("user")
    Call<GetUserResponse>
    getUserInformation(@Header("Authorization") String token);


    @GET("user/setting")
    Call<GetUserSettingsResponse>
    getUserSettings(@Header("Authorization") String token);

    @PUT("user")
    Call<ResponsePutUser>
    putUser(@Header("Authorization") String token,
            @Body UserPutRequest params  );

    @PUT("user/logout")
    Call<Object>
    logOut(@Header("Authorization") String token);



}

