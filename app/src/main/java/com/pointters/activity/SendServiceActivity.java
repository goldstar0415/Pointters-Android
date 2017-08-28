package com.pointters.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.SendServiceAdapter;
import com.pointters.utils.AppUtils;


public class SendServiceActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView sendServiceRecyclerView;
    private SendServiceAdapter sendServiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_service);
        AppUtils.setToolBarWithBothIcon(this, getResources().getString(R.string.send_service), R.drawable.back_icon_grey, 0);
        sendServiceRecyclerView=(RecyclerView)findViewById(R.id.rv_send_service);
        sendServiceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sendServiceRecyclerView.setLayoutManager(new LinearLayoutManager(SendServiceActivity.this));
        sendServiceAdapter =new SendServiceAdapter();
        sendServiceRecyclerView.setAdapter(sendServiceAdapter);

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
