package com.pointters.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.response.GetUserResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CallLoginApiIfFails;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aniljha on 10/8/17.
 */

public class GetUserDataService extends Service implements OnApiFailDueToSessionListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    final String GET_USER_DATA = "getUserData";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        callGetUserDetailsApi();

        return super.onStartCommand(intent, flags, startId);

    }

    private void callGetUserDetailsApi() {

        /*ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetUserResponse> response = apiService.getUserInformation(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));

        response.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, retrofit2.Response<GetUserResponse> rawResponse) {
                try {

                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                        saveUserModelInSharedPreferences(rawResponse.body());

                        stopSelf();

                    } else if (rawResponse.code() == 401) {

                        // we will have to call login api here
                        CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(GetUserDataService.this, GET_USER_DATA);
                        callLoginApiIfFails.OnApiFailDueToSessionListener(GetUserDataService.this);

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    stopSelf();
                }
            }

            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable throwable) {

                stopSelf();
            }
        });
*/
    }


    private void saveUserModelInSharedPreferences(GetUserResponse getUserResponse) {

        Gson gson = new Gson();
        String json = gson.toJson(getUserResponse);
        editor.putString(ConstantUtils.PREF_USER_DATA_MODEL, json);
        editor.apply();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onApiFail(String apiSource) {

        if (apiSource.equals(GET_USER_DATA)) {

            callGetUserDetailsApi();

        }

    }
}
