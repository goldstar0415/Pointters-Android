package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, OnEditTextChangeListener {

    private Toolbar toolbar;
    private Button btnLoginEmail;
    private Button btnLoginFb;
    private EditText edtEmail;
    private EditText edtPassword;
    private ImageView imgValidEmail;
    private TextView txtWarning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        setOnClick();

        //calligraphy library not applying fonts to text input layout hence done programmatically
        AppUtils.applyFontsToTextInputLayout(this, new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.text_input_email),
                (TextInputLayout) findViewById(R.id.text_input_password)});

        setEditTextListener();


    }

    private void setEditTextListener() {

        //listener for action done button click
        edtPassword.setOnEditorActionListener(this);

        //Custom Edit text change listener with returning id of edit text
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail, this));
        edtPassword.addTextChangedListener(new MyTextWatcher(edtPassword, this));
    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnLoginEmail = (Button) findViewById(R.id.btn_email);
        btnLoginFb = (Button) findViewById(R.id.btn_fb);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        imgValidEmail = (ImageView) findViewById(R.id.img_valid_email);
        txtWarning = (TextView) findViewById(R.id.txt_warning);

    }


    private void setOnClick() {

        btnLoginEmail.setOnClickListener(this);
        btnLoginFb.setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        findViewById(R.id.txt_sign_up).setOnClickListener(this);

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

                performLoginEmail();

                getSharedPreferences(ConstantUtils.APP_PREF, MODE_PRIVATE).edit()
                        .putBoolean(ConstantUtils.PREF_IS_LOGIN, true).apply();
                startActivity(new Intent(this, HomeActivity.class));

                break;

            case R.id.btn_fb:
                //startActivity(new Intent(this, RegistrationDetailsActivity.class));
               /* btnSignUpFb.setIndeterminateProgressMode(true); // turn on indeterminate progress
                btnSignUpFb.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress*/
                break;

            case R.id.txt_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;

            case R.id.txt_sign_up:

                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
                break;
        }

    }

    private void performLoginEmail() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (email.isEmpty() || !AndroidUtils.isValidEmailAddress(email)) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText(getResources().getString(R.string.provide_valid_email));
        } else if (password.isEmpty()) {
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText(getResources().getString(R.string.provide_valid_password));
        } else {

        }
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
}
