package com.pointters.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.ChatServiceDetails;
import com.pointters.model.SellerCancelOrderDetails;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class CancelOrderScreenActivity extends AppCompatActivity {

    ArrayList<SellerCancelOrderDetails> sellerCancelOrderDetailsArrayList=new ArrayList<>();
    RecyclerView sellerRecyclerView;
    CancelOrderAdapter cancelOrderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order_screen);
        sellerRecyclerView= (RecyclerView) findViewById(R.id.mReasonToCancelRecyclerView);
        sellerRecyclerView.setNestedScrollingEnabled(false);


        LinearLayoutManager layoutManager2 =new LinearLayoutManager(CancelOrderScreenActivity.this, LinearLayoutManager.VERTICAL,false);
        sellerRecyclerView.setLayoutManager(layoutManager2);
        cancelOrderAdapter=new CancelOrderAdapter(CancelOrderScreenActivity.this,sellerCancelOrderDetailsArrayList);
        sellerRecyclerView.setAdapter(cancelOrderAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

 // Adapter

    public class CancelOrderAdapter extends RecyclerView.Adapter<CancelOrderAdapter.MyViewHolder>{

        private ArrayList<SellerCancelOrderDetails> cancel_options;
        private Context context;

        public CancelOrderAdapter(Context context, ArrayList<SellerCancelOrderDetails> cancel_options) {
            this.context = context;
            this.cancel_options = cancel_options;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_order_list_details, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView cancel_orderList;

            public MyViewHolder(View view) {
                super(view);

                cancel_orderList= (TextView) view.findViewById(R.id.mUserName);

            }
        }
    }
}
