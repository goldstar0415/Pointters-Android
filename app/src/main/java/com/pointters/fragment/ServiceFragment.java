package com.pointters.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.activity.ForgotPasswordActivity;
import com.pointters.activity.ServiceDetailActivity;
import com.pointters.adapter.ServiceAdapter;
import com.pointters.listener.RecyclerViewListener;

/**
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class ServiceFragment extends Fragment {
    private View view;
    private  RecyclerView recyclerViewServices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_services, container, false);

        initViews();

        setUpRecyclerView();
        recyclerViewServices.addOnItemTouchListener(new RecyclerViewListener(getActivity(), new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(getActivity(), ServiceDetailActivity.class));
            }
        }));


        return view;
    }

    private void setUpRecyclerView() {

        recyclerViewServices = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ServiceAdapter serviceAdapter = new ServiceAdapter();
        recyclerViewServices.setLayoutManager(linearLayoutManager);
        recyclerViewServices.setAdapter(serviceAdapter);

    }


    private void initViews() {


    }
}
