package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.adapter.OffersAdapter;
import com.pointters.adapter.OrdersAdapter;

/**
 * Created by prashantkumar on 23/8/17.
 */

public class OffersFragment extends Fragment {
    private View view;
    private RecyclerView offersRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
        offersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        OffersAdapter ordersAdapter = new OffersAdapter();
        offersRecyclerView.setLayoutManager(linearLayoutManager);
        offersRecyclerView.setAdapter(ordersAdapter);
        return view;
    }
}
