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
import com.pointters.adapter.SellOrdersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.SellOrderModel;
import com.pointters.model.response.GetSellOrdersResponse;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtNotFound=(TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setText("No Order found");
        txtNotFound.setVisibility(View.GONE);
        myOrderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         sellOrdersAdapter = new SellOrdersAdapter(getActivity(),sellOrderModelList);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        myOrderRecyclerView.setAdapter(sellOrdersAdapter);


        return view;
    }


    void getSellOrdersApi()
    {
        sellOrderModelList.clear();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetSellOrdersResponse> sellOrdersCall=apiService.getSellOrder(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        sellOrdersCall.enqueue(new Callback<GetSellOrdersResponse>() {
            @Override
            public void onResponse(Call<GetSellOrdersResponse> call, Response<GetSellOrdersResponse> response) {
                if(response.code()==200 && response.body()!=null)
                {
                    sellOrderModelList.addAll(response.body().getDocs());
                    sellOrdersAdapter.notifyDataSetChanged();

                }else if(response.code()==404)
                {
                    txtNotFound.setVisibility(View.VISIBLE);

                }else if(response.code()==401)
                {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetSellOrdersApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SellOrdersFragment.this);
                }
            }

            @Override
            public void onFailure(Call<GetSellOrdersResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && view!=null)
            getSellOrdersApi();

    }


    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetSellOrdersApi"))
        {
            getSellOrdersApi();
        }

    }
}
