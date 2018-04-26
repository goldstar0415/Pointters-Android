package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
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

public class TermsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = TermsActivity.class.getSimpleName();
    TextView header, subtext1, subtext2, detail1, detail2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initViews();

        Intent intent = getIntent();
        if (intent != null) {
            if(intent.getStringExtra("type").equals("terms")) {
                //set toolbar
                AppUtils.setToolBarWithBothIconWithShadow(TermsActivity.this, getResources().getString(R.string.terms_and_condition),
                        R.drawable.back_icon, 0);
                header.setText(getResources().getString(R.string.terms_header));
                subtext1.setText(getResources().getString(R.string.terms_sub_1));
                subtext2.setText(getResources().getString(R.string.terms_sub_2));
                detail1.setText(getResources().getString(R.string.terms_detail_1));
                detail2.setText(getResources().getString(R.string.terms_detail_2));
            }else{
                //set toolbar
                AppUtils.setToolBarWithBothIconWithShadow(TermsActivity.this, getResources().getString(R.string.privacy_policy),
                        R.drawable.back_icon, 0);
                header.setText(getResources().getString(R.string.privacy_header));
                subtext1.setText(getResources().getString(R.string.privacy_sub_1));
                subtext2.setText(getResources().getString(R.string.privacy_sub_2));
                detail1.setText(getResources().getString(R.string.privacy_detail_1));
                detail2.setText(getResources().getString(R.string.privacy_detail_2));

            }
        }
    }

    private void initViews() {
         header = (TextView) findViewById(R.id.header_text);
         subtext1 = (TextView) findViewById(R.id.txt_title_1);
         subtext2 = (TextView) findViewById(R.id.txt_title_2);
         detail1 = (TextView) findViewById(R.id.txt_detail_1);
         detail2 = (TextView) findViewById(R.id.txt_detail_2);
    }

    private void setOnClick() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
