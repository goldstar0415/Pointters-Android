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
import com.pointters.adapter.OffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.SentOfferModel;
import com.pointters.model.response.GetSentOfferResponse;
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
 * Created by prashantkumar on 23/8/17.
 */

public class OffersFragment extends Fragment implements OnApiFailDueToSessionListener {
    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<SentOfferModel> sentOffersList=new ArrayList<>();
    private TextView txtNotFound;
    private OffersAdapter ordersAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
        offersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtNotFound=(TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);
        txtNotFound.setText("No offfer found");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         ordersAdapter = new OffersAdapter(getActivity(),sentOffersList);
        offersRecyclerView.setLayoutManager(linearLayoutManager);
        offersRecyclerView.setAdapter(ordersAdapter);

        return view;
    }

    private void getSentOffersApiCall() {
        sentOffersList.clear();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetSentOfferResponse> getOffersSentCall=apiService.getSentOffers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getOffersSentCall.enqueue(new Callback<GetSentOfferResponse>() {
            @Override
            public void onResponse(Call<GetSentOfferResponse> call, Response<GetSentOfferResponse> response) {
                if(response.code()==200)
                {
                    sentOffersList.addAll(response.body().getDocs());
                    ordersAdapter.notifyDataSetChanged();
                }else if(response.code()==401)
                {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersSentApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(OffersFragment.this);
                }
                else if(response.code()==404)
                {
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetSentOfferResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && view!=null)
            getSentOffersApiCall();
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersSentApi"))
        {
            getSentOffersApiCall();
        }
    }
}
