package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.activity.ServiceDetailActivity;
import com.pointters.adapter.ProfileServicesAdapter;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.model.ServicesModel;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

public class ServiceFragment extends Fragment {
    private ProfileServicesAdapter serviceAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private RecyclerView recyclerViewServices;
    private ArrayList<ServicesModel> serviceArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_services, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initViews();


        recyclerViewServices.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                intent.putExtra(ConstantUtils.SERVICE_ID, "5a029b03b37404568cb6f20e");
                startActivity(intent);

            }
        }));


        return view;
    }


    private void initViews() {
        serviceArrayList.add(new ServicesModel());
        serviceArrayList.add(new ServicesModel());
        serviceArrayList.add(new ServicesModel());
        serviceArrayList.add(new ServicesModel());
        serviceArrayList.add(new ServicesModel());
        recyclerViewServices = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        serviceAdapter = new ProfileServicesAdapter(getActivity(), serviceArrayList);
        recyclerViewServices.setAdapter(serviceAdapter);

    }


}
