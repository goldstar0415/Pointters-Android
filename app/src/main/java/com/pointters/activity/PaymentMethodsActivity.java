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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.CardModel;
import com.pointters.model.ChatServiceDetails;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PaymentMethodsActivity extends AppCompatActivity implements View.OnClickListener{


    //PaymentAdapter paymentAdapter;
    ArrayList<CardModel> paymentArray = new ArrayList<>();
    ArrayList<CardModel> cardsArray = new ArrayList<>();
    RecyclerView recyclerPayment;
    RecyclerView recyclerCards;
    private Button addCardButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        AppUtils.setToolBarWithBothIcon(PaymentMethodsActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon, R.drawable.icon_edit_gray);

        recyclerPayment = (RecyclerView) findViewById(R.id.recycler_payment);
        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);
        addCardButton= (Button) findViewById(R.id.btn_add_card);
        addCardButton.setOnClickListener(this);

        paymentArray.add(new CardModel("Paypal", "user@example.com", R.drawable.icon_paypal, true));
        paymentArray.add(new CardModel("Apple Pay", "user@example.com", R.drawable.icon_apple_pay, false));
        cardsArray.add(new CardModel("Chase Bank", "**** 6753", R.drawable.icon_master_card, true));
        cardsArray.add(new CardModel("Wells Fargo Chicago", "**** 8953", R.drawable.icon_apple_pay, false));

        Adapter paymentAdapter = new Adapter(this, R.layout.adapter_payment_view, paymentArray);
        recyclerPayment.setLayoutManager(new LinearLayoutManager(this));
        recyclerPayment.setAdapter(paymentAdapter);

        Adapter cardAdapter = new Adapter(this, R.layout.adapter_payment_view, cardsArray);
        recyclerCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerCards.setAdapter(cardAdapter);


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

            case R.id.btn_add_card:
                Intent intent = new Intent(PaymentMethodsActivity.this, AddPaymentActivity.class);
                startActivity(intent);
                break;

        }
    }
//======================================PaymentAdapter============================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<CardModel> Al;
        LayoutInflater inflater;

        Adapter(Activity obj, int res, ArrayList<CardModel> Al) {
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
            CardModel model = Al.get(position);
            hold.imgView.setImageResource(model.getImageRes());
            hold.title.setText(model.getCardName());
            hold.subtitle.setText(model.getCardDetail());
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


            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txt_title);
                subtitle = (TextView) itemView.findViewById(R.id.txt_subtitle);
                imgView = (ImageView) itemView.findViewById(R.id.img_payment);
            }
        }
    }
}