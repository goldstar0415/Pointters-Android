package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.ChatActivity;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.adapter.BuyOrderAdapter;
import com.pointters.adapter.LiveOfferRequestsAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.BuyOrderModel;
import com.pointters.model.response.GetBuyOrderResponse;
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
 * Created by prashantkumar on 9/11/17.
 */

public class BuyOrdersFragment extends Fragment implements OnApiFailDueToSessionListener {
    private View view;
    private RecyclerView myOrderRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtNotFound;
    private BuyOrderAdapter buyOrderAdapter;
    private List<BuyOrderModel> buyOrderModelList=new ArrayList<>();
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
        buyOrderAdapter = new BuyOrderAdapter(getActivity(), buyOrderModelList, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.btn_chat:
                        moveToChat(position);
                        break;

                    case R.id.btn_phone:
                        moveToCall(position);
                        break;

                    case R.id.txt_service_provider_name:
                        moveToProfile(position);
                        break;
                }
            }
        });
        myOrderRecyclerView.setAdapter(buyOrderAdapter);
        myOrderRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getBuyOrdersApi(false, lastDocId);
                }
            }
        });

        loader.show();
        getBuyOrdersApi(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBuyOrdersApi(true, "");
            }
        });
    }

    private void moveToChat(int position) {
        if (buyOrderModelList.get(position).getSeller() != null) {
            String strFirst = "", strLast = "";
            if (buyOrderModelList.get(position).getSeller().getFirstName() != null && !buyOrderModelList.get(position).getSeller().getFirstName().isEmpty()) {
                strFirst = buyOrderModelList.get(position).getSeller().getFirstName();
            }
            if (buyOrderModelList.get(position).getSeller().getLastName() != null && !buyOrderModelList.get(position).getSeller().getLastName().isEmpty()) {
                strLast = buyOrderModelList.get(position).getSeller().getLastName();
            }
            String strOtherName = strFirst + " " + strLast;

            String strOtherPic = "";
            if (buyOrderModelList.get(position).getSeller().getProfilePic() != null && !buyOrderModelList.get(position).getSeller().getProfilePic().isEmpty()) {
                strOtherPic = buyOrderModelList.get(position).getSeller().getProfilePic();
                if (!strOtherPic.contains("https://s3.amazonaws.com")) {
                    strOtherPic = "https://s3.amazonaws.com" + strOtherPic;
                }
            }

            String strOtherId = "";
            if (buyOrderModelList.get(position).getSeller().getId() != null && !buyOrderModelList.get(position).getSeller().getId().isEmpty()) {
                strOtherId = buyOrderModelList.get(position).getSeller().getId();
            }

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            editor.putInt(ConstantUtils.USER_VERIFIED, 0).apply();
            editor.putString(ConstantUtils.CHAT_USER_ID, strOtherId).apply();
            editor.putString(ConstantUtils.CHAT_USER_NAME, strOtherName).apply();
            editor.putString(ConstantUtils.CHAT_USER_PIC, strOtherPic).apply();
            editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, "");
            startActivity(intent);
        }
    }

    private void moveToCall(int position) {
        if (buyOrderModelList.get(position).getSeller() != null) {
            String strPhone = "";
            if (buyOrderModelList.get(position).getSeller().getPhone() != null && !buyOrderModelList.get(position).getSeller().getPhone().isEmpty()) {
                strPhone = buyOrderModelList.get(position).getSeller().getPhone();

                if (strPhone.contains("+")) {
                    strPhone = strPhone.substring(1);
                }
            }

            if (!strPhone.equals("")) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strPhone));
                startActivity(callIntent);
            }
        }
    }

    private void moveToProfile(int position) {
        if (buyOrderModelList.get(position).getSeller() != null) {
            String strId = "";
            if (buyOrderModelList.get(position).getSeller().getId() != null && !buyOrderModelList.get(position).getSeller().getId().isEmpty()) {
                strId = buyOrderModelList.get(position).getSeller().getId();
            }

            if (!strId.equals("")) {
                Intent intent = new Intent(getActivity(), ProfileScreenActivity.class);
                startActivity(intent);
            }
        }
    }

    void getBuyOrdersApi(final boolean inited, String lastId) {
        if (inited) {
            buyOrderModelList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetBuyOrderResponse> buyOrdersCall=apiService.getBuyOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        buyOrdersCall.enqueue(new Callback<GetBuyOrderResponse>() {
            @Override
            public void onResponse(Call<GetBuyOrderResponse> call, Response<GetBuyOrderResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code()==200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    buyOrderModelList.addAll(response.body().getDocs());
                    buyOrderAdapter.notifyItemRangeInserted(buyOrderAdapter.getItemCount(), buyOrderModelList.size()-1);

                    if (inited && buyOrderModelList.size() == 0) {
                        txtNotFound.setVisibility(View.VISIBLE);
                        txtNotFound.setText("No order found");
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetBuyOrderApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(BuyOrdersFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No order found");
                }
            }

            @Override
            public void onFailure(Call<GetBuyOrderResponse> call, Throwable t) {
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
//            getBuyOrdersApi(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetBuyOrderApi")) {
//            getBuyOrdersApi(true, "");
        }
    }
}
