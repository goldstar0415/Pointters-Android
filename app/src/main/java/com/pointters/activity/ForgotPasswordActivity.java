package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.ResetPasswordModel;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.response.ResponseBySendOtpApi;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.MyTextWatcher;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 24-Jul-17.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, OnEditTextChangeListener, TextView.OnEditorActionListener, OnApiFailDueToSessionListener {

    private TextInputLayout txtInputEmail;
    private EditText edtEmail;
    private EditText edtVerificationCode;
    private TextInputLayout txtInputPassword,txtInputVerificationCode;
    private EditText edtPassword;
    private TextInputLayout txtInputReEnterPassword;
    private EditText edtReEnterPassword;
    private ImageView imgValidEmail;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintLayout constraintLayout;
    private SpotsDialog spotsDialog;
    private final String SEND_OTP = "sendOtp";
    private TextView txtRunTimeForgot;
private String otp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        initViews();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(ForgotPasswordActivity.this, getResources().getString(R.string.app_name),
                R.drawable.back_icon_grey, 0);

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{txtInputEmail});

        setEditTextListener();

        AndroidUtils.showKeyBoard(ForgotPasswordActivity.this);


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
        imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        constraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        txtRunTimeForgot = (TextView) findViewById(R.id.txt_runtime_forgot);


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

        spotsDialog = new SpotsDialog(ForgotPasswordActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);

        UserEmailLoginRequest userEmailLoginRequest = new UserEmailLoginRequest(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBySendOtpApi> sendOtpApiCall = apiService.sendOtp(userEmailLoginRequest);
        sendOtpApiCall.enqueue(new Callback<ResponseBySendOtpApi>() {
            @Override
            public void onResponse(Call<ResponseBySendOtpApi> call, Response<ResponseBySendOtpApi> response) {
                if (response.code() == 200 && response.body() != null) {

                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }

                    //make clone of constraint to make change once
                    applyConstraintSet.clone(constraintLayout);

                    //use to change visibility with fade animation
                    setNextLayoutVisible(R.id.layout_email, R.id.layout_new_password);

                    edtVerificationCode.requestFocus();

                    txtRunTimeForgot.setText(getResources().getString(R.string.reset_password));
                    if(response.body().getTempPassword()!=null && !response.body().getTempPassword().isEmpty()) {
                        otp = response.body().getTempPassword();
                        edtVerificationCode.setText(response.body().getTempPassword());
                    }


                }
                else if (response.code() == 401) {

                    // We will have to call login api as session is expired
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ForgotPasswordActivity.this, SEND_OTP);
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ForgotPasswordActivity.this);

                }
                else if(response.code()==404)
                {
                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }
                    txtInputEmail.setError(getResources().getString(R.string.email_does_not_exist));
                    imgValidEmail.setVisibility(View.GONE);
                }
                    else
                 {

                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBySendOtpApi> call, Throwable t) {
                if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
            }
        });


    }


    private void reSetPassword() {

        String verificationCode = edtVerificationCode.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String reEnterPassword = edtReEnterPassword.getText().toString().trim();


        if (verificationCode.isEmpty() || !verificationCode.equals(otp)) {
            txtInputVerificationCode.setError(getResources().getString(R.string.invalid_verification_code));
        } else if (password.isEmpty()) {
            txtInputPassword.setError(getResources().getString(R.string.provide_valid_password));
        } else if (reEnterPassword.isEmpty() || !reEnterPassword.equals(password)) {
            txtInputReEnterPassword.setError(getResources().getString(R.string.password_mismatch));
        } else {

            // call reset password api here and send back to login screen
            ResetPasswordModel resetPasswordModel=new ResetPasswordModel(edtEmail.getText().toString().trim(),verificationCode,password);
            AndroidUtils.hideKeyBoard(ForgotPasswordActivity.this);

            spotsDialog = new SpotsDialog(ForgotPasswordActivity.this);
            spotsDialog.show();
            spotsDialog.setCancelable(false);

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserEmailLoginResponse> resetPasswordCall=apiService.resetPassword(resetPasswordModel);
            resetPasswordCall.enqueue(new Callback<UserEmailLoginResponse>() {
                @Override
                public void onResponse(Call<UserEmailLoginResponse> call, Response<UserEmailLoginResponse> response) {
                    if (response.code() == 200 && response.body() != null) {
                        if (spotsDialog != null && spotsDialog.isShowing()) {
                            spotsDialog.dismiss();
                            startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                            finish();
                        }
                    }

                }

                @Override
                public void onFailure(Call<UserEmailLoginResponse> call, Throwable t) {
                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.dismiss();
                    }
                }
            });

        }
    }

    private void setNextLayoutVisible(int layoutGone, int layoutVisible) {

        TransitionManager.beginDelayedTransition(constraintLayout);

        applyConstraintSet.setVisibility(layoutGone, ConstraintSet.GONE);
        applyConstraintSet.setVisibility(layoutVisible, ConstraintSet.VISIBLE);
        applyConstraintSet.applyTo(constraintLayout);
    }

    @Override
    public void onTextChange(String text, View view) {

        EditText editText = (EditText) view;

        if (!text.trim().isEmpty()) {
            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
        }

        switch (view.getId()) {
            case R.id.edt_email:

                if (AndroidUtils.isValidEmailAddress(text)) {
                    imgValidEmail.setVisibility(View.VISIBLE);
                } else {
                    imgValidEmail.setVisibility(View.GONE);
                }

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
