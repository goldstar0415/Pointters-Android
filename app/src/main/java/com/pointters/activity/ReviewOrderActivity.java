package com.pointters.activity;

import android.animation.RectEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.pointters.model.ReviewOrderModel;
import com.pointters.model.request.CancelOrderRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ReviewOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private DiscreteSeekBar seekRating, seekQuality;
    private ImageButton btnWillingAgain;
    private Button btnSubmit;
    private EditText edtComment;

    private String orderId = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;

    private ReviewOrderModel reviewOrderModel;

    private Integer overallRating = 0;
    private Integer qualityOfService = 0;
    private boolean isWillingAgain = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);
        AppUtils.setToolBarWithBothIcon(ReviewOrderActivity.this, getResources().getString(R.string.review_order), R.drawable.back_icon, 0);

        loader = KProgressHUD.create(ReviewOrderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        if (intent.getStringExtra("orderId") != null) {
            orderId = intent.getStringExtra("orderId");
        }

        seekQuality = (DiscreteSeekBar) findViewById(R.id.seeker_quality);
        seekRating = (DiscreteSeekBar) findViewById(R.id.seeker_rating);
        btnWillingAgain = (ImageButton) findViewById(R.id.btn_willing_again);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        edtComment = (EditText) findViewById(R.id.edt_comment);
        edtComment.addTextChangedListener(new TextWatcher() {
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
        btnSubmit.setOnClickListener(this);
        btnWillingAgain.setOnClickListener(this);
        btnWillingAgain.setSelected(false);

        seekRating.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                overallRating = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        seekQuality.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                qualityOfService = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        AllowSubmitButton();
        reviewOrderModel = new ReviewOrderModel();
        reviewOrderModel.setOrderId(orderId);
        reviewOrderModel.setOverallRating(overallRating);
        reviewOrderModel.setQualityOfService(qualityOfService);
        reviewOrderModel.setWillingToBuyServiceAgain(isWillingAgain ? 1: 0);

    }

    public void AllowSubmitButton(){
        if (edtComment.getText().toString().length() == 0 || edtComment.getText().toString().equals(" ")) {
            btnSubmit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_black_info));
            btnSubmit.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
            btnSubmit.setEnabled(false);
        }else{
            btnSubmit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            btnSubmit.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            btnSubmit.setEnabled(true);
        }
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
            case R.id.btn_submit:
                submitOrderReview();
            case R.id.btn_willing_again:
                btnWillingAgain.setSelected(btnWillingAgain.isSelected());
                isWillingAgain = btnWillingAgain.isSelected();
                break;
        }
    }

    public void submitOrderReview(){
        reviewOrderModel.setOrderId(orderId);
        reviewOrderModel.setOverallRating(overallRating);
        reviewOrderModel.setQualityOfService(qualityOfService);
        reviewOrderModel.setWillingToBuyServiceAgain(isWillingAgain ? 1: 0);
        reviewOrderModel.setComment(edtComment.getText().toString());

        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callGetCategoryApi = apiService.postBuyerReviewOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), orderId, reviewOrderModel);
        callGetCategoryApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (loader.isShowing()){
                    loader.dismiss();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    AndroidUtils.hideKeyBoard(ReviewOrderActivity.this);

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
