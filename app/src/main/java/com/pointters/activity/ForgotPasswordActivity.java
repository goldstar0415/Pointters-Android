package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.ResetPasswordModel;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 24-Jul-17.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, OnEditTextChangeListener, TextView.OnEditorActionListener, OnApiFailDueToSessionListener {

    private final String SEND_OTP = "sendOtp";
    private EditText edtEmail;
    private EditText edtVerificationCode;
    private TextInputLayout txtInputEmail, txtInputPassword, txtInputVerificationCode,txtInputReEnterPassword;
    private EditText edtPassword;
    private EditText edtReEnterPassword;
    private ImageView imgValidEmail;
    private RelativeLayout layoutForgotPassword,layoutResetPassword;
    private SpotsDialog spotsDialog;
//    private TextView txtRunTimeForgot;
private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        initViews();
        //set toolbar
        AppUtils.setToolBarWithBothIconWithShadow(ForgotPasswordActivity.this, getResources().getString(R.string.forgot_password), R.drawable.back_icon, 0);

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{txtInputEmail,txtInputVerificationCode,txtInputPassword,txtInputReEnterPassword});

        setEditTextListener();



    }

    private void initViews() {

        txtInputEmail = (TextInputLayout) findViewById(R.id.text_input_email);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtVerificationCode = (EditText) findViewById(R.id.edt_verification_code);
        txtInputPassword = (TextInputLayout) findViewById(R.id.text_input_password);
        txtInputVerificationCode = (TextInputLayout) findViewById(R.id.text_input_verification_code);

        edtPassword = (EditText) findViewById(R.id.edt_password);
        txtInputReEnterPassword = (TextInputLayout) findViewById(R.id.text_input_re_enter_password);
        edtReEnterPassword = (EditText) findViewById(R.id.edt_re_enter_password);
       // imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);

        layoutForgotPassword=(RelativeLayout)findViewById(R.id.layout_forgot_password);
        layoutResetPassword=(RelativeLayout)findViewById(R.id.layout_reset_password);
//        txtRunTimeForgot = (TextView) findViewById(R.id.txt_runtime_forgot);


    }

    private void setEditTextListener() {

        //listener for action done button click
        edtEmail.setOnEditorActionListener(this);
        edtVerificationCode.setOnEditorActionListener(this);
        edtReEnterPassword.setOnEditorActionListener(this);

        //Custom Edit text change listener with returning id of edit text
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail, this));
        edtVerificationCode.addTextChangedListener(new MyTextWatcher(edtVerificationCode, this));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword, this));
        edtReEnterPassword.addTextChangedListener(new MyTextWatcher(edtReEnterPassword, this));
    }

    private void setOnClick() {
        findViewById(R.id.btn_send_code).setOnClickListener(this);
        findViewById(R.id.btn_reset_password).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.toolbar_lft_img:
AndroidUtils.hideKeyBoard(ForgotPasswordActivity.this);
                onBackPressed();

                break;

            case R.id.btn_send_code:

                if (ConnectivityController.isNetworkAvailable(this)) {
                    checkEmailAndSendCode();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_reset_password:
                if (ConnectivityController.isNetworkAvailable(this)) {
                    reSetPassword();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    private void checkEmailAndSendCode() {

        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !AndroidUtils.isValidEmailAddress(email)) {
            txtInputEmail.setError(getResources().getString(R.string.provide_valid_email));
        } else {
            // call send otp api here
            callSendOtpApi();
        }
    }


    private void callSendOtpApi() {

        AndroidUtils.hideKeyBoard(ForgotPasswordActivity.this);

       /* spotsDialog = new SpotsDialog(ForgotPasswordActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);*/

        UserEmailLoginRequest userEmailLoginRequest = new UserEmailLoginRequest(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Void> sendOtpApiCall = apiService.sendOtp(userEmailLoginRequest);
        sendOtpApiCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
/*
                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }*/



                    //use to change visibility
                    layoutForgotPassword.setVisibility(View.GONE);
                    layoutResetPassword.setVisibility(View.VISIBLE);


//                    txtRunTimeForgot.setText(getResources().getString(R.string.reset_password));

                } else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ForgotPasswordActivity.this, SEND_OTP);
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ForgotPasswordActivity.this);

                } else if (response.code() == 404) {
                    /*if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }*/
                    txtInputEmail.setError(getResources().getString(R.string.email_does_not_exist));
                   /* imgValidEmail.setVisibility(View.GONE);*/
                }
                 /*   else
                 {

                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }
                }*/
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
             /*   if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }*/
            }
        });


    }


    private void reSetPassword() {

        String verificationCode = edtVerificationCode.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String reEnterPassword = edtReEnterPassword.getText().toString().trim();


        if (verificationCode.isEmpty()) {
            txtInputVerificationCode.setError(getResources().getString(R.string.provide_verification_code));
        }
        if (password.isEmpty()) {
            txtInputPassword.setError(getResources().getString(R.string.password_required));
        } else if (reEnterPassword.isEmpty() || !reEnterPassword.equals(password)) {
            txtInputReEnterPassword.setError(getResources().getString(R.string.password_mismatch));
        }
            else {

            // call reset password api here and send back to login screen
            ResetPasswordModel resetPasswordModel = new ResetPasswordModel(edtEmail.getText().toString().trim(), verificationCode, password);
            AndroidUtils.hideKeyBoard(ForgotPasswordActivity.this);

           /* spotsDialog = new SpotsDialog(ForgotPasswordActivity.this);
            spotsDialog.show();
            spotsDialog.setCancelable(false);*/

            ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
            Call<UserEmailLoginResponse> resetPasswordCall = apiService.resetPassword(resetPasswordModel);
            resetPasswordCall.enqueue(new Callback<UserEmailLoginResponse>() {
                @Override
                public void onResponse(Call<UserEmailLoginResponse> call, Response<UserEmailLoginResponse> response) {
                    if (response.code() == 200 && response.body() != null) {
                        /*if (spotsDialog != null && spotsDialog.isShowing()) {
                            spotsDialog.dismiss();*/
                        callUserLoginApi();
//                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
//                        finish();
                        //}
                    }else if (response.code() == 404) {
                    /*if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }*/
                        txtInputVerificationCode.setError(getResources().getString(R.string.invalid_verification_code));
                   /* imgValidEmail.setVisibility(View.GONE);*/
                    }

                }

                @Override
                public void onFailure(Call<UserEmailLoginResponse> call, Throwable t) {
                  /*  if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }*/
                }
            });

        }
    }


    private void callUserLoginApi() {
        AndroidUtils.hideKeyBoard(ForgotPasswordActivity.this);
/*
        spotsDialog = new SpotsDialog(LoginActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);*/
        String password = edtPassword.getText().toString().trim();
//        if (password.equals("test")){
//            password = "NePPBAkyjZogQzO39I96xA==";
//        }
        UserEmailLoginRequest userEmailLoginRequest = new UserEmailLoginRequest(edtEmail.getText().toString().trim(), password);
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else
                            startActivity(new Intent(getApplicationContext(), RegistrationDetailsActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ForgotPasswordActivity.this, "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ForgotPasswordActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }


    @Override
    public void onTextChange(String text, View view) {

        EditText editText = (EditText) view;

        if (!text.trim().isEmpty()) {
            if(editText.hashCode()==edtPassword.hashCode()) {
                txtInputReEnterPassword.setError(null);
                txtInputReEnterPassword.setErrorEnabled(false);
            }

            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
        }

        switch (view.getId()) {
            case R.id.edt_email:

              /*  if (AndroidUtils.isValidEmailAddress(text)) {
                    imgValidEmail.setVisibility(View.VISIBLE);
                } else {
                    imgValidEmail.setVisibility(View.GONE);
                }*/

                break;
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            switch (textView.getId()) {

                case R.id.edt_email:

                    checkEmailAndSendCode();

                    break;

                case R.id.edt_re_enter_password:

                    reSetPassword();

                    break;
            }

        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onApiFail(String apiSource) {

        if (apiSource.equals(SEND_OTP)) {

            callSendOtpApi();
        }

    }
}
