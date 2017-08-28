package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;

/**
 * Created by prashantkumar on 25/8/17.
 */

public class HomeQRCodeFragment extends Fragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_qr_code, container, false);
        getFragmentManager().beginTransaction().replace(R.id.qr_code_conatiner,new QRCodeFragment()).addToBackStack(null).commitAllowingStateLoss();
        return view;
    }
}
