package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.service.GetUserDataService;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, OnEditTextChangeListener, OnApiFailDueToSessionListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLoginEmail;
    private Button btnLoginFb;
    private EditText edtEmail;
    private EditText edtPassword;
    private ImageView imgValidEmail;
    private TextInputLayout txtInputEmail;
    private TextInputLayout txtInputPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SpotsDialog spotsDialog;
    private CallbackManager callbackManager;
    private AccessToken fbAccessToken;
    private TextView txtWarning;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(LoginActivity.this, getResources().getString(R.string.app_name),
                R.drawable.back_icon_grey, 0);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.text_input_email),
                (TextInputLayout) findViewById(R.id.text_input_password)});

        setEditTextListener();

        loginFbCallBack();

    }

    private void setEditTextListener() {

        //listener for action done button click
        edtPassword.setOnEditorActionListener(this);

        //Custom Edit text change listener with returning id of edit text
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail, this));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword, this));
    }

    private void initViews() {

        btnLoginEmail = (Button) findViewById(R.id.btn_email_login);
        btnLoginFb = (Button) findViewById(R.id.btn_fb);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        //   imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        txtInputEmail = (TextInputLayout) findViewById(R.id.text_input_email);
        txtInputPassword = (TextInputLayout) findViewById(R.id.text_input_password);
        txtWarning = (TextView) findViewById(R.id.txt_warning);


    }

    private void setOnClick() {
        btnLoginEmail.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.layout_sign_up).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toolbar_lft_img:
                AndroidUtils.hideKeyBoard(LoginActivity.this);
                onBackPressed();

                break;

            case R.id.btn_email_login:

                performLoginEmail();

                break;

            case R.id.btn_fb:

                if (ConnectivityController.isNetworkAvailable(LoginActivity.this)) {

                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_location"));

                } else {

                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_forgot_password:

                startActivity(new Intent(this, ForgotPasswordActivity.class));

                break;

            case R.id.layout_sign_up:

                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

                break;
        }

    }


    private void loginFbCallBack() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        fbAccessToken = loginResult.getAccessToken();
                        AccessToken.setCurrentAccessToken(fbAccessToken);

                        doLoginAttemptUsingFacebook();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

    }

    private void doLoginAttemptUsingFacebook() {

        if (fbAccessToken == null || TextUtils.isEmpty(fbAccessToken.getToken())) {
            return;
        }

        /*spotsDialog = new SpotsDialog(LoginActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);
*/
        UserFacebookLoginRequest userFacebookLoginRequest = new UserFacebookLoginRequest(fbAccessToken.getToken());

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<UserFacebookLoginResponse> response = apiService.userLoginViaFacebook(userFacebookLoginRequest);

        response.enqueue(new Callback<UserFacebookLoginResponse>() {
            @Override
            public void onResponse(Call<UserFacebookLoginResponse> call, retrofit2.Response<UserFacebookLoginResponse> rawResponse) {
                try {

                  /*  if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.cancel();
                    }
*/
                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putString(ConstantUtils.PREF_ID, rawResponse.body().getId());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, false);
                        editor.apply();
                        getUserDataApiCall();
                      /*  Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
*/
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserFacebookLoginResponse> call, Throwable throwable) {

              /*  if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }*/

            }
        });

    }

    private void performLoginEmail() {

        boolean isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(LoginActivity.this, new TextInputLayout[]{txtInputEmail, txtInputPassword},
                getResources().getStringArray(R.array.login_details_errors));

        if (isRequiredFieldsFilled) {

            if (ConnectivityController.isNetworkAvailable(LoginActivity.this)) {
                callUserLoginApi();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
            }

            startService(new Intent(LoginActivity.this, GetUserDataService.class));

        }
    }

    private void callUserLoginApi() {

        AndroidUtils.hideKeyBoard(LoginActivity.this);
/*
        spotsDialog = new SpotsDialog(LoginActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);*/

        UserEmailLoginRequest userEmailLoginRequest = new UserEmailLoginRequest(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<UserEmailLoginResponse> response = apiService.userLoginViaEmail(userEmailLoginRequest);

        response.enqueue(new Callback<UserEmailLoginResponse>() {
            @Override
            public void onResponse(Call<UserEmailLoginResponse> call, retrofit2.Response<UserEmailLoginResponse> rawResponse) {
                try {
/*
                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }*/

                    //Getting response here....
                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                        txtWarning.setVisibility(View.GONE);

                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putString(ConstantUtils.PREF_USER_EMAIL, edtEmail.getText().toString().trim());
                        editor.putString(ConstantUtils.PREF_USER_PASSWORD, edtPassword.getText().toString().trim());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, true);
                        editor.apply();
                        getUserDataApiCall();
                        // startService(new Intent(LoginActivity.this, GetUserDataService.class));
                       /* if (sharedPreferences.getBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, false)) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            finish();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else
                            startActivity(new Intent(getApplicationContext(), RegistrationDetailsActivity.class));*/


                    } else if (rawResponse.code() == 401 || rawResponse.code() == 404) {

                        txtWarning.setVisibility(View.VISIBLE);
                        txtWarning.setText(getResources().getString(R.string.wrong_email_password));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserEmailLoginResponse> call, Throwable throwable) {
               /* if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
*/
                txtWarning.setVisibility(View.GONE);

            }
        });
    }

    private void getUserDataApiCall() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);

        Call<Object> getUserInformation = apiService.getUserInformation(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getUserInformation.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.code() == 200) {
                    try {

                        String json = new Gson().toJson(((LinkedTreeMap) response.body()).get("user"));
                        editor.putString(ConstantUtils.USER_DATA, json).commit();
                        JSONObject jsonObject = new JSONObject(json);


                        if (jsonObject.has("completedRegistration")) {
                            editor.putBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, (Boolean) jsonObject.get("completedRegistration")).commit();

                        }
                        if (sharedPreferences.getBoolean(ConstantUtils.IS_REGISTRATION_COMPLETED, false)) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            finish();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else
                            startActivity(new Intent(getApplicationContext(), RegistrationDetailsActivity.class));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LoginActivity.this, "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LoginActivity.this);

                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            performLoginEmail();
        }
        return false;
    }

    @Override
    public void onTextChange(String text, View view) {


        txtWarning.setVisibility(View.GONE);

        EditText editText = (EditText) view;

        if (!text.trim().isEmpty()) {

            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);

        }

        switch (view.getId()) {

            case R.id.edt_email:

                /*if (AndroidUtils.isValidEmailAddress(text)) {
                    imgValidEmail.setVisibility(View.VISIBLE);
                } else {
                    imgValidEmail.setVisibility(View.GONE);
                }*/

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FacebookSdk.isFacebookRequestCode(requestCode))
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
       /* Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
        finish();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);*/
        super.onBackPressed();
    }

    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetUserApi")) {

            getUserDataApiCall();
        }
    }
}
