package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.MyTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 18-Jul-17.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, OnEditTextChangeListener {

    private Toolbar toolbar;
    private Button btnSignUpEmail;
    private Button btnSignUpFb;
    private TextView txtTermsConditions;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtReEnterPassword;
    private ImageView imgValidEmail;
    private TextView txtWarning;
    private CallbackManager callbackManager;
    private String userFirstName;
    private String userLastName;
    private String userFbId;
    private String userFbEmail;
    private String profilePicURL;
    private String currentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        setOnClick();

        makeSpannableText();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.text_input_email)
                , (TextInputLayout) findViewById(R.id.text_input_password)
                , (TextInputLayout) findViewById(R.id.text_input_re_enter_password)});

        setEditTextListener();

        registerFbCallBack();

    }

    private void registerFbCallBack() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getRequiredFbData();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

    }

    private void getRequiredFbData() {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            if (object.has("id")) {
                                userFbId = object.getString("id");
                            } else {
                                userFbId = "";
                            }

                            if (object.has("email")) {
                                userFbEmail = object.getString("email");

                            } else {
                                userFbEmail = "";

                            }

                            startActivity(new Intent(RegistrationActivity.this, RegistrationDetailsActivity.class));


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email");
        request.setParameters(parameters);
        request.executeAsync();

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
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnSignUpEmail = (Button) findViewById(R.id.btn_email);
        btnSignUpFb = (Button) findViewById(R.id.btn_fb);
        txtTermsConditions = (TextView) findViewById(R.id.txt_agree_to_terms_conditions);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtReEnterPassword = (EditText) findViewById(R.id.edt_re_enter_password);
        imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        txtWarning = (TextView) findViewById(R.id.txt_warning);

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

                Toast.makeText(RegistrationActivity.this, "term's & Conditions", Toast.LENGTH_SHORT).show();

            }
        };

        ClickableSpan spanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                Toast.makeText(RegistrationActivity.this, "Privacy Conditions", Toast.LENGTH_SHORT).show();

            }
        };

        spannableTermsConditions.setSpan(spanTerms, 9, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTermsConditions.setSpan(spanPrivacy, 33, spannableTermsConditions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtTermsConditions.setText(spannableTermsConditions);
        txtTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_email:

                performSignUpEmail();
                // btnSignUpFb.setProgress(100); // set progress to 100 or -1 to indicate complete or error state
                break;

            case R.id.btn_fb:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_work_history", "user_location"));

               /*btnSignUpFb.setIndeterminateProgressMode(true); // turn on indeterminate progress
                btnSignUpFb.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress*/

                break;
            case R.id.txt_sign_in:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
                break;
        }

    }

    private void performSignUpEmail() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (email.isEmpty() || !AndroidUtils.isValidEmailAddress(email)) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText(getResources().getString(R.string.provide_valid_email));
        } else if (password.isEmpty()) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText(getResources().getString(R.string.provide_valid_password));
        } else if (!password.equals(edtReEnterPassword.getText().toString())) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText(getResources().getString(R.string.password_mismatch));
        } else {
            txtWarning.setVisibility(View.GONE);
            startActivity(new Intent(this, RegistrationDetailsActivity.class));
        }
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

        if (txtWarning.getVisibility() == View.VISIBLE)
            txtWarning.setVisibility(View.GONE);

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
