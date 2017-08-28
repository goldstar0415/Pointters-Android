package com.pointters.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.LinkServiceAdapter;
import com.pointters.utils.AppUtils;


public class SendCustomOfferChatLinkServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView linkServiceRecyclerView;
    private LinkServiceAdapter linkServiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_custom_offer_chat_link_service);

        AppUtils.setToolBarWithBothIcon(this, getResources().getString(R.string.link_service), R.drawable.back_icon_grey, 0);
        linkServiceRecyclerView = (RecyclerView) findViewById(R.id.rv_link_service);
        linkServiceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        linkServiceRecyclerView.setLayoutManager(new LinearLayoutManager(SendCustomOfferChatLinkServiceActivity.this));
        linkServiceAdapter = new LinkServiceAdapter();
        linkServiceRecyclerView.setAdapter(linkServiceAdapter);

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
