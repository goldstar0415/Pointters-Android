package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.model.CurrentPageDetails;

import java.util.ArrayList;

/**
 * Created by vikas on 8/25/2017.
 */

public class PointFragment extends Fragment {
    RecyclerView recyclerView;
    View view;
    private ArrayList<CurrentPageDetails> currentPageDetailsArrayList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.point_fragment, container, false);
        init();
        return view;
    }

    public void init() {


    }
}
