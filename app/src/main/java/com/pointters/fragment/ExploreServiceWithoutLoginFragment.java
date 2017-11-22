package com.pointters.fragment;

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

import com.pointters.R;
import com.pointters.adapter.PopularCategoriesAdapter;
import com.pointters.adapter.ServiceAdapter;
import com.pointters.adapter.SuggestedCategoriesAdapter;
import com.pointters.model.ServiceModel;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class ExploreServiceWithoutLoginFragment extends Fragment {
    private View view;
    private ArrayList<ServiceModel> serviceArrayList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragement_explore_service_without_login, container, false);

        setUpSuggestedCategoryViewPager();

        setUpPopularCategory();

        setUpRecyclerView();

        return view;
    }


    private void setUpRecyclerView() {

        RecyclerView recyclerViewServices = (RecyclerView) view.findViewById(R.id.recycler_view_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ServiceAdapter serviceAdapter = new ServiceAdapter(getActivity(),serviceArrayList);
        recyclerViewServices.setNestedScrollingEnabled(false);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        recyclerViewServices.setAdapter(serviceAdapter);

    }


    private void setUpPopularCategory() {

        RecyclerView recyclerViewPopularCategories = (RecyclerView) view.findViewById(R.id.recycler_view_popular_categories);
        PopularCategoriesAdapter popularCategoriesAdapter = new PopularCategoriesAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerViewPopularCategories.getContext(), DividerItemDecoration.HORIZONTAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_white_divider));
        recyclerViewPopularCategories.addItemDecoration(divider);
        recyclerViewPopularCategories.setLayoutManager(linearLayoutManager);
        recyclerViewPopularCategories.setAdapter(popularCategoriesAdapter);

    }

    private void setUpSuggestedCategoryViewPager() {

        ViewPager viewPagerSuggestedServices = (ViewPager) view.findViewById(R.id.view_pager_suggested_services);
        SuggestedCategoriesAdapter suggestedCategoriesAdapter = new SuggestedCategoriesAdapter(getChildFragmentManager());
        viewPagerSuggestedServices.setAdapter(suggestedCategoriesAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator_view_pager_suggested_services);
        circlePageIndicator.setViewPager(viewPagerSuggestedServices);

    }

}
