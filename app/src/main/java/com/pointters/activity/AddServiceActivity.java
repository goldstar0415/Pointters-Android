package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.DeliveryMethodsRecyclerViewAdapter;
import com.pointters.model.DeliveryMethod;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class AddServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerViewDelivery;
    private ArrayList<DeliveryMethod> deliveryMethods;
    private DeliveryMethodsRecyclerViewAdapter deliveryMethodsRecyclerViewAdapter;
    private TextView addPriceBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_service);

        initViews();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(AddServiceActivity.this, getResources().getString(R.string.add_service),
                R.drawable.back_icon_grey, 0);

        setOnClick();


        // Delivery method
        deliveryMethods = new ArrayList<>();
        deliveryMethods.add(new DeliveryMethod("Online", false));
        deliveryMethods.add(new DeliveryMethod("Shipment", false));
        deliveryMethods.add(new DeliveryMethod("Local- anywhere in the city you service", true));
        deliveryMethods.add(new DeliveryMethod("Local- at your store locations", false));

        deliveryMethodsRecyclerViewAdapter = new DeliveryMethodsRecyclerViewAdapter(deliveryMethods);
        RecyclerView.LayoutManager mLayoutManagerDelivery = new LinearLayoutManager(getApplicationContext());
        recyclerViewDelivery.setLayoutManager(mLayoutManagerDelivery);
        recyclerViewDelivery.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDelivery.setAdapter(deliveryMethodsRecyclerViewAdapter);


    }

    private void setOnClick() {

        addPriceBtn.setOnClickListener(this);

    }

    private void initViews() {

        recyclerViewDelivery = (RecyclerView) findViewById(R.id.recycler_delivery_method);
        addPriceBtn = (TextView) findViewById(R.id.btn_add_price_button);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onClick(View view) {

        switch ((view.getId())) {


            case R.id.toolbar_lft_img:

                onBackPressed();

                break;

            case R.id.btn_add_price_button:

                startActivity(new Intent(AddServiceActivity.this, AddPriceOptionActivity.class));

                break;
        }

    }
}
