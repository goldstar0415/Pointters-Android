package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.model.OrdersDetailModel;
import com.pointters.model.request.CancelOrderRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CancelOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llPoorQuality, llNotCompleted, llOther;
    private ImageButton btnPoorQuality, btnNotCompleted, btnOther;
    private Button btnSubmit;
    private EditText edtMessage;
    private String selectedReason = "not_on_time";
    private String orderId = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        loader = KProgressHUD.create(CancelOrderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        AppUtils.setToolBarWithBothIcon(CancelOrderActivity.this, getResources().getString(R.string.cancel_order), R.drawable.back_icon, 0);
        Intent intent = getIntent();
        if (intent.getStringExtra("orderId") != null) {
            orderId = intent.getStringExtra("orderId");
        }

        llNotCompleted = (LinearLayout) findViewById(R.id.ll_not_completed);
        llPoorQuality = (LinearLayout) findViewById(R.id.ll_poor_quality);
        llOther = (LinearLayout) findViewById(R.id.ll_other);

        btnNotCompleted = (ImageButton) findViewById(R.id.btn_not_completed);
        btnPoorQuality = (ImageButton) findViewById(R.id.btn_poor_quality);
        btnOther = (ImageButton) findViewById(R.id.btn_other);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        llNotCompleted.setOnClickListener(this);
        llPoorQuality.setOnClickListener(this);
        llOther.setOnClickListener(this);
        btnNotCompleted.setOnClickListener(this);
        btnPoorQuality.setOnClickListener(this);
        btnOther.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        edtMessage = (EditText) findViewById(R.id.edt_message_reason);
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                AllowSubmitButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AllowSubmitButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                AllowSubmitButton();
            }
        });
        setCheckButton(0);
        AllowSubmitButton();
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
            case R.id.view_order_accept:
                break;

            case R.id.ll_not_completed:
                setCheckButton(0);
                break;
            case R.id.ll_poor_quality:
                setCheckButton(1);
                break;
            case R.id.ll_other:
                setCheckButton(2);
                break;
            case R.id.btn_not_completed:
                setCheckButton(0);
                break;
            case R.id.btn_poor_quality:
                setCheckButton(1);
                break;
            case R.id.btn_other:
                setCheckButton(2);
                break;
            case R.id.btn_submit:
                postCancelOrder();
                break;
        }
    }

    public void setCheckButton(int index){
        btnNotCompleted.setSelected(false);
        btnPoorQuality.setSelected(false);
        btnOther.setSelected(false);
        switch (index) {
            case 0:
                btnNotCompleted.setSelected(true);
                selectedReason = "not_on_time";
                break;
            case 1:
                btnPoorQuality.setSelected(true);
                selectedReason = "poor_quality_of_service";
                break;
            case 2:
                btnOther.setSelected(true);
                selectedReason = "other";
                break;
        }
    }

    public void AllowSubmitButton(){
        if (edtMessage.getText().toString().length() == 0 || edtMessage.getText().toString().equals(" ")) {
            btnSubmit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_black_info));
            btnSubmit.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
            btnSubmit.setEnabled(false);
        }else{
            btnSubmit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            btnSubmit.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            btnSubmit.setEnabled(true);
        }
    }

    public void postCancelOrder(){

        loader.show();
        CancelOrderRequest request = new CancelOrderRequest(selectedReason, edtMessage.getText().toString());

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.putBuyerRequestCancelOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), orderId, request);
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    AndroidUtils.hideKeyBoard(CancelOrderActivity.this);

                }
                if (response.code() == 200 && response.body() != null) {
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (loader.isShowing()){
                    loader.dismiss();
                }
            }
        });

    }
}
