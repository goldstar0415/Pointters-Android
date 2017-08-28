package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener {
    private Button payButton;
    private RelativeLayout changePaymentMethodRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        payButton = (Button) findViewById(R.id.btn_pay);
        changePaymentMethodRelativeLayout = (RelativeLayout) findViewById(R.id.change_payment);
        changePaymentMethodRelativeLayout.setOnClickListener(this);
        payButton.setOnClickListener(this);
        AppUtils.setToolBarWithBothIcon(CheckoutActivity.this, getResources().getString(R.string.checkout), R.drawable.back_icon_grey, 0);

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
            //    startActivity(new Intent(CheckoutActivity.this, FulfillmentActivity.class));
                break;
            case R.id.change_payment:
                //startActivity(new Intent(CheckoutActivity.this,PaymentMethodsActivity.class));
                break;

        }
    }
}
