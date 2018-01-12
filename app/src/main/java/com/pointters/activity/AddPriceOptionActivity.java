package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.model.Prices;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class AddPriceOptionActivity extends AppCompatActivity implements View.OnClickListener, OnEditTextChangeListener {

    private TextView addPriceButton,txtInFormat;
    private EditText priceAmountEditText, editTextPriceOptionDescription, editTextDays;
    private RelativeLayout layoutEdtSearch,layoutDropDown;
    private Prices prices;
    private String timeUnitOfMeasure = "day";
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_price_option);

        initViews();

       /* AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.add_price_option_header),
                R.drawable.back_icon_grey, 0);*/
        layoutEdtSearch = (RelativeLayout) findViewById(R.id.layout_edt_search);
        layoutEdtSearch.setOnClickListener(this);
        priceAmountEditText.setText("90");
        priceAmountEditText.setSelection(priceAmountEditText.getText().length());


        setOnClick();

        setEditTextListener();

        if (getIntent() != null && getIntent().getSerializableExtra(ConstantUtils.PRICE) != null) {
            prices = (Prices) getIntent().getSerializableExtra(ConstantUtils.PRICE);
            priceAmountEditText.setText(String.valueOf(prices.getPrice()));
            editTextPriceOptionDescription.setText(prices.getDescription());
            editTextDays.setText(String.valueOf(prices.getTime()));
            txtInFormat.setText(prices.getTimeUnitOfMeasure() + "(s)");
            addPriceButton.setText(getResources().getString(R.string.save));

            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.edit_price_option_header),
                    R.drawable.back_icon_grey, R.drawable.delete_icon);
        }else {
            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.add_price_option_header),
                    R.drawable.back_icon_grey, 0);
        }
    }
    private void setEditTextListener() {



        //Custom Edit text change listener with returning id of edit text
        priceAmountEditText.addTextChangedListener(new MyTextWatcher(priceAmountEditText, this));
        editTextPriceOptionDescription.addTextChangedListener(new MyTextWatcher(editTextPriceOptionDescription, this));
        editTextDays.addTextChangedListener(new MyTextWatcher(editTextDays, this));

    }


    private void setOnClick() {

        addPriceButton.setOnClickListener(this);

        layoutDropDown.setOnClickListener(this);
    }

    private void initViews() {
        editTextDays = (EditText) findViewById(R.id.edt_how_many_days);
        editTextPriceOptionDescription = (EditText) findViewById(R.id.edt_price_option_descn);
        addPriceButton = (TextView) findViewById(R.id.save_pricing_btn);
        txtInFormat = (TextView) findViewById(R.id.txt_in_date_format);
        priceAmountEditText = (EditText) findViewById(R.id.edt_price_amount);
        layoutDropDown=(RelativeLayout)findViewById(R.id.layout_drop_down);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_edt_search:
                priceAmountEditText.setEnabled(true);
                priceAmountEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;

            case R.id.toolbar_lft_img:
                AndroidUtils.hideKeyBoard(AddPriceOptionActivity.this);
                onBackPressed();

                break;

            case R.id.save_pricing_btn:
                AndroidUtils.hideKeyBoard(AddPriceOptionActivity.this);
                if(addPriceButton.isSelected())
                {
                    prices = new Prices(editTextPriceOptionDescription.getText().toString(), Integer.parseInt(priceAmountEditText.getText().toString()),Integer.parseInt( editTextDays.getText().toString()), timeUnitOfMeasure, "$");
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.DELETE, "no");
                    intent.putExtra(ConstantUtils.POSITION,getIntent().getStringExtra(ConstantUtils.POSITION));
                    intent.putExtra(ConstantUtils.PRICE, prices);
                    setResult(RESULT_OK, intent);
                    finish();

                }



                break;
            case R.id.toolbar_right_img:
                Intent intents = new Intent();
                intents.putExtra(ConstantUtils.POSITION,getIntent().getStringExtra(ConstantUtils.POSITION));
                intents.putExtra(ConstantUtils.DELETE, "yes");
                setResult(RESULT_OK, intents);
                finish();


                break;

            case R.id.layout_drop_down:
            /*    PopupWindow popupWindow=new PopupWindow();
                LayoutInflater inflater = (LayoutInflater) AddPriceOptionActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.popup_layout, null);*/
                PopupMenu popup = new PopupMenu(this, editTextDays);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu_price_option, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        txtInFormat.setText(item.getTitle() + "(s)");
                        timeUnitOfMeasure = item.getTitle().toString().toLowerCase();
                        return true;
                    }
                });

                popup.show(); //showing popup menu

                break;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onTextChange(String text, View view) {

        if (!priceAmountEditText.getText().toString().isEmpty() && !editTextDays.getText().toString().isEmpty() && !editTextPriceOptionDescription.getText().toString().trim().isEmpty()) {

            addPriceButton.setSelected(true);
            addPriceButton.setBackground(ContextCompat.getDrawable(AddPriceOptionActivity.this, R.drawable.bg_bottom_corner_color_accent));

        }else {
            addPriceButton.setSelected(false);
            addPriceButton.setBackground(ContextCompat.getDrawable(AddPriceOptionActivity.this, R.drawable.btn_seletor_rounded));
        }

    }
}
