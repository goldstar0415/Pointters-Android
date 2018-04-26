package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pointters.R;
import com.pointters.model.AddressModel;
import com.pointters.model.LocationModel;
import com.pointters.model.ParcelModel;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddShippingDetailActivity extends AppCompatActivity implements View.OnClickListener{


    //PaymentAdapter paymentAdapter;
    private Button btnSave, btnReset;


    private AddressModel addressModel;
    private ParcelModel parcel;

    TextInputLayout tilName, tilStreet, tilApt, tilState, tilCity, tilZip, tilCountry;
    EditText edtName, edtStreet, edtApt, edtState, edtCity, edtZip, edtCountry;

    TextInputLayout tiHeight, tiWeight, tiLength, tiWidth;
    EditText edtHegiht, edtWeight, edtLength, edtWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_detail);
        AppUtils.setToolBarWithBothIcon(AddShippingDetailActivity.this, getResources().getString(R.string.shipping_details), R.drawable.back_icon, 0);

        btnSave= (Button) findViewById(R.id.btn_save);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        edtName = (EditText)findViewById(R.id.edt_name);
        edtStreet = (EditText)findViewById(R.id.edt_street);
        edtApt = (EditText)findViewById(R.id.edt_apt);
        edtState = (EditText)findViewById(R.id.edt_state);
        edtCity = (EditText)findViewById(R.id.edt_city);
        edtZip = (EditText)findViewById(R.id.edt_zip);
        edtCountry = (EditText)findViewById(R.id.edt_country);
        edtHegiht = (EditText)findViewById(R.id.edt_height);
        edtLength = (EditText)findViewById(R.id.edt_length);
        edtWeight = (EditText)findViewById(R.id.edt_weight);
        edtWidth = (EditText)findViewById(R.id.edt_width);

        tilName = (TextInputLayout) findViewById(R.id.text_input_name);
        tilStreet = (TextInputLayout) findViewById(R.id.text_input_street);
        tilApt = (TextInputLayout) findViewById(R.id.text_input_apt);
        tilState = (TextInputLayout) findViewById(R.id.text_input_state);
        tilCity = (TextInputLayout) findViewById(R.id.text_input_city);
        tilZip = (TextInputLayout) findViewById(R.id.text_input_zip);
        tilCountry = (TextInputLayout) findViewById(R.id.text_input_country);
        tiHeight = (TextInputLayout) findViewById(R.id.text_input_height);
        tiWeight= (TextInputLayout) findViewById(R.id.text_input_weight);
        tiLength = (TextInputLayout) findViewById(R.id.text_input_length);
        tiWidth = (TextInputLayout) findViewById(R.id.text_input_width);
    }

    public boolean checkValidateData(){
        int valid = 0;
        if (edtName.getText().toString().length() == 0 || edtName.getText().toString().equals(" ")) {
            tilName.setError("You need to enter a Name");
            tilName.setErrorEnabled(true);
            valid++;
        }else{
            tilName.setErrorEnabled(false);
        }
        if (edtStreet.getText().toString().length() == 0 || edtStreet.getText().toString().equals(" ")) {
            tilStreet.setError("You need to enter a Street");
            tilStreet.setErrorEnabled(true);
            valid++;
        }else{
            tilStreet.setErrorEnabled(false);
        }
        if (edtCity.getText().toString().length() == 0 || edtCity.getText().toString().equals(" ")) {
            tilCity.setError("You need to enter a City");
            tilCity.setErrorEnabled(true);
            valid++;
        }else{
            tilCity.setErrorEnabled(false);
        }
        if (edtState.getText().toString().length() == 0 || edtState.getText().toString().equals(" ")) {
            tilState.setError("You need to enter a State");
            tilState.setErrorEnabled(true);
            valid++;
        }else{
            tilState.setErrorEnabled(false);
        }
        if (edtZip.getText().toString().length() == 0 || edtZip.getText().toString().equals(" ")) {
            tilZip.setError("You need to enter a Zip");
            tilZip.setErrorEnabled(true);
            valid++;
        }else{
            tilZip.setErrorEnabled(false);
        }
        if (edtCountry.getText().toString().length() == 0 || edtCountry.getText().toString().equals(" ")) {
            tilCountry.setError("You need to enter a Country");
            tilCountry.setErrorEnabled(true);
            valid++;
        }else{
            tilCountry.setErrorEnabled(false);
        }
        if (edtWeight.getText().toString().length() == 0 || edtWeight.getText().toString().equals(" ")) {
            tiWeight.setError("You need to enter a Weight");
            tiWeight.setErrorEnabled(true);
            valid++;
        }else{
            tiWeight.setErrorEnabled(false);
        }
        if (edtHegiht.getText().toString().length() == 0 || edtHegiht.getText().toString().equals(" ")) {
            tiHeight.setError("You need to enter a Height");
            tiHeight.setErrorEnabled(true);
            valid++;
        }else{
            tiHeight.setErrorEnabled(false);
        }
        if (edtLength.getText().toString().length() == 0 || edtLength.getText().toString().equals(" ")) {
            tiLength.setError("You need to enter a Length");
            tiLength.setErrorEnabled(true);
            valid++;
        }else{
            tiLength.setErrorEnabled(false);
        }
        if (edtWidth.getText().toString().length() == 0 || edtWidth.getText().toString().equals(" ")) {
            tiWidth.setError("You need to enter a Width");
            tiWidth.setErrorEnabled(true);
            valid++;
        }else{
            tiWidth.setErrorEnabled(false);
        }
        return valid == 0;
    }

    public void resetShippingDetail(){
        edtName.setText("");
        edtStreet.setText("");
        edtCity.setText("");
        edtApt.setText("");
        edtState.setText("");
        edtZip.setText("");
        edtCountry.setText("");
        edtWeight.setText("");
        edtHegiht.setText("");
        edtLength.setText("");
        edtWidth.setText("");

        tilName.setEnabled(false);
        tilStreet.setEnabled(false);
        tilApt.setEnabled(false);
        tilCountry.setEnabled(false);
        tilCity.setEnabled(false);
        tilState.setEnabled(false);
        tilZip.setEnabled(false);
        tiHeight.setEnabled(false);
        tiWeight.setEnabled(false);
        tiWidth.setEnabled(false);
        tiLength.setEnabled(false);
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
                if (checkValidateData()){
                    saveShippingDetail();
                }
                break;
            case R.id.btn_reset:
                resetShippingDetail();
                break;

        }
    }

    public void saveShippingDetail(){
        addressModel = new AddressModel();
        addressModel.setState(edtState.getText().toString());
        addressModel.setZip(edtZip.getText().toString());
        addressModel.setCountry(edtCountry.getText().toString());
        addressModel.setCity(edtCity.getText().toString());
        addressModel.setStreet1(edtStreet.getText().toString());
        addressModel.setStreet2(edtApt.getText().toString());
        addressModel.setName(edtName.getText().toString());

        parcel = new ParcelModel();
        parcel.setHeight(Float.valueOf(edtHegiht.getText().toString()));
        parcel.setLength(Float.valueOf(edtLength.getText().toString()));
        parcel.setWeight(Float.valueOf(edtWeight.getText().toString()));
        parcel.setWidth(Float.valueOf(edtWidth.getText().toString()));

        Intent intent = new Intent();
        intent.putExtra(ConstantUtils.ADD_ADDRESS, addressModel);
        intent.putExtra(ConstantUtils.ADD_PARCEL, parcel);
        setResult(RESULT_OK, intent);
        finish();
        AndroidUtils.hideKeyBoard(AddShippingDetailActivity.this);

    }
}