package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pointters.R;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddCreditCardActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        AppUtils.setToolBarWithBothIcon(AddCreditCardActivity.this, getResources().getString(R.string.add_crdt_card), R.drawable.back_icon_grey, 0);
        Log.d("onCreate", "onCreate: ");

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;


        }
    }
}
