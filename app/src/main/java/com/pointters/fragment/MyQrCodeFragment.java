package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pointters.R;

/**
 * Created by prashantkumar on 25/8/17.
 */

public class MyQrCodeFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout scanQrCode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_qr_code, container, false);
        scanQrCode=(RelativeLayout)view.findViewById(R.id.rl_sacn_qr_code);
        scanQrCode.setOnClickListener(this);
    return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.rl_sacn_qr_code:
                getFragmentManager().beginTransaction().replace(R.id.qr_code_conatiner,new QRCodeFragment ()).commit();

                break;
        }
    }
}
