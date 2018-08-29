package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.Prices;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CurrencyEditText;
import com.pointters.utils.MyTextWatcher;
import com.pointters.utils.wheelpicker.listener.OnCityWheelComfirmListener;
import com.pointters.utils.wheelpicker.ppw.CityWheelPickerPopupWindow;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class AddPriceOptionActivity extends AppCompatActivity implements View.OnClickListener, OnEditTextChangeListener {

    private Button addPriceButton, btnDeliveryTime;
    private String currencyString;
    private CurrencyEditText priceAmountEditText;
    private EditText editTextPriceOptionDescription;
    private Button deleteButton;
    private Prices prices;
    private String timeUnitOfMeasure = "hour";
    private String currencyCode = "USD";
    private String currencySymbol = "$";
    private String deliverytime = "1";
    private String deliveryUnit = "hour";
    private TextView txtRemainCharacters;
    private final int MAX_CHARACTERS = 200;

    CityWheelPickerPopupWindow wheelPickerPopupWindow;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price_option);

        initViews();

        setOnClick();
        setEditTextListener();

        if (getIntent() != null && getIntent().getSerializableExtra(ConstantUtils.PRICE) != null) {
            prices = (Prices) getIntent().getSerializableExtra(ConstantUtils.PRICE);
            if (prices.getPrice() != null && prices.getPrice() > 0) {
                priceAmountEditText.setText(String.valueOf(prices.getPrice()));
            }
            if (prices.getDescription() != null && !prices.getDescription().isEmpty()) {
                editTextPriceOptionDescription.setText(prices.getDescription());
            }
            if (prices.getTime() != null && prices.getTime() > 0) {
                btnDeliveryTime.setText(String.valueOf(prices.getTime()));
                if (prices.getTimeUnitOfMeasure() != null && !prices.getTimeUnitOfMeasure().isEmpty()) {
                    if (prices.getTime() > 1) {
                        btnDeliveryTime.setText(String.format("%d %ss", prices.getTime(), prices.getTimeUnitOfMeasure()));
                    }else{
                        btnDeliveryTime.setText(String.format("%d %s", prices.getTime(), prices.getTimeUnitOfMeasure()));
                    }
                }
            }
            addPriceButton.setText(getResources().getString(R.string.save).toUpperCase());

            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.edit_price_option_header), R.drawable.back_icon, R.drawable.delete_icon);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.add_price_option_header), R.drawable.back_icon, 0);
            deleteButton.setVisibility(View.GONE);
        }
        setAddButtonState();
    }

    private void setEditTextListener() {
        //Custom Edit text change listener with returning id of edit text
        priceAmountEditText.addTextChangedListener(new MyTextWatcher(priceAmountEditText, this));
        editTextPriceOptionDescription.addTextChangedListener(new MyTextWatcher(editTextPriceOptionDescription, this));
    }

    private void setOnClick() {
        addPriceButton.setOnClickListener(this);
        btnDeliveryTime.setOnClickListener(this);
    }

    private void initViews() {
        wheelPickerPopupWindow = new CityWheelPickerPopupWindow(AddPriceOptionActivity.this);
        wheelPickerPopupWindow.setListener(new OnCityWheelComfirmListener() {
            @Override
            public void onSelected(String time, String unit) {
//                Toast.makeText(AddPriceOptionActivity.this, time+unit,Toast.LENGTH_LONG).show();
                if (time == null || time.equals("")){
                    deliverytime = "1";
                }else{
                    deliverytime = time;
                }
                timeUnitOfMeasure = unit;
                String strunit = timeUnitOfMeasure;
                timeUnitOfMeasure = timeUnitOfMeasure.replace("(s)", "");
                if (Integer.valueOf(deliverytime) > 1){
                    strunit = strunit.replace("(s)", "s");
                }else{
                    strunit = strunit.replace("(s)", "");
                }
                btnDeliveryTime.setText(deliverytime + " " + strunit);
                setAddButtonState();
            }

        });
        deleteButton = (Button) findViewById(R.id.delete_price);
        deleteButton.setOnClickListener(this);
        txtRemainCharacters = (TextView) findViewById(R.id.txt_remain_character);
        editTextPriceOptionDescription = (EditText) findViewById(R.id.edit_price_option_desc);
        addPriceButton = (Button) findViewById(R.id.btn_add);
        priceAmountEditText = (CurrencyEditText) findViewById(R.id.edt_price_amount);
        btnDeliveryTime = (Button) findViewById(R.id.btn_deivery_time);
        if (Integer.valueOf(deliverytime) > 1) {
            btnDeliveryTime.setText(String.format("%s %ss", deliverytime, deliveryUnit));
        }else{
            btnDeliveryTime.setText(String.format("%s %s", deliverytime, deliveryUnit));
        }
        priceAmountEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        },200);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                hide.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                onBackPressed();
                break;

            case R.id.btn_add:
                AndroidUtils.hideKeyBoard(AddPriceOptionActivity.this);
                String description = editTextPriceOptionDescription.getText().toString();
                Integer price = Math.toIntExact(priceAmountEditText.getRawValue());
                Integer time = Integer.parseInt(deliverytime);
                prices = new Prices(description, Float.valueOf(price), time, timeUnitOfMeasure, currencySymbol, currencyCode);
                Intent intent = new Intent();
                intent.putExtra(ConstantUtils.DELETE, "no");
                intent.putExtra(ConstantUtils.POSITION,getIntent().getStringExtra(ConstantUtils.POSITION));
                intent.putExtra(ConstantUtils.PRICE, prices);
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.toolbar_right_img:
                Intent intents = new Intent();
                intents.putExtra(ConstantUtils.POSITION,getIntent().getStringExtra(ConstantUtils.POSITION));
                intents.putExtra(ConstantUtils.DELETE, "yes");
                setResult(RESULT_OK, intents);
                finish();
                break;
            case R.id.delete_price:
                Intent intents1 = new Intent();
                intents1.putExtra(ConstantUtils.POSITION,getIntent().getStringExtra(ConstantUtils.POSITION));
                intents1.putExtra(ConstantUtils.DELETE, "yes");
                setResult(RESULT_OK, intents1);
                finish();
                break;
            case R.id.btn_deivery_time:
                showWindow();
                break;
        }
    }
    private void showWindow() {
        wheelPickerPopupWindow.show();
    }


    public static int dp2Px(float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onTextChange(String text, View view) {
        long strPrice = priceAmountEditText.getRawValue();
        String strDays = deliverytime != null ? "1": deliverytime;
        String strDesc = editTextPriceOptionDescription.getText().toString().trim();
        int remainCount = MAX_CHARACTERS - strDesc.length();
        txtRemainCharacters.setText(remainCount + "/" + MAX_CHARACTERS);
        setAddButtonState();
    }

    public void setAddButtonState(){
        boolean b = true;
        if (priceAmountEditText.getRawValue() == 0 ){
            b = false;
        }
        if (timeUnitOfMeasure.equals("")){
            b = false;
        }
        if (editTextPriceOptionDescription.getText().toString().equals("")){
            b = false;
        }
        addPriceButton.setEnabled(b);
        if (b){
            addPriceButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
            addPriceButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        }else{
            addPriceButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_black_info));
            addPriceButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
        }
    }
}
