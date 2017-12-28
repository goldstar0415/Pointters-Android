package com.pointters.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.pointters.R;
import com.pointters.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BackgroundCheckActivity extends AppCompatActivity implements OnClickListener{
    private EditText dateOfBirthEditText;
    private int mYear, mMonth, mDay;
    private TextInputLayout firstNameTextInputLayout,middleNameTextInputLayout,lastNameTextInputLayout,emailTextInputLayout,phoneTextInputLayout,socialSecurityTextInputLayout,dobTextInputLayout,postalCodeTextInputLayout,driverLicenseNumberTextInputLayout,driverLicenseStateTextInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check);
        AppUtils.setToolBarWithBothIcon(BackgroundCheckActivity.this, getResources().getString(R.string.background_check), R.drawable.back_icon_grey, R.drawable.more_icon_horizontal);
        dateOfBirthEditText=(EditText)findViewById(R.id.edt_date_of_birth);
        dateOfBirthEditText.setOnClickListener(this);

        firstNameTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_first_name);
        middleNameTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_middle_name);
        lastNameTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_last_name);
        emailTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_email);
        phoneTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_phone_number);
        socialSecurityTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_social_security_number);
        dobTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_date_of_birth);
        postalCodeTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_postal_code);
        driverLicenseNumberTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_driver_license_number);
        driverLicenseStateTextInputLayout=(TextInputLayout)findViewById(R.id.text_input_driver_license_state);

        AppUtils.applyFontsToTextInputLayout(this,new TextInputLayout[]{firstNameTextInputLayout,middleNameTextInputLayout,lastNameTextInputLayout,emailTextInputLayout,phoneTextInputLayout,socialSecurityTextInputLayout,dobTextInputLayout,postalCodeTextInputLayout,driverLicenseNumberTextInputLayout,driverLicenseStateTextInputLayout});

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;
            case R.id.toolbar_right_img:
                //    startActivity(new Intent(CheckoutActivity.this, FulfillmentActivity.class));
                break;
           /* case R.id.edt_date_of_birth:
                final Calendar c;
                c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 23);
                c.set(Calendar.MINUTE, 59);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                final SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            c.set(year, monthOfYear, dayOfMonth);
                            String strDate = mdformat.format(c.getTime());
                            dateOfBirthEditText.setText(strDate);



                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                break;*/


        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
