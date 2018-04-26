package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.LiveOfferModel;
import com.pointters.model.response.GetLiveOfferResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.SocketManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jkc on 3/8/18.
 */

@SuppressLint("ValidFragment")
public class LiveOffersFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    public Fragment fragment;
    private String lastDocId = "";
    private int limitCnt = 10;
    private int page = 1;
    private int totalCnt = 0;
    private KProgressHUD loader;
    private ArrayList<LiveOfferModel> serviceArrayList = new ArrayList<>();
    private io.socket.client.Socket mSocket;

    FloatingActionButton fab;

    LiveOffersListFragment liveOffersListFragment;
    LiveOffersMapFragment liveOffersMapFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_live_offers, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        liveOffersListFragment = new LiveOffersListFragment();
        liveOffersMapFragment = new LiveOffersMapFragment();

        mSocket = SocketManager.getInstance().getSocket();
        if (!mSocket.connected()) {
            SocketManager.getInstance().init(sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        }
        initViews();
        processLiveOffer();
        getLiveOffers();

        return view;
    }


    private void initViews() {
        fragment = new LiveOffersMapFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_live_container, fragment)
                .commitAllowingStateLoss();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setSelected(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setSelected(!fab.isSelected());
                if (fab.isSelected()) {
                    fragment = liveOffersMapFragment;
                } else {
                    fragment = liveOffersListFragment;
                }

                getFragmentManager().beginTransaction().replace(R.id.frame_live_container, fragment)
                        .commitAllowingStateLoss();
            }
        });


    }


    private void getLiveOffers(){
        final Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetLiveOfferResponse> userServiceCall = apiService.getLiveOffer(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        userServiceCall.enqueue(new Callback<GetLiveOfferResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<GetLiveOfferResponse> call, Response<GetLiveOfferResponse> response) {
//                if (loader.isShowing()) {
//                    loader.dismiss();
//                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();
                    page = response.body().getPage();
                    int pages = response.body().getPages();
                    if (page > pages) {
                        page = page - 1;
                    }
                    Log.e("data: ", String.valueOf(response.body()));

                    serviceArrayList.addAll(response.body().getDocs());
                    liveOffersListFragment.serviceArrayList = serviceArrayList;
                    liveOffersMapFragment.serviceArrayList = serviceArrayList;
                    if (fab.isSelected()) {
                        fragment = liveOffersMapFragment;
                    }else{
                        fragment = liveOffersListFragment;
                    }
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().replace(R.id.frame_live_container, fragment)
                                .commitAllowingStateLoss();
                    }


                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getContext(), "callGetUserServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener((OnApiFailDueToSessionListener) getContext());
                }
            }

            @Override
            public void onFailure(Call<GetLiveOfferResponse> call, Throwable t) {
//                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Can't find the service info.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processLiveOffer() {
        mSocket.on("live_offer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data;
                        try {
                            data = (JSONObject) args[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (data == null)
                            return;
                        getLiveOffers();
//                        getChatHistory(data);
                    }
                });
            }
        });

        mSocket.on("join_live_offer_room", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data;
                        try {
                            data = (JSONObject) args[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (data == null)
                            return;
                        getLiveOffers();
//                        receivedMessage(data);
                    }
                });
            }
        });
    }

}
