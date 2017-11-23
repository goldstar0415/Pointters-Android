package com.pointters.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.BuyOrderAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.BuyOrderModel;
import com.pointters.model.response.GetBuyOrderResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtNotFound=(TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);
        txtNotFound.setText("No Order found");
        myOrderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         buyOrderAdapter = new BuyOrderAdapter(getActivity(),buyOrderModelList);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        myOrderRecyclerView.setAdapter(buyOrderAdapter);
        return view;
    }

    void getBuyOrdersApi()
    {
        buyOrderModelList.clear();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetBuyOrderResponse> buyOrdersCall=apiService.getBuyOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        buyOrdersCall.enqueue(new Callback<GetBuyOrderResponse>() {
            @Override
            public void onResponse(Call<GetBuyOrderResponse> call, Response<GetBuyOrderResponse> response) {
                if(response.code()==200 && response.body()!=null)
                {
                    buyOrderModelList.addAll(response.body().getDocs());
                    buyOrderAdapter.notifyDataSetChanged();

                }else if(response.code()==404)
                {
                    txtNotFound.setVisibility(View.VISIBLE);
                }else if(response.code()==401)
                {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetBuyOrderApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(BuyOrdersFragment.this);
                }

            }

            @Override
            public void onFailure(Call<GetBuyOrderResponse> call, Throwable t) {

            }

        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && view!=null)
            getBuyOrdersApi();

    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetBuyOrderApi"))
        {
            getBuyOrdersApi();
        }
    }
}
