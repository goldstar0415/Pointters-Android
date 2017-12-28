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
import com.pointters.adapter.CustomOffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.ReceivedOfferModel;
import com.pointters.model.response.GetReceivedOffersResponse;
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
 * Created by prashantkumar on 26/9/17.
 */

public class CustomOffersFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<ReceivedOfferModel> receivedOffersList = new ArrayList<>();
    private CustomOffersAdapter customOffersAdapter;
    private TextView txtNotFound;
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

        txtNotFound= (TextView)view.findViewById(R.id.txt_notfound);
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
        customOffersAdapter = new CustomOffersAdapter(getActivity(), receivedOffersList, mTagLat, mTagLng);
        offersRecyclerView.setAdapter(customOffersAdapter);

        offersRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getOffersReceivedApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getOffersReceivedApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOffersReceivedApiCall(true, "");
            }
        });
    }

    private void getOffersReceivedApiCall(final boolean inited, String lastId) {
        if (inited) {
            receivedOffersList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetReceivedOffersResponse> getOffersReceivedCall = apiService.getReceivedOffer(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getOffersReceivedCall.enqueue(new Callback<GetReceivedOffersResponse>() {
            @Override
            public void onResponse(Call<GetReceivedOffersResponse> call, Response<GetReceivedOffersResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if(response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    receivedOffersList.addAll(response.body().getDocs());
                    customOffersAdapter.notifyItemRangeInserted(customOffersAdapter.getItemCount(), receivedOffersList.size()-1);

                    if (inited && receivedOffersList.size() == 0) {
                        txtNotFound.setText("No offer found");
                        txtNotFound.setVisibility(View.VISIBLE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersReceivedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CustomOffersFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No offer found");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetReceivedOffersResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser && view != null)
//            getOffersReceivedApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersReceivedApi")) {
//            getOffersReceivedApiCall(true, "");
        }
    }
}
