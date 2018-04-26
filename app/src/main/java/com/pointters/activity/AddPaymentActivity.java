package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.cardform.view.CardForm;
import com.pointters.R;
import com.pointters.model.CardModel;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddPaymentActivity extends AppCompatActivity implements View.OnClickListener{


    private Button setDefaultButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_methods);
        AppUtils.setToolBarWithBothIcon(AddPaymentActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon, R.drawable.icon_edit_gray);

        setDefaultButton= (Button) findViewById(R.id.btn_set_default);
        setDefaultButton.setOnClickListener(this);

        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this);

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

                break;

            case R.id.btn_add_card:
                break;

        }
    }
}