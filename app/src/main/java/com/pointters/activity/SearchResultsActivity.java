package com.pointters.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pointters.R;
import com.pointters.adapter.ServiceAdapter;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        setUpRecyclerView();

        setOnClickListener();

    }

    private void setOnClickListener() {

        findViewById(R.id.btn_back).setOnClickListener(this);

    }

    private void setUpRecyclerView() {

        RecyclerView recyclerViewServices = (RecyclerView) findViewById(R.id.recycler_services_result);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ServiceAdapter serviceAdapter = new ServiceAdapter();
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
}
