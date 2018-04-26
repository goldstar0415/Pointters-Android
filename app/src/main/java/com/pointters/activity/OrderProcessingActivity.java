package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class OrderProcessingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout orderAccept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_progress);
        AppUtils.setToolBarWithBothIcon(OrderProcessingActivity.this, getResources().getString(R.string.order_progress), R.drawable.back_icon, 0);

        orderAccept = (LinearLayout) findViewById(R.id.view_order_accept);
        orderAccept.setOnClickListener(this);
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

        }
    }
}
