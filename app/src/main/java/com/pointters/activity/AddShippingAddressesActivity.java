package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.AddressModel;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddShippingAddressesActivity extends AppCompatActivity implements View.OnClickListener{


    //PaymentAdapter paymentAdapter;
    private Button btnSave, btnReset;

    AddressModel addressModel;

    TextInputLayout tilFirstName, tilLastName, tilStreet, tilApt, tilState, tilCity, tilZip;
    EditText edtFirstName, edtLastName, edtStreet, edtApt, edtState, edtCity, edtZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_address);
        AppUtils.setToolBarWithBothIcon(AddShippingAddressesActivity.this, getResources().getString(R.string.enter_new_address), R.drawable.back_icon, 0);

        btnSave= (Button) findViewById(R.id.btn_save);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edtFirstName = (EditText)findViewById(R.id.edt_first_name);
        edtLastName = (EditText)findViewById(R.id.edt_last_name);
        edtStreet = (EditText)findViewById(R.id.edt_street);
        edtApt = (EditText)findViewById(R.id.edt_apt);
        edtState = (EditText)findViewById(R.id.edt_state);
        edtCity = (EditText)findViewById(R.id.edt_city);
        edtZip = (EditText)findViewById(R.id.edt_zip);

        tilFirstName = (TextInputLayout) findViewById(R.id.text_input_first_name);
        tilLastName = (TextInputLayout) findViewById(R.id.text_input_last_name);
        tilStreet = (TextInputLayout) findViewById(R.id.text_input_street);
        tilApt = (TextInputLayout) findViewById(R.id.text_input_apt);
        tilState = (TextInputLayout) findViewById(R.id.text_input_state);
        tilCity = (TextInputLayout) findViewById(R.id.text_input_city);
        tilZip = (TextInputLayout) findViewById(R.id.text_input_zip);


    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.toolbar_right_img:

                break;

            case R.id.btn_save:
                break;
            case R.id.btn_reset:
                break;

        }
    }
}