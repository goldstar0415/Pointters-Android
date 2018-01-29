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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout layoutUnit, itemHour, itemDay, itemWeek;
    private ImageView imgDown, imgHour, imgDay, imgWeek;
    private Prices prices;
    private String timeUnitOfMeasure = "day";
    private String currencyCode = "USD";
    private String currencySymbol = "$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price_option);

        initViews();

        layoutEdtSearch = (RelativeLayout) findViewById(R.id.layout_edt_search);
        layoutEdtSearch.setOnClickListener(this);

        setOnClick();
        setEditTextListener();

        if (getIntent() != null && getIntent().getSerializableExtra(ConstantUtils.PRICE) != null) {
            prices = (Prices) getIntent().getSerializableExtra(ConstantUtils.PRICE);
            if (prices.getPrice() != null && prices.getPrice() > 0) {
                priceAmountEditText.setText(String.valueOf(prices.getPrice()));
            }
            if (prices.getDescription() != null && !prices.getDescription().isEmpty()) {
                editTextPriceOptionDescription.setText(prices.getDescription());
            }
            if (prices.getTime() != null && prices.getTime() > 0) {
                editTextDays.setText(String.valueOf(prices.getTime()));
            }
            if (prices.getTimeUnitOfMeasure() != null && !prices.getTimeUnitOfMeasure().isEmpty()) {
                if (prices.getTimeUnitOfMeasure().equals("hour")) {
                    setPaidTime(0);
                } else if (prices.getTimeUnitOfMeasure().equals("week")) {
                    setPaidTime(2);
                } else {
                    setPaidTime(1);
                }
            }
            addPriceButton.setText(getResources().getString(R.string.save).toUpperCase());

            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.edit_price_option_header), R.drawable.back_icon_grey, R.drawable.delete_icon);
        } else {
            AppUtils.setToolBarWithBothIcon(AddPriceOptionActivity.this, getResources().getString(R.string.add_price_option_header), R.drawable.back_icon_grey, 0);
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
        itemHour.setOnClickListener(this);
        itemDay.setOnClickListener(this);
        itemWeek.setOnClickListener(this);
    }

    private void initViews() {
        editTextDays = (EditText) findViewById(R.id.edt_how_many_days);
        editTextPriceOptionDescription = (EditText) findViewById(R.id.edit_price_option_desc);
        addPriceButton = (TextView) findViewById(R.id.save_pricing_btn);
        txtInFormat = (TextView) findViewById(R.id.txt_in_date_format);
        priceAmountEditText = (EditText) findViewById(R.id.edt_price_amount);
        layoutDropDown=(RelativeLayout)findViewById(R.id.layout_drop_down);
        imgDown = (ImageView) findViewById(R.id.img_drop_down);

        layoutUnit = (LinearLayout) findViewById(R.id.layout_worktime_unit);
        layoutUnit.setVisibility(View.GONE);

        itemHour = (LinearLayout) findViewById(R.id.layout_hour_item);
        itemDay = (LinearLayout) findViewById(R.id.layout_day_item);
        itemWeek = (LinearLayout) findViewById(R.id.layout_week_item);

        imgHour = (ImageView) findViewById(R.id.img_hour_item);
        imgDay = (ImageView) findViewById(R.id.img_day_item);
        imgWeek = (ImageView) findViewById(R.id.img_week_item);
    }

    private void setPaidTime(int index) {
        imgHour.setVisibility(View.INVISIBLE);
        imgDay.setVisibility(View.INVISIBLE);
        imgWeek.setVisibility(View.INVISIBLE);

        switch (index) {
            case 0:
                imgHour.setVisibility(View.VISIBLE);
                txtInFormat.setText("Hour(s)");
                timeUnitOfMeasure = "hour";
                break;
            case 1:
                imgDay.setVisibility(View.VISIBLE);
                txtInFormat.setText("Day(s)");
                timeUnitOfMeasure = "day";
                break;
            case 2:
                imgWeek.setVisibility(View.VISIBLE);
                txtInFormat.setText("Week(s)");
                timeUnitOfMeasure = "week";
                break;
            default:
                break;
        }

        layoutUnit.setVisibility(View.GONE);
        imgDown.setImageResource(R.drawable.down_arrow);
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
                if (addPriceButton.isSelected()) {
                    prices = new Prices(editTextPriceOptionDescription.getText().toString(), Integer.parseInt(priceAmountEditText.getText().toString()), Integer.parseInt(editTextDays.getText().toString()), timeUnitOfMeasure, currencySymbol, currencyCode);
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
                imgDown.setImageResource(R.drawable.up_arrow);
                layoutUnit.setVisibility(View.VISIBLE);
                break;

            case R.id.layout_hour_item:
                setPaidTime(0);
                break;

            case R.id.layout_day_item:
                setPaidTime(1);
                break;

            case R.id.layout_week_item:
                setPaidTime(2);
                break;

            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onTextChange(String text, View view) {
        String strPrice = priceAmountEditText.getText().toString();
        String strDays = editTextDays.getText().toString();
        String strDesc = editTextPriceOptionDescription.getText().toString().trim();
        if (!strPrice.isEmpty() && Integer.parseInt(strPrice) > 0 && !strDays.isEmpty() && Integer.parseInt(strDays) > 0 && !strDesc.isEmpty()) {
            addPriceButton.setSelected(true);
            addPriceButton.setEnabled(true);

        } else {
            addPriceButton.setSelected(false);
            addPriceButton.setEnabled(false);
        }
    }
}
