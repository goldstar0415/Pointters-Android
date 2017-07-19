package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 19-Jul-17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CircularProgressButton btnLogin;
    private CircularProgressButton btnFb;
    private TextView txtForgotPassword;
    private TextView txtDontHaveAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        enableOnClick();

        makeSpannableText();


    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnLogin = (CircularProgressButton) findViewById(R.id.btn_email);
        btnFb = (CircularProgressButton) findViewById(R.id.btn_fb);
        txtForgotPassword = (TextView) findViewById(R.id.txt_forgot_password);
        txtDontHaveAccount = (TextView) findViewById(R.id.txt_dont_have_account);
    }


    private void enableOnClick() {

        findViewById(R.id.btn_email).setOnClickListener(this);
        findViewById(R.id.btn_fb).setOnClickListener(this);

    }

    private void makeSpannableText() {

        //Spannable String builder for sign in
        SpannableStringBuilder spannableAlreadyAccount = new SpannableStringBuilder(getResources().getString(
                R.string.dont_have_account));

        ClickableSpan spanAlreadyAccount = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

            }
        };

        spannableAlreadyAccount.setSpan(spanAlreadyAccount, 23, spannableAlreadyAccount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtDontHaveAccount.setText(spannableAlreadyAccount);
        txtDontHaveAccount.setMovementMethod(LinkMovementMethod.getInstance());

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

                // startActivity(new Intent(this, RegistrationDetailsActivity.class));

                //btnSignUpFb.setProgress(100); // set progress to 100 or -1 to indicate complete or error state
                break;

            case R.id.btn_fb:
                //startActivity(new Intent(this, RegistrationDetailsActivity.class));
               /* btnSignUpFb.setIndeterminateProgressMode(true); // turn on indeterminate progress
                btnSignUpFb.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress*/
                break;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
