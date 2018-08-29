package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.ServiceAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.SearchModel;
import com.pointters.model.ServiceModel;
import com.pointters.model.response.SearchSearviceResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {
    private TextView edtSearchHere;
    private ArrayList<ServiceModel> serviceArrayList=new ArrayList<>();
    private RecyclerView recyclerViewServices;
    private RelativeLayout tabGetLiveOffer, tabFilters, tabMapView;
    private View line1, line2, line3;

    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String query = "";
    String location = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        edtSearchHere = (TextView) findViewById(R.id.edt_search_here);
        query = getIntent().getStringExtra("query");
        location = getIntent().getStringExtra("location");
        edtSearchHere.setText(String.format("%s, %s", query, location));

        setUpRecyclerView();

        setOnClickListener();

    }

    private void setOnClickListener() {

        tabGetLiveOffer = (RelativeLayout) findViewById(R.id.tab_get_live_offer);
        tabFilters = (RelativeLayout) findViewById(R.id.tab_filters);
        tabMapView = (RelativeLayout) findViewById(R.id.tab_map_view);
        line1 = (View) findViewById(R.id.line_get_live_offer);
        line2 = (View) findViewById(R.id.line_filters);
        line3 = (View) findViewById(R.id.line_map_view);

        findViewById(R.id.btn_back).setOnClickListener(this);
        tabGetLiveOffer.setOnClickListener(this);
        tabFilters.setOnClickListener(this);
        tabMapView.setOnClickListener(this);

    }

    private void setUpRecyclerView() {

        recyclerViewServices = (RecyclerView) findViewById(R.id.recycler_services_result);
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
            case R.id.tab_get_live_offer:
                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.GONE);
                line3.setVisibility(View.GONE);
                break;
            case R.id.tab_filters:
                line1.setVisibility(View.GONE);
                line2.setVisibility(View.VISIBLE);
                line3.setVisibility(View.GONE);
                break;
            case R.id.tab_map_view:
                line1.setVisibility(View.GONE);
                line2.setVisibility(View.GONE);
                line3.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void CallSearchApi() {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        SearchModel searchRequest = new SearchModel(query);
        Call<ArrayList<SearchSearviceResponse>> callSearchPageApi = apiService.postSearch(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), searchRequest);
        callSearchPageApi.enqueue(new Callback<ArrayList<SearchSearviceResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchSearviceResponse>> call, Response<ArrayList<SearchSearviceResponse>> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    ArrayList<SearchSearviceResponse> models = response.body();
//                    if (models.size() > 0) {
//                        popularCategories.addAll(models.getPopularCategories());
//                    }
//                    if (models.getRecentSearches().size() > 0 ) {
//                        recentSearches.addAll(models.getRecentSearches());
//                    }
//                    setSearchBarActive();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(SearchResultsActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SearchResultsActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ArrayList<SearchSearviceResponse>> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(SearchResultsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onApiFail(String apiSource) {

    }
}
