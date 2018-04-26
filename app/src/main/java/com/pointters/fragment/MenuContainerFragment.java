package com.pointters.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.adapter.UpdatesAdapterAdapter;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.BuyOrderModel;

import java.util.ArrayList;

/**
 * Created by prashant on 22-11-2017.
 */

public class MenuContainerFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu_screen, container, false);
        Fragment fragment = new MenuScreenFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment)
                .commitAllowingStateLoss();

        return view;
    }

}
