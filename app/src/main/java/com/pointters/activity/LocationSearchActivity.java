package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.braintreepayments.cardform.view.CardForm;
import com.pointters.R;
import com.pointters.adapter.PlacesAutoCompleteAdapter;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.rest.PlaceAPI;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LocationSearchActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView backButton, closeButton;
    private RecyclerView searchRecyclerView;
    private EditText searchField;
    PlacesAutoCompleteAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        searchField = (EditText) findViewById(R.id.edt_search_here);
        searchRecyclerView = (RecyclerView) findViewById(R.id.recycler_locations);
        backButton = (ImageView) findViewById(R.id.btn_back);
        closeButton = (ImageView) findViewById(R.id.img_cross_search_here);

        backButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new PlacesAutoCompleteAdapter(this, R.layout.adapter_search_item, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                if (v.getId() == R.id.txt_search_loation) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        searchRecyclerView.setAdapter(adapter);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.img_cross_search_here:

                break;

        }
    }
}

