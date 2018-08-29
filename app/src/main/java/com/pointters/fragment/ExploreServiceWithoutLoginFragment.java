package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.FeaturedStyle1Adapter;
import com.pointters.adapter.FeaturedStyle2Adapter;
import com.pointters.adapter.FeaturedStyle3Adapter;
import com.pointters.adapter.FeaturedStyle4Adapter;
import com.pointters.adapter.FeaturedStyle5Adapter;
import com.pointters.adapter.PopularCategoriesAdapter;
import com.pointters.adapter.SuggestedCategoriesAdapter;
import com.pointters.model.ServiceModel;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class ExploreServiceWithoutLoginFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ArrayList<ServiceModel> serviceArrayList=new ArrayList<>();

    RecyclerView recyclerCategoriesView;
    RecyclerView recyclerFeaturedStyleView1;
    RecyclerView recyclerFeaturedStyleView2;
    RecyclerView recyclerFeaturedStyleView3;
    RecyclerView recyclerFeaturedStyleView4;
    RecyclerView recyclerFeaturedStyleView5;
    private KProgressHUD loader;
    private LinkedTreeMap homepageData;
    ViewPager viewPagerSuggestedServices;

    ArrayList<LinkedTreeMap> bannerArray;
    LinkedTreeMap section2Array;
    LinkedTreeMap section3Array;
    LinkedTreeMap section4Array;
    LinkedTreeMap section5Array;
    LinkedTreeMap section6Array;
    LinkedTreeMap section7Array;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_explore_service_without_login, container, false);
        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        CallGetHomepageData();

        return view;
    }

    public void CallGetHomepageData(){
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<LinkedTreeMap> callGetCategoryApi = apiService.getHomepage();
        callGetCategoryApi.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    homepageData = response.body();
                    updateUI();

                }
                else if (response.code() == 401) {
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateUI(){
        bannerArray = (ArrayList<LinkedTreeMap>) homepageData.get("banner");
        section2Array = (LinkedTreeMap) homepageData.get("section2");
        section3Array = (LinkedTreeMap) homepageData.get("section3");
        section4Array = (LinkedTreeMap) homepageData.get("section4");
        section5Array = (LinkedTreeMap) homepageData.get("section5");
        section6Array = (LinkedTreeMap) homepageData.get("section6");
        section7Array = (LinkedTreeMap) homepageData.get("section7");
        setUpSuggestedCategoryViewPager();
        setUpPopularCategory();

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        TextView section3Title = (TextView) view.findViewById(R.id.txt_section3_title);
        section3Title.setText(section3Array.get("title").toString());
        Button btnSeeall3 = (Button) view.findViewById(R.id.see_all_section3);
        btnSeeall3.setOnClickListener(this);

        TextView section4Title = (TextView) view.findViewById(R.id.txt_section4_title);
        section4Title.setText(section4Array.get("title").toString());
        Button btnSeeall4 = (Button) view.findViewById(R.id.see_all_section4);
        btnSeeall4.setOnClickListener(this);

        TextView section5Title = (TextView) view.findViewById(R.id.txt_section5_title);
        section5Title.setText(section5Array.get("title").toString());
        Button btnSeeall5 = (Button) view.findViewById(R.id.see_all_section5);
        btnSeeall5.setOnClickListener(this);

        TextView section6Title = (TextView) view.findViewById(R.id.txt_section6_title);
        section6Title.setText(section6Array.get("title").toString());
        Button btnSeeall6 = (Button) view.findViewById(R.id.see_all_section6);
        btnSeeall6.setOnClickListener(this);

        TextView section7Title = (TextView) view.findViewById(R.id.txt_section7_title);
        section7Title.setText(section7Array.get("title").toString());
        Button btnSeeall7 = (Button) view.findViewById(R.id.see_all_section7);
        btnSeeall7.setOnClickListener(this);


        recyclerFeaturedStyleView1 = (RecyclerView) view.findViewById(R.id.recycler_section3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerFeaturedStyleView1.getContext(), DividerItemDecoration.HORIZONTAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        FeaturedStyle1Adapter serviceAdapter = new FeaturedStyle1Adapter(getContext(), (ArrayList<LinkedTreeMap>) section3Array.get("docs"));
        recyclerFeaturedStyleView1.addItemDecoration(divider);
        recyclerFeaturedStyleView1.setLayoutManager(linearLayoutManager);
        recyclerFeaturedStyleView1.setAdapter(serviceAdapter);

        recyclerFeaturedStyleView2 = (RecyclerView) view.findViewById(R.id.recycler_section4);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider2 = new DividerItemDecoration(recyclerFeaturedStyleView2.getContext(), DividerItemDecoration.HORIZONTAL);
        divider2.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        FeaturedStyle2Adapter serviceAdapter2 = new FeaturedStyle2Adapter(getContext(), (ArrayList<LinkedTreeMap>) section4Array.get("docs"));
        recyclerFeaturedStyleView2.addItemDecoration(divider2);
        recyclerFeaturedStyleView2.setLayoutManager(linearLayoutManager2);
        recyclerFeaturedStyleView2.setAdapter(serviceAdapter2);

        recyclerFeaturedStyleView3 = (RecyclerView) view.findViewById(R.id.recycler_section5);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider3 = new DividerItemDecoration(recyclerFeaturedStyleView3.getContext(), DividerItemDecoration.HORIZONTAL);
        divider3.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        FeaturedStyle3Adapter serviceAdapter3 = new FeaturedStyle3Adapter(getContext(), (ArrayList<LinkedTreeMap>) section5Array.get("docs"));
        recyclerFeaturedStyleView3.addItemDecoration(divider3);
        recyclerFeaturedStyleView3.setLayoutManager(linearLayoutManager3);
        recyclerFeaturedStyleView3.setAdapter(serviceAdapter3);

        recyclerFeaturedStyleView4 = (RecyclerView) view.findViewById(R.id.recycler_section6);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider4 = new DividerItemDecoration(recyclerFeaturedStyleView4.getContext(), DividerItemDecoration.HORIZONTAL);
        divider4.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        FeaturedStyle4Adapter serviceAdapter4 = new FeaturedStyle4Adapter(getContext(), (ArrayList<LinkedTreeMap>) section7Array.get("docs"));
        recyclerFeaturedStyleView4.addItemDecoration(divider4);
        recyclerFeaturedStyleView4.setLayoutManager(linearLayoutManager4);
        recyclerFeaturedStyleView4.setAdapter(serviceAdapter4);

        recyclerFeaturedStyleView5 = (RecyclerView) view.findViewById(R.id.recycler_section7);
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider5 = new DividerItemDecoration(recyclerFeaturedStyleView5.getContext(), DividerItemDecoration.HORIZONTAL);
        divider5.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        FeaturedStyle5Adapter serviceAdapter5 = new FeaturedStyle5Adapter(getContext(), (ArrayList<LinkedTreeMap>) section7Array.get("docs"));
        recyclerFeaturedStyleView5.addItemDecoration(divider5);
        recyclerFeaturedStyleView5.setLayoutManager(linearLayoutManager5);
        recyclerFeaturedStyleView5.setAdapter(serviceAdapter5);

    }


    private void setUpPopularCategory() {

        TextView section2Title = (TextView) view.findViewById(R.id.txt_section2_title);
        section2Title.setText(section2Array.get("title").toString());
        Button btnSeeall2 = (Button) view.findViewById(R.id.see_all_section2);
        btnSeeall2.setOnClickListener(this);

        recyclerCategoriesView = (RecyclerView) view.findViewById(R.id.recycler_section2);
        PopularCategoriesAdapter popularCategoriesAdapter = new PopularCategoriesAdapter(getContext(), (ArrayList<LinkedTreeMap>) section2Array.get("docs"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerCategoriesView.getContext(), DividerItemDecoration.HORIZONTAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_transparent_divider));
        recyclerCategoriesView.addItemDecoration(divider);
        recyclerCategoriesView.setLayoutManager(linearLayoutManager);
        recyclerCategoriesView.setAdapter(popularCategoriesAdapter);

    }

    private void setUpSuggestedCategoryViewPager() {

        viewPagerSuggestedServices = (ViewPager) view.findViewById(R.id.view_pager_suggested_services);
        SuggestedCategoriesAdapter suggestedCategoriesAdapter = new SuggestedCategoriesAdapter(getChildFragmentManager(), bannerArray);
        viewPagerSuggestedServices.setAdapter(suggestedCategoriesAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);

    }

    @Override
    public void onClick(View v) {

    }
}
