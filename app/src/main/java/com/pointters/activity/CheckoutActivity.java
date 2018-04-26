package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView payButton, txtCountService, txtCountAddOn;
    private ImageView imgAddService, imgAddon, imgMinusService, imgMinusAddOn;
    private RelativeLayout changePaymentMethodRelativeLayout, changeShippingAddressLayout;
    private int serviceCount = 1, addOnCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        txtCountAddOn = (TextView) findViewById(R.id.txt_count_addon);
        txtCountService = (TextView) findViewById(R.id.txt_count_service);

        imgAddService = (ImageView) findViewById(R.id.img_add_service);
        imgAddon = (ImageView) findViewById(R.id.img_add_addon);
        imgMinusService = (ImageView) findViewById(R.id.img_minus_service);
        imgMinusAddOn = (ImageView) findViewById(R.id.img_minus_addon);

        imgAddService.setOnClickListener(this);
        imgAddon.setOnClickListener(this);
        imgMinusAddOn.setOnClickListener(this);
        imgMinusService.setOnClickListener(this);

        payButton = (TextView) findViewById(R.id.btn_pay);
        changePaymentMethodRelativeLayout = (RelativeLayout) findViewById(R.id.change_payment);
        changeShippingAddressLayout = (RelativeLayout) findViewById(R.id.change_shipping_address);
        changePaymentMethodRelativeLayout.setOnClickListener(this);
        changeShippingAddressLayout.setOnClickListener(this);
        payButton.setOnClickListener(this);
        AppUtils.setToolBarWithBothIcon(CheckoutActivity.this, getResources().getString(R.string.checkout), R.drawable.back_icon, 0);

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
            case R.id.btn_pay:
                startActivity(new Intent(CheckoutActivity.this, FulfillmentActivity.class));
                break;
            case R.id.change_payment:
                startActivity(new Intent(CheckoutActivity.this, PaymentMethodsActivity.class));
                break;
            case R.id.change_shipping_address:
                startActivity(new Intent(CheckoutActivity.this, ShippingAddressesActivity.class));
                break;
            case R.id.img_add_addon:
                if(addOnCount!=0) {

                        addOnCount++;
                        txtCountAddOn.setText(String.valueOf(addOnCount));

                }
                break;
            case R.id.img_minus_addon:
                if(addOnCount!=1) {
                        addOnCount--;
                        txtCountAddOn.setText(String.valueOf(addOnCount));

                }
                break;
            case R.id.img_add_service:
                    serviceCount++;
                    txtCountService.setText(String.valueOf(serviceCount));
                break;
            case R.id.img_minus_service:
                if(serviceCount!=1) {
                    serviceCount--;
                    txtCountService.setText(String.valueOf(serviceCount));
                }
                break;


        }
    }
}
