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
import com.pointters.adapter.OffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.SentOfferModel;
import com.pointters.model.response.GetSentOfferResponse;
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
 * Created by prashantkumar on 23/8/17.
 */

public class OffersFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<SentOfferModel> sentOffersList=new ArrayList<>();
    private TextView txtNotFound;
    private OffersAdapter offersAdapter;
    private SwipeRefreshLayout refreshLayout;
    private KProgressHUD loader;

    private Double mTagLat = 0.0;
    private Double mTagLng = 0.0;

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

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mTagLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mTagLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        txtNotFound = (TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        offersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        offersRecyclerView.setLayoutManager(linearLayoutManager);
        offersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        offersAdapter = new OffersAdapter(getActivity(), sentOffersList, mTagLat, mTagLng);
        offersRecyclerView.setAdapter(offersAdapter);

        offersRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getSentOffersApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getSentOffersApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSentOffersApiCall(true, "");
            }
        });
    }

    private void getSentOffersApiCall(final boolean inited, String lastId) {
        if (inited) {
            sentOffersList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSentOfferResponse> getOffersSentCall = apiService.getSentOffers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getOffersSentCall.enqueue(new Callback<GetSentOfferResponse>() {
            @Override
            public void onResponse(Call<GetSentOfferResponse> call, Response<GetSentOfferResponse> response) {
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

                    sentOffersList.addAll(response.body().getDocs());
                    offersAdapter.notifyItemRangeInserted(offersAdapter.getItemCount(), sentOffersList.size()-1);

                    if (inited && sentOffersList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                        txtNotFound.setText("No offer found");
                    }
                }
                else if(response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersSentApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(OffersFragment.this);
                }
                else if(response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No offer found");
                }
            }

            @Override
            public void onFailure(Call<GetSentOfferResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && view != null)
//            getSentOffersApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersSentApi")) {
//            getSentOffersApiCall(true, "");
        }
    }
}
