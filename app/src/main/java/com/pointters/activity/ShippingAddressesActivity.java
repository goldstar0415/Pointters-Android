package com.pointters.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.StoreLocationModel;
import com.pointters.model.response.GetStoreLocationResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ShippingAddressesActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {

    private static int REQUEST_EDIT_STORELOCATION = 1;
    private static int REQUEST_ENTER_STORELOCATION = 2;

    ArrayList<StoreLocationModel> shippingAddress = new ArrayList<>();
    RecyclerView recyclerShippingAddress;
    private RelativeLayout addNewAddress;
    private RelativeLayout editShippingAddresses;
    private OnRecyclerViewItemClickListener listener;
    Adapter addressAdapter;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private StoreLocationModel selectedStoreLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address_list);
        AppUtils.setToolBarWithBothIcon(ShippingAddressesActivity.this, getResources().getString(R.string.shipping_addresses), R.drawable.back_icon, 0);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        recyclerShippingAddress = (RecyclerView) findViewById(R.id.recycler_shipping_address);
        addNewAddress = (RelativeLayout) findViewById(R.id.btn_new_address);
        editShippingAddresses = (RelativeLayout) findViewById(R.id.btn_edt_address);
        addNewAddress.setOnClickListener(this);
        editShippingAddresses.setOnClickListener(this);

        addressAdapter = new Adapter(this, R.layout.adapter_shipment_address, shippingAddress, this);
        recyclerShippingAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerShippingAddress.setAdapter(addressAdapter);

        CallGetShipmentAddresses();

    }

    public void CallGetShipmentAddresses() {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetStoreLocationResponse> callGetCategoryApi = apiService.getShipmentAddresses(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        callGetCategoryApi.enqueue(new Callback<GetStoreLocationResponse>() {
            @Override
            public void onResponse(Call<GetStoreLocationResponse> call, Response<GetStoreLocationResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    ArrayList<StoreLocationModel> models = response.body().getDocs();
                    shippingAddress.clear();
                    shippingAddress.addAll(models);
                    addressAdapter.setData(shippingAddress);
                } else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ShippingAddressesActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ShippingAddressesActivity.this);
                } else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetStoreLocationResponse> call, Throwable t) {
                if (loader.isShowing()) loader.dismiss();
                Toast.makeText(ShippingAddressesActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                if (selectedStoreLocation != null) {
                    Intent i = new Intent();
                    Gson gson = new Gson();
                    i.putExtra(ConstantUtils.ADD_ADDRESS, gson.toJson(selectedStoreLocation));
                    setResult(RESULT_OK, i);
                    finish();
                }else{
                    Intent i = new Intent();
                    setResult(RESULT_CANCELED, i);
                    finish();
                }
                break;

            case R.id.btn_new_address:
                Intent intent = new Intent(ShippingAddressesActivity.this, EnterShippingAddressesActivity.class);
                startActivityForResult(intent, REQUEST_ENTER_STORELOCATION);
                break;

            case R.id.btn_edt_address:
                Intent intent1 = new Intent(ShippingAddressesActivity.this, EnterShippingAddressesActivity.class);
                intent1.putExtra("storeLocationId", selectedStoreLocation.get_id());
                startActivityForResult(intent1, REQUEST_EDIT_STORELOCATION);
                break;

        }
    }

    @Override
    public void onItemClick(int position) {
        selectedStoreLocation = shippingAddress.get(position);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            CallGetShipmentAddresses();
        }
    }

    @Override
    public void onApiFail(String apiSource) {

    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<StoreLocationModel> Al;
        LayoutInflater inflater;
        OnRecyclerViewItemClickListener listener;
        ArrayList<Boolean> selecteList;
        StoreLocationModel selectedLocation = new StoreLocationModel();
        Adapter(Activity obj, int res, ArrayList<StoreLocationModel> Al, OnRecyclerViewItemClickListener listener) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
            this.listener = listener;
            this.selecteList = new ArrayList<Boolean>(Collections.nCopies(Al.size(), false));
        }

        public void setData(ArrayList<StoreLocationModel> models) {
            this.Al = models;
            this.selecteList = new ArrayList<Boolean>(Collections.nCopies(Al.size(), false));
            notifyDataSetChanged();
        }

        @Override
        public Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Adapter.MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new Adapter.MyHolder(vv);
            return hold;

        }

        public StoreLocationModel getSelected(){
            return selectedLocation;
        }

        @Override
        public void onBindViewHolder(Adapter.MyHolder hold, final int position) {
            StoreLocationModel model = Al.get(position);
            hold.imgView.setImageResource(R.drawable.location_pin_icon);
            hold.title.setText(model.getName());
            hold.subtitle.setText(model.getFullAddress());
            if (selecteList.get(position)) {
                hold.checkicon.setImageResource(R.drawable.icon_check);
                hold.checkicon.setVisibility(View.VISIBLE);
            }else{
                hold.checkicon.setVisibility(View.INVISIBLE);
            }
            hold.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selecteList = new ArrayList<Boolean>(Collections.nCopies(Al.size(), false));
                    selecteList.remove(position);
                    selecteList.add(position, true);
                    selectedLocation = model;
                    notifyDataSetChanged();
                    listener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Al.size();
        }

        //holder
        public class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView subtitle;
            ImageView imgView;
            ImageView checkicon;


            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txt_title);
                subtitle = (TextView) itemView.findViewById(R.id.txt_subtitle);
                imgView = (ImageView) itemView.findViewById(R.id.img_payment);
                checkicon = (ImageView) itemView.findViewById(R.id.right_icon);
            }
        }
    }
}