package com.pointters.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.pointters.R;
import com.pointters.adapter.ServiceAdapter;
import com.pointters.model.ServiceModel;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtSearchHere;
    private ArrayList<ServiceModel> serviceArrayList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);
        edtSearchHere = (EditText) findViewById(R.id.edt_search_here);
        edtSearchHere.setText(getIntent().getStringExtra("searchedValue"));

        setUpRecyclerView();

        setOnClickListener();

    }

    private void setOnClickListener() {

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_get_live_offers).setOnClickListener(this);

    }

    private void setUpRecyclerView() {

        RecyclerView recyclerViewServices = (RecyclerView) findViewById(R.id.recycler_services_result);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ServiceAdapter serviceAdapter = new ServiceAdapter(SearchResultsActivity.this,serviceArrayList);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        recyclerViewServices.setAdapter(serviceAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_back:
                onBackPressed();
                break;

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
