package com.pointters.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.utils.AppUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class AddPriceOptionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView addPriceButton;
    private ImageView dropDownImg;
    private TextView txtInFormat;
    private EditText priceAmountEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_price_option);

        initViews();

        //set toolbar
        AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.add_price_option_header),
                R.drawable.back_icon_grey, 0);

       // priceAmountEditText.setText("$" + priceAmountEditText.getText().toString());


      /*  priceAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                priceAmountEditText.append("$" + priceAmountEditText.getText().toString());
            }
        });*/

        setOnClick();

    }

    private void setOnClick() {

        addPriceButton.setOnClickListener(this);
        dropDownImg.setOnClickListener(this);
    }

    private void initViews() {

        addPriceButton = (TextView) findViewById(R.id.save_pricing_btn);
        dropDownImg = (ImageView) findViewById(R.id.drop_down_format);
        txtInFormat = (TextView) findViewById(R.id.txt_in_date_format);
        priceAmountEditText=(EditText)findViewById(R.id.edt_price_amount);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.toolbar_lft_img:

                onBackPressed();

                break;

            case R.id.save_pricing_btn:

                break;

            case R.id.drop_down_format:

                PopupMenu popup = new PopupMenu(this, dropDownImg);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu_price_option, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        txtInFormat.setText(item.getTitle() + "(s)");

                        return true;
                    }
                });

                popup.show(); //showing popup menu

                break;
        }

    }
}
