package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.ChatServiceDetails;
import com.pointters.model.SellerCancelOrderDetails;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;


public class PaymentMethodsActivity extends AppCompatActivity implements View.OnClickListener{


    //PaymentAdapter paymentAdapter;
    ArrayList<ChatServiceDetails> chatServiceDetailsArrayList = new ArrayList<>();
    RecyclerView gridViewPaymentMethode;
    private RelativeLayout addAPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        AppUtils.setToolBarWithBothIcon(PaymentMethodsActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon_grey, 0);

        gridViewPaymentMethode = (RecyclerView) findViewById(R.id.mPaymentMethodGridView);
        addAPayment= (RelativeLayout) findViewById(R.id.add_a_payment_view);
       addAPayment.setOnClickListener(this);

        Adapter obj = new Adapter(this, R.layout.payment_details_option,chatServiceDetailsArrayList );
        gridViewPaymentMethode.setLayoutManager(new LinearLayoutManager(this));
        gridViewPaymentMethode.setAdapter(obj);
        /*GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        gridViewPaymentMethode.setLayoutManager(mGridLayoutManager);*/

        /*DividerItemDecoration divider2 = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider2.setDrawable(ContextCompat.getDrawable(this, R.drawable.bg_grid));
        gridViewPaymentMethode.addItemDecoration(divider2);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;
            case R.id.add_a_payment_view:
                startActivity(new Intent(PaymentMethodsActivity.this,AddCreditCardActivity.class));
                break;
        }
    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<ChatServiceDetails> Al;
        LayoutInflater inflater;

        Adapter(Activity obj, int res, ArrayList<ChatServiceDetails> Al) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);


        }


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder hold;


            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new MyHolder(vv);


            return hold;

        }

        @Override
        public void onBindViewHolder(MyHolder hold, int position) {


        }

        @Override
        public int getItemCount() {
            return 2;

        }

        //holder
        public class MyHolder extends RecyclerView.ViewHolder {
            TextView iv;


            public MyHolder(View itemView) {
                super(itemView);
                iv = (TextView) itemView.findViewById(R.id.mPaymentCardName);
            }
        }
    }
}
