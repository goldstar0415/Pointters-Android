package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pointters.R;

/**
 * Created by prashantkumar on 24/8/17.
 */

public class QRCodeFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout myQrCode,scanFromGallery;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        myQrCode=(RelativeLayout)view.findViewById(R.id.rl_my_qr_code);
        scanFromGallery=(RelativeLayout)view.findViewById(R.id.rl_scan_from_gallery);
        myQrCode.setOnClickListener(this);
        scanFromGallery.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_scan_from_gallery:
                //getFragmentManager().beginTransaction().replace(R.id.qr_code_conatiner,new QRCodeFragment ()).addToBackStack(null).commitAllowingStateLoss();

                break;
            case R.id.rl_my_qr_code:
                getFragmentManager().beginTransaction().replace(R.id.qr_code_conatiner,new MyQrCodeFragment ()).commit();

                break;
        }
    }

}
