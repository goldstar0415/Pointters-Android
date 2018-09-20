package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.pointters.activity.CustomOfferDetailsActivity;
import com.pointters.adapter.LiveOfferRequestsAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.LiveOfferRequestsModel;
import com.pointters.model.response.GetLiveOfferRequestsResponse;
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
 * Created by mac on 12/4/17.
 */

public class LiveOfferRequestFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView liveRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<LiveOfferRequestsModel> liveOfferRequestsList = new ArrayList<>();
    private LiveOfferRequestsAdapter liveOfferRequestAdapter;
    private TextView txtNotFound;
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

        txtNotFound= (TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        liveRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        liveRecyclerView.setLayoutManager(linearLayoutManager);
        liveRecyclerView.setItemAnimator(new DefaultItemAnimator());
        liveOfferRequestAdapter = new LiveOfferRequestsAdapter(getActivity(), liveOfferRequestsList, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.upper_view:
                        detailOffer(position);
                        break;

                    case R.id.btn_accept:
                        detailOffer(position);
                        break;
                }
            }

        });
        liveRecyclerView.setAdapter(liveOfferRequestAdapter);

        liveRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getLiveOfferRequestsApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getLiveOfferRequestsApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                getLiveOfferRequestsApiCall(true, "");
            }
        });
    }

    private void detailOffer(int position) {
        if (liveOfferRequestsList.get(position).getRequests().getId() != null && !liveOfferRequestsList.get(position).getRequests().getId().isEmpty()) {
            String offerId = liveOfferRequestsList.get(position).getRequests().getId();

//            Intent intent = new Intent(getActivity(), CustomOfferDetailsActivity.class);
//            intent.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
//            intent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 2);
//            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Can't get the detail info", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLiveOfferRequestsApiCall(final boolean inited, String lastId) {
        if (inited) {
            liveOfferRequestsList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetLiveOfferRequestsResponse> getOffersReceivedCall = apiService.getLiveOfferRequests(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getOffersReceivedCall.enqueue(new Callback<GetLiveOfferRequestsResponse>() {
            @Override
            public void onResponse(Call<GetLiveOfferRequestsResponse> call, Response<GetLiveOfferRequestsResponse> response) {
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

                    liveOfferRequestsList.addAll(response.body().getDocs());
                    liveOfferRequestAdapter.notifyItemRangeInserted(liveOfferRequestAdapter.getItemCount(), liveOfferRequestsList.size()-1);

                    if (inited && liveOfferRequestsList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                        txtNotFound.setText("No live offer found");
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersReceivedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LiveOfferRequestFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No live offer found");
                }
            }

            @Override
            public void onFailure(Call<GetLiveOfferRequestsResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser && view != null)
//            getLiveOfferRequestsApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersReceivedApi")) {
//            getLiveOfferRequestsApiCall(true, "");
        }
    }
}
