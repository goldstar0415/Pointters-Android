package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.AddressModel;
import com.pointters.model.CardModel;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ShippingAddressesActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener{


    //PaymentAdapter paymentAdapter;
    ArrayList<AddressModel> shippingAddress = new ArrayList<>();
    RecyclerView recyclerShippingAddress;
    private Button addNewAddress;
    private OnRecyclerViewItemClickListener listener;
    Adapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address_list);
        AppUtils.setToolBarWithBothIcon(ShippingAddressesActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon, R.drawable.icon_edit_gray);

        recyclerShippingAddress = (RecyclerView) findViewById(R.id.recycler_shipping_address);
        addNewAddress= (Button) findViewById(R.id.btn_new_address);
        addNewAddress.setOnClickListener(this);

        shippingAddress.add(new AddressModel("Home", "", "", "", "", "", true, "123, Lake Shore..."));
        shippingAddress.add(new AddressModel("Office", "", "", "", "", "", false, "47B, Kingswood..."));
        shippingAddress.add(new AddressModel("Apartment", "", "", "", "", "", false, "Peter Lane, 45..."));

        addressAdapter = new Adapter(this, R.layout.adapter_payment_view, shippingAddress, this);
        recyclerShippingAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerShippingAddress.setAdapter(addressAdapter);



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

            case R.id.btn_new_address:
                Intent intent = new Intent(ShippingAddressesActivity.this, AddShippingAddressesActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onItemClick(int position) {
        for(int i = 0; i < shippingAddress.size(); i++){
            AddressModel model = shippingAddress.get(i);
            if (position == i){
                model.setDefault(true);
            }else{
                model.setDefault(false);
            }
        }
        addressAdapter.notifyDataSetChanged();
    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<AddressModel> Al;
        LayoutInflater inflater;
        OnRecyclerViewItemClickListener listener;

        Adapter(Activity obj, int res, ArrayList<AddressModel> Al, OnRecyclerViewItemClickListener listener) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
            this.listener = listener;
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
            AddressModel model = Al.get(position);
            hold.imgView.setImageResource(R.drawable.location_icon_grey);
            hold.title.setText(model.getName());
            hold.subtitle.setText(model.getFullAddress());
            if (model.isDefault()) {
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