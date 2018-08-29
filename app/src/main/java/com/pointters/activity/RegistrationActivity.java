package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.request.UserEmailSignUpRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.response.UserEmailSignUpResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
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

import java.util.Arrays;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 18-Jul-17.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, OnEditTextChangeListener, OnApiFailDueToSessionListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private CardView btnSignUpEmail;
    private CardView btnSignUpFb;
    private CardView btnSignUpGG;
    private LinkConsumableTextView txtTermsConditions;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtPhone;
    private EditText edtLocation;
    private ImageView imgValidEmail;
    private CallbackManager callbackManager;
    private TextInputLayout txtInputEmail;
    private TextInputLayout txtInputPassword;
    private TextInputLayout txtInputFirstName;
    private TextInputLayout txtInputLastName;
    private TextInputLayout txtInputPhone;
    private TextInputLayout txtInputLocation;

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
        AppUtils.setToolBarWithBothIconWithShadow(RegistrationActivity.this, getResources().getString(R.string.sign_up),
                R.drawable.back_icon, 0);

        setOnClick();

        makeSpannableText();

        /*calligraphy library not applying
        fonts to text input layout hence
        done programmatically*/
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                txtInputEmail
                , txtInputPassword
                , txtInputPhone
                , txtInputFirstName
                , txtInputLastName
                , txtInputLocation});

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
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException exception) {}
                });
    }

    private void doLoginAttemptUsingFacebook() {
        if (fbAccessToken == null || TextUtils.isEmpty(fbAccessToken.getToken())) {
            return;
        }

      /*  spotsDialog = new SpotsDialog(RegistrationActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);*/

        UserFacebookLoginRequest userFacebookLoginRequest = new UserFacebookLoginRequest(fbAccessToken.getToken());
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<UserFacebookLoginResponse> response = apiService.userLoginViaFacebook(userFacebookLoginRequest);
        response.enqueue(new Callback<UserFacebookLoginResponse>() {
            @Override
            public void onResponse(Call<UserFacebookLoginResponse> call, retrofit2.Response<UserFacebookLoginResponse> rawResponse) {
                try {
                    /*if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.cancel();
                    }
*/
                    if (rawResponse.code() == 200 && rawResponse.body() != null) {
                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, false);
                        editor.putString(ConstantUtils.PREF_ID, rawResponse.body().getId());
                        editor.apply();
                        getUserDataApiCall();
                       /* Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserFacebookLoginResponse> call, Throwable throwable) {
               /* if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }*/
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
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(RegistrationActivity.this, "callGetUserApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(RegistrationActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {}
        });

    }

    private void setEditTextListener() {
        //listener for action done button click
        edtFirstName.setOnEditorActionListener(this);
        edtLastName.setOnEditorActionListener(this);
        edtPhone.setOnEditorActionListener(this);
        edtLocation.setOnEditorActionListener(this);
        edtPassword.setOnEditorActionListener(this);
        //Custom Edit text change listener with returning id of edit text
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail, this));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword, this));
        edtFirstName.addTextChangedListener(new MyTextWatcher(edtFirstName, this));
        edtLastName.addTextChangedListener(new MyTextWatcher(edtLastName, this));
        edtPhone.addTextChangedListener(new MyTextWatcher(edtPhone, this));
        edtLocation.addTextChangedListener(new MyTextWatcher(edtLocation, this));
    }

    private void initViews() {
        btnSignUpEmail = (CardView) findViewById(R.id.btn_email);
        btnSignUpFb = (CardView) findViewById(R.id.btn_fb);
        btnSignUpGG = (CardView) findViewById(R.id.btn_gg);
        txtTermsConditions = (LinkConsumableTextView) findViewById(R.id.txt_privacy_conditions);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtPhone = (EditText) findViewById(R.id.edt_phone_number);
        edtLocation = (EditText) findViewById(R.id.edt_location);
        imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        txtInputPassword = (TextInputLayout) findViewById(R.id.text_input_password);
        txtInputFirstName = (TextInputLayout) findViewById(R.id.text_input_first_name);
        txtInputLastName = (TextInputLayout) findViewById(R.id.text_input_last_name);
        txtInputPhone = (TextInputLayout) findViewById(R.id.text_input_phone_number);
        txtInputLocation = (TextInputLayout) findViewById(R.id.text_input_location);
        txtInputEmail = (TextInputLayout) findViewById(R.id.text_input_email);
    }

    private void setOnClick() {
        btnSignUpEmail.setOnClickListener(this);
        btnSignUpFb.setOnClickListener(this);
        findViewById(R.id.layout_sign_in).setOnClickListener(this);
    }

    private void makeSpannableText() {
        final Link link = new Link("Terms of Service");
        link.setUnderlined(false);
        final Link link1 = new Link("Privacy Policy");
        link1.setUnderlined(false);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String s) {
                Intent intent = new Intent(RegistrationActivity.this, TermsActivity.class);
                intent.putExtra("type", "terms");
                startActivity(intent);
                Toast.makeText(RegistrationActivity.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }
        });

        link1.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String s) {
                Intent intent = new Intent(RegistrationActivity.this, TermsActivity.class);
                intent.putExtra("type", "privacy");
                startActivity(intent);
                Toast.makeText(RegistrationActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        });

        LinkBuilder.Companion.on(txtTermsConditions).addLink(link1).addLink(link).build();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                AndroidUtils.hideKeyBoard(RegistrationActivity.this);
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

            case R.id.layout_sign_in:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                break;
        }
    }

    private void performSignUpEmail() {
        boolean isRequiredFieldsFilled = AppUtils.isRequiredFieldsFilled(RegistrationActivity.this, new TextInputLayout[]{txtInputEmail, txtInputPassword/*, txtInputRePassword*/},
                getResources().getStringArray(R.array.registration_errors));

        if (isRequiredFieldsFilled) {
            // Validation is passed and password is also matching
//            if (edtPassword.getText().toString().equals(edtReEnterPassword.getText().toString()) || edtReEnterPassword.getText().toString().equals(edtPassword.getText().toString())) {
                if (ConnectivityController.isNetworkAvailable(RegistrationActivity.this)) {
                    callUserSignUpApi();
                } else {
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                }
//            } else {
//                txtInputRePassword.setError(getResources().getString(R.string.password_mismatch));
//            }
        }
    }

    private void callUserSignUpApi() {
        AndroidUtils.hideKeyBoard(RegistrationActivity.this);
      /*  spotsDialog = new SpotsDialog(RegistrationActivity.this);
        spotsDialog.show();
        spotsDialog.setCancelable(false);
*/
        UserEmailSignUpRequest userEmailSignUpRequest = new UserEmailSignUpRequest(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<UserEmailSignUpResponse> response = apiService.userSignUpViaEmail(userEmailSignUpRequest);
        response.enqueue(new Callback<UserEmailSignUpResponse>() {
            @Override
            public void onResponse(Call<UserEmailSignUpResponse> call, retrofit2.Response<UserEmailSignUpResponse> rawResponse) {
                try {
              /*      if (spotsDialog != null && spotsDialog.isShowing()) {
                        spotsDialog.cancel();
                    }
*/
                    if (rawResponse.code() == 200 && rawResponse.body() != null) {
                        editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, true);
                        editor.putString(ConstantUtils.PREF_TOKEN, rawResponse.body().getToken());
                        editor.putString(ConstantUtils.PREF_USER_ID, rawResponse.body().getId());
                        editor.putString(ConstantUtils.PREF_USER_EMAIL, edtEmail.getText().toString());
                        editor.putString(ConstantUtils.PREF_USER_PASSWORD, edtPassword.getText().toString());
                        editor.putBoolean(ConstantUtils.PREF_IS_EMAIL_LOGIN, true);
                        editor.apply();

                        startActivity(new Intent(RegistrationActivity.this, RegistrationDetailsActivity.class));
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
           /*     if (spotsDialog != null && spotsDialog.isShowing()) {
                    spotsDialog.cancel();
                }*/
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
//            if (editText.hashCode() == edtPassword.hashCode()) {
//                txtInputRePassword.setError(null);
//                txtInputRePassword.setErrorEnabled(false);
//            }

            ((TextInputLayout) editText.getParentForAccessibility()).setError(null);
            ((TextInputLayout) editText.getParentForAccessibility()).setErrorEnabled(false);
        }

        switch (view.getId()) {
            case R.id.edt_email:
            /*    if (AndroidUtils.isValidEmailAddress(text)) {
                    imgValidEmail.setVisibility(View.VISIBLE);
                } else {
                    imgValidEmail.setVisibility(View.GONE);
                }
*/
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
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetUserApi")) {
            getUserDataApiCall();
        }
    }
}
