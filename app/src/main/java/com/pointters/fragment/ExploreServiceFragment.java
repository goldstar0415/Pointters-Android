package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.activity.SearchServiceActivity;
import com.pointters.utils.ConstantUtils;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class ExploreServiceFragment extends Fragment implements View.OnClickListener {
    private TextView searchBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore_service, container, false);
        searchBar = (TextView) view.findViewById(R.id.txt_search_here_hint);
        Fragment fragment;
        searchBar.setOnClickListener(this);

        if (getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE).
                getBoolean(ConstantUtils.PREF_IS_LOGIN, false)) {

            fragment = new ExploreServiceAfterLoginFragment();
        } else {
            fragment = new ExploreServiceWithoutLoginFragment();
        }

        getFragmentManager().beginTransaction().replace(R.id.frame_layout_explore_service, fragment)
                .commitAllowingStateLoss();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_search_here_hint:


                startActivity(new Intent(getActivity(), SearchServiceActivity.class));

                break;
        }
    }
}
