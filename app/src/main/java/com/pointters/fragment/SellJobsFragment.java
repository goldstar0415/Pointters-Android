package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.JobDetailActivity;
import com.pointters.adapter.SellJobsAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.SellJobsModel;
import com.pointters.model.response.GetSellJobsResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/5/17.
 */

public class SellJobsFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView sellJobsRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<SellJobsModel> sellJobsList=new ArrayList<>();
    private TextView txtNotFound;
    private SellJobsAdapter sellJobsAdapter;
    private SwipyRefreshLayout refreshLayout;
    private KProgressHUD loader;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        txtNotFound = (TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        sellJobsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        sellJobsRecyclerView.setLayoutManager(linearLayoutManager);
        sellJobsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sellJobsAdapter = new SellJobsAdapter(getActivity(), sellJobsList);
        sellJobsRecyclerView.setAdapter(sellJobsAdapter);
        sellJobsAdapter.setListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SellJobsModel model = sellJobsList.get(position);
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                intent.putExtra(ConstantUtils.SELECT_JOB_ID, model.getRequestOffers().getRequestOfferId());
                startActivity(intent);
            }
        });
        sellJobsRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getSellJobsApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getSellJobsApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
               getSellJobsApiCall(true, "");
            }
        });
    }

    private void getSellJobsApiCall(final boolean inited, String lastId) {
        if (inited) {
            sellJobsList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSellJobsResponse> getSellJobsCall = apiService.getSellJobs(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getSellJobsCall.enqueue(new Callback<GetSellJobsResponse>() {
            @Override
            public void onResponse(Call<GetSellJobsResponse> call, Response<GetSellJobsResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    limitCnt = response.body().getLimit();
                    totalCnt = response.body().getTotal();
                    lastDocId = response.body().getLastDocId();

                    sellJobsList.addAll(response.body().getDocs());
                    sellJobsAdapter.notifyItemRangeInserted(sellJobsAdapter.getItemCount(), sellJobsList.size()-1);
                    sellJobsAdapter.notifyDataSetChanged();

                    if (inited && sellJobsList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                        txtNotFound.setText("No job found");
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if(response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetSellJobsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SellJobsFragment.this);
                }
                else if(response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No job found");
                }
            }

            @Override
            public void onFailure(Call<GetSellJobsResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && view != null)
//            getSellJobsApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersSentApi")) {
//            getSellJobsApiCall(true, "");
        }
    }
}
