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
import com.pointters.adapter.CustomOffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.ReceivedOfferModel;
import com.pointters.model.response.GetReceivedOffersResponse;
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
 * Created by prashantkumar on 26/9/17.
 */

public class CustomOffersFragment extends Fragment implements OnApiFailDueToSessionListener {
    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<ReceivedOfferModel> receivedOffersList=new ArrayList<>();
    private   CustomOffersAdapter customOffersAdapter;
    private TextView txtNotFound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtNotFound=(TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setText("No offer found");
        txtNotFound.setVisibility(View.GONE);
        offersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
         customOffersAdapter = new CustomOffersAdapter(getActivity(),receivedOffersList);
        offersRecyclerView.setLayoutManager(linearLayoutManager);
        offersRecyclerView.setAdapter(customOffersAdapter);

        return view;
    }


    private void getOffersReceivedApiCall() {
        receivedOffersList.clear();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetReceivedOffersResponse> getOffersReceivedCall=apiService.getReceivedOffer(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        getOffersReceivedCall.enqueue(new Callback<GetReceivedOffersResponse>() {
            @Override
            public void onResponse(Call<GetReceivedOffersResponse> call, Response<GetReceivedOffersResponse> response) {
                if(response.code()==200 && response.body()!=null)
                {
                    receivedOffersList.addAll(response.body().getDocs());
                    customOffersAdapter.notifyDataSetChanged();

                }else if(response.code()==401)
                {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersReceivedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CustomOffersFragment.this);
                }
                else if(response.code()==404)
                {
                    txtNotFound.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<GetReceivedOffersResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && view!=null)
            getOffersReceivedApiCall();
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersReceivedApi"))
        {
            getOffersReceivedApiCall();
        }
    }
}
