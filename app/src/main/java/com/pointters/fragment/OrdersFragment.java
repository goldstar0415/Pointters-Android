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
import com.pointters.adapter.OrdersAdapter;

/**
 * Created by prashantkumar on 21/8/17.
 */

public class OrdersFragment extends Fragment {
    private View view;
    private RecyclerView myOrderRecyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);

        myOrderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        OrdersAdapter ordersAdapter = new OrdersAdapter();
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        myOrderRecyclerView.setAdapter(ordersAdapter);
        return view;
    }
}
