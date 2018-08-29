package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.JobDetailActivity;
import com.pointters.activity.SendCustomOfferActivity;
import com.pointters.adapter.JobsExploreAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.ExploreJobsModel;
import com.pointters.model.response.GetJobsExploreReponse;
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

public class JobsFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener{
    private JobsExploreAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private RecyclerView recyclerViewServices;
    private ArrayList<ExploreJobsModel> serviceArrayList = new ArrayList<>();
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
        getJobsExplore();

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
        serviceAdapter = new JobsExploreAdapter(getActivity(), serviceArrayList, new OnRecyclerViewButtonClickListener() {
            @Override
            public void onButtonClick(View v, int position) {
                if (v.getId() == R.id.btn_make_offer) {
                    Intent intent = new Intent(getActivity(), SendCustomOfferActivity.class);
                    intent.putExtra(ConstantUtils.SERVICE_ID, serviceArrayList.get(position).getId());
                    startActivity(intent);
                }else if (v.getId() == R.id.btn_edit_offer) {
                    Intent intent = new Intent(getActivity(), SendCustomOfferActivity.class);
                    intent.putExtra(ConstantUtils.SERVICE_ID, serviceArrayList.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        serviceAdapter.setListener1(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                ExploreJobsModel model = serviceArrayList.get(position);
                intent.putExtra(ConstantUtils.SELECT_JOB_ID, model.getId());
                startActivity(intent);

            }
        });

        recyclerViewServices.setAdapter(serviceAdapter);
    }

    public void getJobsExplore(){

        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetJobsExploreReponse> userServiceCall = apiService.getJobsExplore(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        userServiceCall.enqueue(new Callback<GetJobsExploreReponse>() {
            @Override
            public void onResponse(Call<GetJobsExploreReponse> call, Response<GetJobsExploreReponse> response) {
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
                    Log.e("data: ", String.valueOf(response.body()));

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
            public void onFailure(Call<GetJobsExploreReponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Can't find the service info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
            page++;
            getJobsExplore();
        }else{
            page = 1;
            serviceArrayList.clear();
            getJobsExplore();
        }
    }
}
