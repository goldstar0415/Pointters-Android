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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.ChooseCategoryAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.AddressModel;
import com.pointters.model.CategoryModel;
import com.pointters.model.StoreLocationModel;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.model.response.GetStoreLocationResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class StoreLocationsActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {

    private static int REQUEST_EDIT_STORELOCATION = 1;
    private static int REQUEST_ENTER_STORELOCATION = 2;
    ArrayList<StoreLocationModel> storeLocations = new ArrayList<>();
    RecyclerView recyclerShippingAddress;
    private Button addNewAddress;
    private OnRecyclerViewItemClickListener listener;
    Adapter addressAdapter;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private StoreLocationModel selectedStoreLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locations_list);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AppUtils.setToolBarWithBothIcon(StoreLocationsActivity.this, getResources().getString(R.string.store_location), R.drawable.back_icon, R.drawable.icon_edit_gray);
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        recyclerShippingAddress = (RecyclerView) findViewById(R.id.recycler_store_location);
        addNewAddress= (Button) findViewById(R.id.btn_new_store_location);
        addNewAddress.setOnClickListener(this);

        addressAdapter = new Adapter(this, R.layout.adapter_payment_view, storeLocations, this);
        recyclerShippingAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerShippingAddress.setAdapter(addressAdapter);
        CallGetStoreLocations();
    }

    public void CallGetStoreLocations() {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetStoreLocationResponse> callGetCategoryApi = apiService.getStores(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        callGetCategoryApi.enqueue(new Callback<GetStoreLocationResponse>() {
            @Override
            public void onResponse(Call<GetStoreLocationResponse> call, Response<GetStoreLocationResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    ArrayList<StoreLocationModel> models = response.body().getDocs();
                    storeLocations.clear();
                    storeLocations.addAll(models);
                    addressAdapter.setData(storeLocations);
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(StoreLocationsActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(StoreLocationsActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetStoreLocationResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(StoreLocationsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

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
                if (selectedStoreLocation != null) {
                    Intent intent1 = new Intent(StoreLocationsActivity.this, EnterStoreLocationActivity.class);
                    intent1.putExtra("storeLocationId", selectedStoreLocation.get_id());
                    startActivityForResult(intent1, REQUEST_EDIT_STORELOCATION);

                }
                break;

            case R.id.btn_new_store_location:
                Intent intent = new Intent(StoreLocationsActivity.this, EnterStoreLocationActivity.class);
                startActivityForResult(intent, REQUEST_ENTER_STORELOCATION);
                break;

        }
    }

    @Override
    public void onItemClick(int position) {
        for(int i = 0; i < storeLocations.size(); i++){
            StoreLocationModel model = storeLocations.get(i);
            if (position == i){
                model.setSelected(true);
            }else{
                model.setSelected(false);
            }
        }
        selectedStoreLocation = storeLocations.get(position);
        addressAdapter.notifyDataSetChanged();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            CallGetStoreLocations();
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

        Adapter(Activity obj, int res, ArrayList<StoreLocationModel> Al, OnRecyclerViewItemClickListener listener) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
            this.listener = listener;
        }

        public void setData(ArrayList<StoreLocationModel> models){
            this.Al = models;
            notifyDataSetChanged();
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new MyHolder(vv);
            return hold;

        }

        @Override
        public void onBindViewHolder(MyHolder hold, final int position) {
            StoreLocationModel model = Al.get(position);
            hold.imgView.setImageResource(R.drawable.location_icon_grey);
            hold.title.setText(model.getName());
            hold.subtitle.setText(model.getFullAddress());
            if (model.isSelected()) {
                hold.checkicon.setImageResource(R.drawable.icon_check);
                hold.checkicon.setVisibility(View.VISIBLE);
            }else{
                hold.checkicon.setVisibility(View.INVISIBLE);
            }
            hold.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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