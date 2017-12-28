package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.BecomeASellerActivityDetails;
import com.pointters.model.ChatServiceDetails;
import com.pointters.utils.AppUtils;
import com.pointters.utils.DividerItemDecorationVer;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pointters.R.id.view;


public class BecomeASellerActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    SellerAdapter sellerAdapter;
    ArrayList<BecomeASellerActivityDetails> becomeASellerActivityDetailsArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_aseller);
        init();
        AppUtils.setToolBarWithBothIcon(BecomeASellerActivity.this, getResources().getString(R.string.become_a_seller),
                R.drawable.back_icon_grey, 0);

        BecomeASellerActivityDetails becomeASellerActivityDetails1=new BecomeASellerActivityDetails();
        becomeASellerActivityDetails1.setSellerHeading("Add Service");
        becomeASellerActivityDetails1.setSellerSubHeading("Status: 0 Added");
        becomeASellerActivityDetailsArrayList.add(becomeASellerActivityDetails1);


        BecomeASellerActivityDetails becomeASellerActivityDetails2=new BecomeASellerActivityDetails();
        becomeASellerActivityDetails2.setSellerHeading("Payment Setup");
        becomeASellerActivityDetails2.setSellerSubHeading("Status: No payment method added yet");
        becomeASellerActivityDetailsArrayList.add(becomeASellerActivityDetails2);


        BecomeASellerActivityDetails becomeASellerActivityDetails3=new BecomeASellerActivityDetails();
        becomeASellerActivityDetails3.setSellerHeading("Background Check");
        becomeASellerActivityDetails3.setSellerSubHeading("Status: Not Started");
        becomeASellerActivityDetails3.setSellerSubHeading1("Required for local service at your store location");
        becomeASellerActivityDetailsArrayList.add(becomeASellerActivityDetails3);

        BecomeASellerActivityDetails becomeASellerActivityDetails4=new BecomeASellerActivityDetails();
        becomeASellerActivityDetails4.setSellerHeading("Business Verification");
        becomeASellerActivityDetails4.setSellerSubHeading("Status: Not Started");
        becomeASellerActivityDetails4.setSellerSubHeading1("Required for local service at your store location");
        becomeASellerActivityDetailsArrayList.add(becomeASellerActivityDetails4);

    }


 //=============================Ui================================================================
    public void init(){

        recyclerView= (RecyclerView) findViewById(R.id.mBecomeSellerRecyclerView);
        LinearLayoutManager layoutManager2 =new LinearLayoutManager(BecomeASellerActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager2);
        sellerAdapter=new SellerAdapter(BecomeASellerActivity.this,becomeASellerActivityDetailsArrayList);
        recyclerView.setAdapter(sellerAdapter);

        DividerItemDecorationVer divider2 = new DividerItemDecorationVer(ContextCompat.getDrawable(this, R.drawable.divider_option));
        recyclerView.addItemDecoration(divider2);
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

        }
    }

//=======================Become A Seller Adapter==================================================

  public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.MyViewHolder>{

      private ArrayList<BecomeASellerActivityDetails> seller_options;
      private Context context;

      public SellerAdapter(Context context, ArrayList<BecomeASellerActivityDetails> seller_options) {
          this.context = context;
          this.seller_options = seller_options;

      }


      @Override
      public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.become_seller_item, parent, false);
          return new MyViewHolder(view);
      }

      @Override
      public void onBindViewHolder(MyViewHolder holder, int position) {
            if(seller_options.get(position).getSellerHeading()!=null)
          holder.seller_heading.setText(seller_options.get(position).getSellerHeading());
          else holder.seller_heading.setVisibility(View.GONE);
          if(seller_options.get(position).getSellerSubHeading()!=null)
          holder.seller_subHeading.setText(seller_options.get(position).getSellerSubHeading());
          else holder.seller_subHeading.setVisibility(View.GONE);
          if(seller_options.get(position).getSellerSubHeading1()!=null)
          holder.seller_subHeading1.setText(seller_options.get(position).getSellerSubHeading1());
          else holder.seller_subHeading1.setVisibility(View.GONE);


      }

      @Override
      public int getItemCount() {
          return seller_options.size();
      }

      public class MyViewHolder extends RecyclerView.ViewHolder {

          TextView seller_heading,seller_subHeading,seller_subHeading1;

          public MyViewHolder(View itemView) {
              super(itemView);

              seller_heading= (TextView) itemView.findViewById(R.id.mAddService);
              seller_subHeading= (TextView) itemView.findViewById(R.id.mStatus);
              seller_subHeading1= (TextView) itemView.findViewById(R.id.subheading2);
          }
      }
  }



}
