package com.pointters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;

public class CallLoginApiIfFails {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private OnApiFailDueToSessionListener onApiFailDueToSessionListener;

    /**
     * Log-in api is executed if session expired as per user login type( Email or Facebook)
     */

    public CallLoginApiIfFails(Context context, final String apiSource) {

        sharedPreferences = context.getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, false)) {

            UserFacebookLoginRequest userFacebookLoginRequest = new UserFacebookLoginRequest(sharedPreferences.getString(ConstantUtils.PREF_USER_FB_AUTH_TOKEN, ""));

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserFacebookLoginResponse> response = apiService.userLoginViaFacebook(userFacebookLoginRequest);

            response.enqueue(new Callback<UserFacebookLoginResponse>() {
                @Override
                public void onResponse(Call<UserFacebookLoginResponse> call, retrofit2.Response<UserFacebookLoginResponse> rawResponse) {
                    try {

                        if (rawResponse.code() == 200 && rawResponse.body() != null) {

                            editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                            editor.apply();

                            onApiFailDueToSessionListener.onApiFail(apiSource);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UserFacebookLoginResponse> call, Throwable throwable) {

                }
            });


        } else if (sharedPreferences.getBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, false)) {

            UserEmailLoginRequest userEmailLoginRequest = new UserEmailLoginRequest(sharedPreferences.getString(ConstantUtils.PREF_USER_EMAIL, ""), sharedPreferences.getString(ConstantUtils.PREF_USER_PASSWORD, ""));

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserEmailLoginResponse> response = apiService.userLoginViaEmail(userEmailLoginRequest);

            response.enqueue(new Callback<UserEmailLoginResponse>() {
                @Override
                public void onResponse(Call<UserEmailLoginResponse> call, retrofit2.Response<UserEmailLoginResponse> rawResponse) {
                    try {

                        //Getting response here....
                        if (rawResponse.code() == 200 && rawResponse.body() != null) {

                            editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                            editor.apply();

                            onApiFailDueToSessionListener.onApiFail(apiSource);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UserEmailLoginResponse> call, Throwable throwable) {

                }
            });

        }

    }

    public void OnApiFailDueToSessionListener(OnApiFailDueToSessionListener onApiFailDueToSessionListener) {

        this.onApiFailDueToSessionListener = onApiFailDueToSessionListener;
    }

}
