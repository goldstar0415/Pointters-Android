package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.StoreLocationModel;
import com.pointters.model.request.StoreLocationRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class EnterShippingAddressesActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener{


    //PaymentAdapter paymentAdapter;
    private Button btnSave, btnReset;

    StoreLocationModel storeLocationModel;
    private String storeLocationId;
    TextInputLayout tiName, tilStreet, tilApt, tilState, tilCity, tilZip;
    EditText edtName, edtStreet, edtApt, edtState, edtCity, edtZip;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;

    CountryCodePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_store_location);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("storeLocationId") != null) {
            storeLocationId = intent.getStringExtra("storeLocationId");
            AppUtils.setToolBarWithBothIcon(EnterShippingAddressesActivity.this, getResources().getString(R.string.edit_shipping_address), R.drawable.back_icon, 0);
        }else{
            AppUtils.setToolBarWithBothIcon(EnterShippingAddressesActivity.this, getResources().getString(R.string.enter_shipping_address), R.drawable.back_icon, 0);
        }
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        btnSave= (Button) findViewById(R.id.btn_save);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edtName = (EditText)findViewById(R.id.edt_name);
        picker = (CountryCodePicker) findViewById(R.id.country_code_picker);
        edtStreet = (EditText)findViewById(R.id.edt_street);
        edtApt = (EditText)findViewById(R.id.edt_apt);
        edtState = (EditText)findViewById(R.id.edt_state);
        edtCity = (EditText)findViewById(R.id.edt_city);
        edtZip = (EditText)findViewById(R.id.edt_zip);

        tiName = (TextInputLayout) findViewById(R.id.text_input_name);
        tilStreet = (TextInputLayout) findViewById(R.id.text_input_street);
        tilApt = (TextInputLayout) findViewById(R.id.text_input_apt);
        tilState = (TextInputLayout) findViewById(R.id.text_input_state);
        tilCity = (TextInputLayout) findViewById(R.id.text_input_city);
        tilZip = (TextInputLayout) findViewById(R.id.text_input_zip);

        picker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
            }
        });
        if (storeLocationId != null) {
            btnReset.setText("DELETE");
            CallGetShippingAddressWithId(storeLocationId);
        }

    }

    public void CallGetShippingAddressWithId(String id){
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<StoreLocationModel> callGetCategoryApi = apiService.getShipmentAddressesDetail(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        callGetCategoryApi.enqueue(new Callback<StoreLocationModel>() {
            @Override
            public void onResponse(Call<StoreLocationModel> call, Response<StoreLocationModel> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    storeLocationModel = response.body();
                    updateUI();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EnterShippingAddressesActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EnterShippingAddressesActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<StoreLocationModel> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EnterShippingAddressesActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateUI(){
        if (storeLocationModel.getName() != null) {
            edtName.setText(storeLocationModel.getName());
        }
        if (storeLocationModel.getStreet1() != null) {
            edtStreet.setText(storeLocationModel.getStreet1());
        }
        if (storeLocationModel.getStreet2() != null) {
            edtApt.setText(storeLocationModel.getStreet2());
        }
        if (storeLocationModel.getCity() != null) {
            edtCity.setText(storeLocationModel.getCity());
        }
        if (storeLocationModel.getState() != null) {
            edtState.setText(storeLocationModel.getState());
        }
        if (storeLocationModel.getZip() != null) {
            edtZip.setText(storeLocationModel.getZip());
        }
        if (storeLocationModel.getCountry() != null) {
            picker.setCountryForNameCode(storeLocationModel.getCountry());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.toolbar_right_img:
                CallDeleteShippingAddressWithID(storeLocationId);
                break;

            case R.id.btn_save:
                if (storeLocationId != null) {
                    CallPutStoreLocation(storeLocationId);
                }else{
                    CallPostStoreLocation();
                }
                break;
            case R.id.btn_reset:
                if (storeLocationId != null) {
                    CallDeleteShippingAddressWithID(storeLocationId);
                }else{
                    resetStoreLocation();
                }
                break;

        }
    }

    public void CallDeleteShippingAddressWithID(String id){
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<BaseResponse> callDeleteApi = apiService.deleteShipmentAddresses(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id);
        callDeleteApi.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    BaseResponse object = response.body();
                    if (object.isSuccess()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EnterShippingAddressesActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EnterShippingAddressesActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EnterShippingAddressesActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void CallPutStoreLocation(String id){
        loader.show();
        StoreLocationRequest request = new StoreLocationRequest(edtName.getText().toString(), edtStreet.getText().toString(), edtApt.getText().toString(), edtCity.getText().toString(),
                edtState.getText().toString(), edtZip.getText().toString(), picker.getSelectedCountryNameCode(), "");
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<StoreLocationModel> callGetCategoryApi = apiService.putShipmentAddresses(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), id, request);
        callGetCategoryApi.enqueue(new Callback<StoreLocationModel>() {
            @Override
            public void onResponse(Call<StoreLocationModel> call, Response<StoreLocationModel> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    storeLocationModel = response.body();
//                    updateUI();
                    setResult(RESULT_OK);
                    finish();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EnterShippingAddressesActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EnterShippingAddressesActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<StoreLocationModel> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EnterShippingAddressesActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void CallPostStoreLocation(){
        loader.show();
        StoreLocationRequest request = new StoreLocationRequest(edtName.getText().toString(), edtStreet.getText().toString(), edtApt.getText().toString(), edtCity.getText().toString(),
                edtState.getText().toString(), edtZip.getText().toString(), picker.getSelectedCountryNameCode(), "");
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<StoreLocationModel> callGetCategoryApi = apiService.postShipmentAddresses(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), request);
        callGetCategoryApi.enqueue(new Callback<StoreLocationModel>() {
            @Override
            public void onResponse(Call<StoreLocationModel> call, Response<StoreLocationModel> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    storeLocationModel = response.body();
//                    updateUI();
                    setResult(RESULT_OK);
                    finish();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(EnterShippingAddressesActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(EnterShippingAddressesActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<StoreLocationModel> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(EnterShippingAddressesActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void resetStoreLocation(){
        edtZip.setText("");
        edtState.setText("");
        edtCity.setText("");
        edtApt.setText("");
        edtStreet.setText("");
        edtName.setText("");
    }

    @Override
    public void onApiFail(String apiSource) {

    }
}