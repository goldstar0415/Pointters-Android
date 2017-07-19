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
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Vishal Sharma on 18-Jul-17.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CircularProgressButton btnSignUpEmail;
    private CircularProgressButton btnSignUpFb;
    private TextView txtTermsConditions;
    private TextView txtAlreadyAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();

        AppUtils.setDefaultToolbarWithBackIcon(this, toolbar);

        enableOnClick();

        makeSpannableText();


    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        btnSignUpEmail = (CircularProgressButton) findViewById(R.id.btn_email);
        btnSignUpFb = (CircularProgressButton) findViewById(R.id.btn_fb);
        txtTermsConditions = (TextView) findViewById(R.id.txt_agree_to_terms_conditions);
        txtAlreadyAccount = (TextView) findViewById(R.id.txt_already_account);
    }


    private void enableOnClick() {

        findViewById(R.id.btn_email).setOnClickListener(this);
        findViewById(R.id.btn_fb).setOnClickListener(this);

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

        //Spannable String builder for sign in
        SpannableStringBuilder spannableAlreadyAccount = new SpannableStringBuilder(getResources().getString(
                R.string.already_have_account));

        ClickableSpan spanAlreadyAccount = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

            }
        };

        spannableAlreadyAccount.setSpan(spanAlreadyAccount, 25, spannableAlreadyAccount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtAlreadyAccount.setText(spannableAlreadyAccount);
        txtAlreadyAccount.setMovementMethod(LinkMovementMethod.getInstance());

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

                startActivity(new Intent(this, RegistrationDetailsActivity.class));

                //btnSignUpFb.setProgress(100); // set progress to 100 or -1 to indicate complete or error state
                break;

            case R.id.btn_fb:
                startActivity(new Intent(this, RegistrationDetailsActivity.class));
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
