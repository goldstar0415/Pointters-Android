package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.GetLiveOffersActivity;
import com.pointters.adapter.LiveOffersAdapter;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.model.LiveOfferModel;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by jkc on 3/8/18.
 */

@SuppressLint("ValidFragment")
public class LiveOffersListFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener, View.OnClickListener{
    private LiveOffersAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private RecyclerView recyclerViewServices;
    public ArrayList<LiveOfferModel> serviceArrayList = new ArrayList<>();

    private RelativeLayout editView;
    private KProgressHUD loader;

    private LinearLayout spinnerCategories;
    private LinearLayout llEditButton, llNewRequestButton, llSeeRequestButton;
    SwipyRefreshLayout swipyRefreshLayout;
    private String lastDocId = "";
    private int limitCnt = 10;
    private int page = 1;
    private int totalCnt = 0;
    OnRecyclerViewButtonClickListener listener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_live_offers_list, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initViews();


        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews() {

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipyRefreshLayout.setOnRefreshListener(this);

        recyclerViewServices = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_option));
        recyclerViewServices.addItemDecoration(divider);
        listener = new OnRecyclerViewButtonClickListener() {
            @Override
            public void onButtonClick(View v, int position) {

            }
        };
        serviceAdapter = new LiveOffersAdapter(getActivity(), serviceArrayList, listener);
        recyclerViewServices.setAdapter(serviceAdapter);
        recyclerViewServices.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), GetLiveOffersActivity.class);
                startActivity(intent);
            }
        }));

        llEditButton = (LinearLayout) view.findViewById(R.id.ll_edit);
        llNewRequestButton = (LinearLayout) view.findViewById(R.id.ll_new_request);
        llSeeRequestButton = (LinearLayout) view.findViewById(R.id.ll_see_request);

        llEditButton.setOnClickListener(this);
        llNewRequestButton.setOnClickListener(this);
        llSeeRequestButton.setOnClickListener(this);

        editView = (RelativeLayout) view.findViewById(R.id.rl_overlay);
        editView.setVisibility(View.GONE);
        editView.setOnClickListener(this);
        spinnerCategories = (LinearLayout)view.findViewById(R.id.spinner_categories);
        spinnerCategories.setOnClickListener(this);

//        getLiveOffers();
    }

//    private void getLiveOffers(){
//        final Map<String, String> params = new HashMap<>();
//        params.put("page", String.valueOf(page));
//        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
//        Call<GetLiveOfferResponse> userServiceCall = apiService.getLiveOffer(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
//        userServiceCall.enqueue(new Callback<GetLiveOfferResponse>() {
//            @Override
//            public void onResponse(Call<GetLiveOfferResponse> call, Response<GetLiveOfferResponse> response) {
//                if (loader.isShowing()) {
//                    loader.dismiss();
//                }
//                if (swipyRefreshLayout.isRefreshing()){
//                    swipyRefreshLayout.setRefreshing(false);
//                }
//
//                if (response.code() == 200 && response.body() != null) {
//                    totalCnt = response.body().getTotal();
//                    limitCnt = response.body().getLimit();
//                    lastDocId = response.body().getLastDocId();
//                    page = response.body().getPage();
//                    int pages = response.body().getPages();
//                    if (page > pages) {
//                        page = page - 1;
//                    }
//                    Log.e("data: ", String.valueOf(response.body()));
//
//                    serviceArrayList.addAll(response.body().getDocs());
//                    serviceAdapter.notifyItemRangeInserted(serviceAdapter.getItemCount(), serviceArrayList.size()-1);
//                    serviceAdapter.notifyDataSetChanged();
//                }
//                else if (response.code() == 401) {
//                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getContext(), "callGetUserServiceApi");
//                    callLoginApiIfFails.OnApiFailDueToSessionListener((OnApiFailDueToSessionListener) getContext());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetLiveOfferResponse> call, Throwable t) {
//                if (loader.isShowing()) { loader.dismiss(); }
//                Toast.makeText(getActivity(), "Can't find the service info.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipyRefreshLayout.setRefreshing(false);
//        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
//            page++;
//            getLiveOffers();
//        }else{
//            page = 1;
//            serviceArrayList.clear();
//            getLiveOffers();
//        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setServiceArrayList(ArrayList<LiveOfferModel> models) {
        this.serviceArrayList = models;
        serviceAdapter = new LiveOffersAdapter(getContext(), serviceArrayList, listener);
        recyclerViewServices.setAdapter(serviceAdapter);
        serviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.spinner_categories:
                editView.setVisibility(View.VISIBLE);

                break;
            case R.id.rl_overlay:
                editView.setVisibility(View.GONE);
                break;

            case R.id.ll_new_request:
                editView.setVisibility(View.GONE);
                break;
            case R.id.ll_edit:
                editView.setVisibility(View.GONE);
                break;
            case R.id.ll_see_request:
                editView.setVisibility(View.GONE);
                break;
        }
    }
}
