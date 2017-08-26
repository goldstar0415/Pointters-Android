package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.request.UserEmailSignUpRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.response.UserEmailSignUpResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import org.json.JSONObject;

import java.util.Arrays;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 18-Jul-17.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, OnEditTextChangeListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private Button btnSignUpEmail;
    private Button btnSignUpFb;
    private TextView txtTermsConditions;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtReEnterPassword;
    private ImageView imgValidEmail;
    private CallbackManager callbackManager;
    private TextInputLayout txtInputEmail;
    private TextInputLayout txtInputPassword;
    private TextInputLayout txtInputRePassword;
    private SpotsDialog spotsDialog;
    private AccessToken fbAccessToken;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(RegistrationActivity.this, getResources().getString(R.string.app_name),
                R.drawable.back_icon_grey, 0);

        setOnClick();

        makeSpannableText();

        /*calligraphy library not applying
        fonts to text input layout hence
        done programmatically*/
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                txtInputEmail
                , txtInputPassword
                , txtInputRePassword});

        setEditTextListener();

        registerFbCallBack();

    }

    private void registerFbCallBack() {

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

        spotsDialog = new SpotsDialog(RegistrationActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);

        UserFacebookLoginRequest userFacebookLoginRequest = new UserFacebookLoginRequest(fbAccessToken.getToken());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserFacebookLoginResponse> response = apiService.userLoginViaFacebook(userFacebookLoginRequest);

        response.enqueue(new Callback<UserFacebookLoginResponse>() {
            @Override
            public void onResponse(Call<UserFacebookLoginResponse> call, retrofit2.Response<UserFacebookLoginResponse> rawResponse) {
                try {

                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.cancel();
                    }

                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, false);
                        editor.apply();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserFacebookLoginResponse> call, Throwable throwable) {

                if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }
            }
        });

    }


    private void setEditTextListener() {

        //listener for action done button click
        edtReEnterPassword.setOnEditorActionListener(this);

        //Custom Edit text change listener with returning id of edit text
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail, this));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword, this));
        edtReEnterPassword.addTextChangedListener(new MyTextWatcher(edtReEnterPassword, this));
    }


    private void initViews() {

        btnSignUpEmail = (Button) findViewById(R.id.btn_email);
        btnSignUpFb = (Button) findViewById(R.id.btn_fb);
        txtTermsConditions = (TextView) findViewById(R.id.txt_agree_to_terms_conditions);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtReEnterPassword = (EditText) findViewById(R.id.edt_re_enter_password);
        imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        txtInputPassword = (TextInputLayout) findViewById(R.id.text_input_password);
        txtInputRePassword = (TextInputLayout) findViewById(R.id.text_input_re_enter_password);
        txtInputEmail = (TextInputLayout) findViewById(R.id.text_input_email);

    }


    private void setOnClick() {

        btnSignUpEmail.setOnClickListener(this);
        btnSignUpFb.setOnClickListener(this);
        findViewById(R.id.txt_sign_in).setOnClickListener(this);

    }

    private void makeSpannableText() {

        //Spannable String builder for Terms & conditions and privacy
        SpannableStringBuilder spannableTermsConditions = new SpannableStringBuilder(getResources().getString(
                R.string.agree_to_terms_conditions));

        ClickableSpan spanTerms = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Terms And Condition on Click
               // Toast.makeText(RegistrationActivity.this, "term's & Conditions", Toast.LENGTH_SHORT).show();

            }
        };

        ClickableSpan spanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //privacy onClick
             //   Toast.makeText(RegistrationActivity.this, "Privacy Conditions", Toast.LENGTH_SHORT).show();

            }
        };

        spannableTermsConditions.setSpan(spanTerms, 9, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTermsConditions.setSpan(spanPrivacy, 33, spannableTermsConditions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtTermsConditions.setText(spannableTermsConditions);
        txtTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toolbar_lft_img:

                onBackPressed();

                break;

            case R.id.btn_email:

                performSignUpEmail();

                break;

            case R.id.btn_fb:

                if (ConnectivityController.isNetworkAvailable(RegistrationActivity.this)) {

                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_work_history", "user_location"));

                } else {

                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.txt_sign_in:

                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();

                break;
        }

    }

    private void performSignUpEmail() {

        boolean isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(new TextInputLayout[]{txtInputEmail, txtInputPassword, txtInputRePassword},
                getResources().getStringArray(R.array.registration_errors));

        if (isRequiredFieldsFilled) {

            // Validation is passed and password is also matching
            if (edtPassword.getText().toString().equals(edtReEnterPassword.getText().toString())) {

                if (ConnectivityController.isNetworkAvailable(RegistrationActivity.this)) {
                    callUserSignUpApi();
                } else {
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }

            } else {

                txtInputRePassword.setError(getResources().getString(R.string.password_mismatch));

            }

        }

    }

    private void callUserSignUpApi() {

        AndroidUtils.hideKeyBoard(RegistrationActivity.this);

        spotsDialog = new SpotsDialog(RegistrationActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);

        UserEmailSignUpRequest userEmailSignUpRequest = new UserEmailSignUpRequest(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<UserEmailSignUpResponse> response = apiService.userSignUpViaEmail(userEmailSignUpRequest);

        response.enqueue(new Callback<UserEmailSignUpResponse>() {
            @Override
            public void onResponse(Call<UserEmailSignUpResponse> call, retrofit2.Response<UserEmailSignUpResponse> rawResponse) {
                try {

                    if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.cancel();
                    }

                    if (rawResponse.code() == 200 && rawResponse.body() != null) {

                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putString(ConstantUtils.PREF_USER_ID, rawResponse.body().getId());
                        editor.putString(ConstantUtils.PREF_USER_EMAIL, edtEmail.getText().toString());
                        editor.putString(ConstantUtils.PREF_USER_PASSWORD, edtPassword.getText().toString());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, true);

                        editor.apply();

                        startActivity(new Intent(RegistrationActivity.this, RegistrationDetailsActivity.class));
                        finish();

                    } else if (rawResponse.code() == 409) {

                        JSONObject jObjError = new JSONObject(rawResponse.errorBody().string());
                        txtInputEmail.setError(jObjError.getString("message"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserEmailSignUpResponse> call, Throwable throwable) {

                if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }

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
            performSignUpEmail();
        }
        return false;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FacebookSdk.isFacebookRequestCode(requestCode))
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
