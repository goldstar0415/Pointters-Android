package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.response.GetSellerEligibilityResponse;
import com.pointters.model.response.GetSendServicesResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BecomeASellerActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    LinearLayout btnAddService;
    LinearLayout btnPaymentSetup;
    LinearLayout btnBackgroundCheck;
    LinearLayout btnBusinessVerification;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String paymentSetupStatus;
    private Integer numServices;
    private String backgroundCheckStatus;

    private TextView txtPaymentSetupStatus, txtNumServices, txtBackgroundCheckStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_aseller);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AppUtils.setToolBarWithBothIcon(BecomeASellerActivity.this, getResources().getString(R.string.become_a_seller),
                R.drawable.back_icon, 0);
        init();
    }


 //=============================Ui================================================================
    public void init(){
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        btnAddService = (LinearLayout) findViewById(R.id.btn_addService);
        btnPaymentSetup = (LinearLayout) findViewById(R.id.btn_paymentSetup);
        btnBackgroundCheck = (LinearLayout) findViewById(R.id.btn_backgroundCheck);
        btnBusinessVerification = (LinearLayout) findViewById(R.id.btn_businessVerification);

        btnAddService.setOnClickListener(this);
        btnPaymentSetup.setOnClickListener(this);
        btnBackgroundCheck.setOnClickListener(this);
        btnBusinessVerification.setOnClickListener(this);

        txtBackgroundCheckStatus = (TextView) findViewById(R.id.txt_status_backgroud_check);
        txtNumServices = (TextView) findViewById(R.id.txt_num_services);
        txtPaymentSetupStatus = (TextView) findViewById(R.id.txt_status_payment_setup);
        loader.show();
        getSellerEligibility();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;

            case R.id.btn_addService:
                editor.putString(ConstantUtils.LAST_SELECTED_TAB, "1");
                editor.commit();
                editor.apply();
                startActivity(new Intent(BecomeASellerActivity.this, AddServiceActivity.class));
                break;

            case R.id.btn_paymentSetup:
                startActivity(new Intent(BecomeASellerActivity.this, PaymentMethodsActivity.class));
                break;

            case R.id.btn_backgroundCheck:
                startActivity(new Intent(BecomeASellerActivity.this, BackgroundCheckActivity.class));
                break;

            case R.id.btn_businessVerification:

                break;

        }
    }

    private void getSellerEligibility() {

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSellerEligibilityResponse> getSellerEligibilityResponseCall = apiService.getUserSellerEleigibility(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getSellerEligibilityResponseCall.enqueue(new Callback<GetSellerEligibilityResponse>() {
            @Override
            public void onResponse(Call<GetSellerEligibilityResponse> call, Response<GetSellerEligibilityResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if(response.code() == 200 && response.body() != null) {
                    paymentSetupStatus = response.body().getPaymentSetupStatus();
                    numServices = response.body().getNumServices();
                    backgroundCheckStatus = response.body().getBackgroundCheckStatus();
                    txtBackgroundCheckStatus.setText("Status: "+paymentSetupStatus);
                    txtNumServices.setText(String.valueOf("Status: "+numServices+" Added"));
                    txtBackgroundCheckStatus.setText("Status: "+backgroundCheckStatus);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(BecomeASellerActivity.this, "callGetSendServicesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(BecomeASellerActivity.this);
                }
                else if (response.code() == 404) {
                    txtBackgroundCheckStatus.setText("NA");
                    txtNumServices.setText(String.valueOf("NA"));
                    txtBackgroundCheckStatus.setText("NA");
                }
            }

            @Override
            public void onFailure(Call<GetSellerEligibilityResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(BecomeASellerActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onApiFail(String apiSource) {

    }
}
