package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.pointters.R;
import com.pointters.adapter.SearchHintAdapter;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.listener.OnSearchItemClickListener;
import com.pointters.model.SearchHint;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.MyTextWatcher;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class SearchServiceActivity extends AppCompatActivity implements View.OnTouchListener, OnEditTextChangeListener, View.OnClickListener, OnSearchItemClickListener {

    private ImageView imgSearch;
    private ImageView imgLocation;
    private EditText edtSearchHere;
    private EditText edtLocation;
    private ImageView imgCrossSearch;
    private ImageView imgCrossLocation;
    private RecyclerView recyclerViewSearchHints;
    private SearchHintAdapter searchHintAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_service);

        initViews();

        setSearchBarActive();

        setTouchListener();

        setEditTextListener();

        setOnClickListener();

        recyclerViewSearchHints.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    AndroidUtils.hideKeyBoard(SearchServiceActivity.this);

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


    }

    private void setOnClickListener() {

        imgCrossSearch.setOnClickListener(this);
        imgCrossLocation.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.txt_apply).setOnClickListener(this);

    }

    private void setEditTextListener() {

        //Custom Edit text change listener with returning id of edit text
        edtSearchHere.addTextChangedListener(new MyTextWatcher(edtSearchHere, this));
        edtLocation.addTextChangedListener(new MyTextWatcher(edtLocation, this));
    }

    private void setTouchListener() {

        edtSearchHere.setOnTouchListener(this);
        edtLocation.setOnTouchListener(this);

    }

    private void setSearchBarActive() {

        imgSearch.setSelected(true);
        imgLocation.setSelected(false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchHints.setLayoutManager(linearLayoutManager);

        ArrayList<SearchHint> searchHints = new ArrayList<>();

        searchHints.add(new SearchHint("Popular Categories", new String[]{"Photographer", "Entertainer", "Wedding Planner"}));
        searchHints.add(new SearchHint("Recent Searches", new String[]{"Dog Walker", "Entertainer", "Wedding Planner"}));
        searchHints.add(new SearchHint("Keywords", new String[]{"Dog Walk", "Dog Care", "Dog Cares"}));

        SearchHintAdapter searchHintAdapter = new SearchHintAdapter(searchHints);

        searchHintAdapter.setOnSearchItemClickListener(this);
        recyclerViewSearchHints.setAdapter(searchHintAdapter);

        /*edtSearchHere.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"));
        edtLocation.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf"));*/
    }

    private void setLocationBarActive() {

        imgLocation.setSelected(true);
        imgSearch.setSelected(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchHints.setLayoutManager(linearLayoutManager);

        ArrayList<SearchHint> searchHints = new ArrayList<>();

        searchHints.add(new SearchHint("Locations", new String[]{"Chicago", "Chicago Suburb", "Chicago Downtown"}));

        SearchHintAdapter searchHintAdapter = new SearchHintAdapter(searchHints);

        searchHintAdapter.setOnSearchItemClickListener(this);
        recyclerViewSearchHints.setAdapter(searchHintAdapter);

       /* edtLocation.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"));
        edtSearchHere.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf"));*/

    }

    private void initViews() {

        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgLocation = (ImageView) findViewById(R.id.img_location_search);
        edtSearchHere = (EditText) findViewById(R.id.edt_search_here);
        edtLocation = (EditText) findViewById(R.id.edt_enter_location);
        imgCrossSearch = (ImageView) findViewById(R.id.img_cross_search_here);
        imgCrossLocation = (ImageView) findViewById(R.id.img_cross_location);
        recyclerViewSearchHints = (RecyclerView) findViewById(R.id.recycler_view_search_hints);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
            case R.id.edt_search_here:
                if (!imgSearch.isSelected())
                    setSearchBarActive();
                break;

            case R.id.edt_enter_location:
                if (!imgLocation.isSelected())
                    setLocationBarActive();
                break;
        }


        return false;
    }

    @Override
    public void onTextChange(String text, View view) {

        switch (view.getId()) {

            case R.id.edt_search_here:
                if (text.length() > 0)
                    imgCrossSearch.setVisibility(View.VISIBLE);
                else
                    imgCrossSearch.setVisibility(View.GONE);
                break;

            case R.id.edt_enter_location:
                if (text.length() > 0)
                    imgCrossLocation.setVisibility(View.VISIBLE);
                else
                    imgCrossLocation.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_cross_search_here:
                edtSearchHere.setText("");
                break;

            case R.id.img_cross_location:
                edtLocation.setText("");
                break;

            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.txt_apply:
                startActivity(new Intent(this, SearchResultsActivity.class));
                break;
        }

    }

    @Override
    public void searchItemClicked(String hint) {

        AndroidUtils.hideKeyBoard(this);

        if (imgSearch.isSelected()) {
            edtSearchHere.setText(hint);
        } else {
            edtLocation.setText(hint);
        }

    }
}
