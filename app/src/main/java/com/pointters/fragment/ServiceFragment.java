package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.ServiceDetailActivity;
import com.pointters.adapter.ServicesExploreAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.model.ServicesExploreModel;
import com.pointters.model.response.GetServicesExploreReponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener{
    private ServicesExploreAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private RecyclerView recyclerViewServices;
    private ArrayList<ServicesExploreModel> serviceArrayList = new ArrayList<>();
    private KProgressHUD loader;

    SwipyRefreshLayout swipyRefreshLayout;
    private String lastDocId = "";
    private int limitCnt = 10;
    private int page = 1;
    private int totalCnt = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_services, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Log.e("token: ", sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        initViews();
        getServicesExplore();

        recyclerViewServices.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ServicesExploreModel model = serviceArrayList.get(position);
                Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                intent.putExtra(ConstantUtils.SERVICE_ID, model.getId());
                startActivity(intent);
            }
        }));

        return view;
    }


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
        serviceAdapter = new ServicesExploreAdapter(getActivity(), serviceArrayList, new OnRecyclerViewButtonClickListener() {
            @Override
            public void onButtonClick(View v, int position) {

            }

        });
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_option));
        recyclerViewServices.addItemDecoration(divider);

        recyclerViewServices.setAdapter(serviceAdapter);
    }

    public void getServicesExplore(){

        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limitCnt));
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServicesExploreReponse> userServiceCall = apiService.getServicesExplore(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        userServiceCall.enqueue(new Callback<GetServicesExploreReponse>() {
            @Override
            public void onResponse(Call<GetServicesExploreReponse> call, Response<GetServicesExploreReponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
                if (swipyRefreshLayout.isRefreshing()){
                    swipyRefreshLayout.setRefreshing(false);
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();
                    page = response.body().getPage();
                    int pages = response.body().getPages();
                    if (page > pages) {
                        page = page - 1;
                    }

                    serviceArrayList.addAll(response.body().getDocs());
                    serviceAdapter.notifyItemRangeInserted(serviceAdapter.getItemCount(), serviceArrayList.size()-1);
                    serviceAdapter.notifyDataSetChanged();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getContext(), "callGetUserServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener((OnApiFailDueToSessionListener) getContext());
                }
            }

            @Override
            public void onFailure(Call<GetServicesExploreReponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
//                Toast.makeText(getActivity(), "Can't find the service info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
            page++;
            getServicesExplore();
        }else{
            page = 1;
            serviceArrayList.clear();
            getServicesExplore();
        }
    }
}
