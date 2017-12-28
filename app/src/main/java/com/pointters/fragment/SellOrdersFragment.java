package com.pointters.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.BuyOrderAdapter;
import com.pointters.adapter.SellOrdersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.SellOrderModel;
import com.pointters.model.response.GetSellOrdersResponse;
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
 * Created by prashantkumar on 21/8/17.
 */

public class SellOrdersFragment extends Fragment implements OnApiFailDueToSessionListener {
    private View view;
    private RecyclerView myOrderRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtNotFound;
    private List<SellOrderModel> sellOrderModelList=new ArrayList<>();
    private SellOrdersAdapter sellOrdersAdapter;
    private SwipeRefreshLayout refreshLayout;
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

        txtNotFound=(TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        myOrderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        myOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sellOrdersAdapter = new SellOrdersAdapter(getActivity(), sellOrderModelList);
        myOrderRecyclerView.setAdapter(sellOrdersAdapter);

        myOrderRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getSellOrdersApi(false, lastDocId);
                }
            }
        });

        loader.show();
        getSellOrdersApi(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSellOrdersApi(true, "");
            }
        });
    }

    void getSellOrdersApi(final boolean inited, String lastId) {
        if (inited) {
            sellOrderModelList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSellOrdersResponse> sellOrdersCall = apiService.getSellOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        sellOrdersCall.enqueue(new Callback<GetSellOrdersResponse>() {
            @Override
            public void onResponse(Call<GetSellOrdersResponse> call, Response<GetSellOrdersResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    sellOrderModelList.addAll(response.body().getDocs());
                    sellOrdersAdapter.notifyItemRangeInserted(sellOrdersAdapter.getItemCount(), sellOrderModelList.size()-1);

                    if (inited && sellOrderModelList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                        txtNotFound.setText("No order found");
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetSellOrdersApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SellOrdersFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No order found");
                }
            }

            @Override
            public void onFailure(Call<GetSellOrdersResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && view != null) {
//            getSellOrdersApi(true, "");
//        }
    }

    @Override
    public void onApiFail(String apiSource) {
        if (apiSource.equals("callGetSellOrdersApi")) {
//            getSellOrdersApi(true, "");
        }
    }
}
