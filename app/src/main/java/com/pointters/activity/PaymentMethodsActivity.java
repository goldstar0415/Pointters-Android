package com.pointters.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.BraintreePaymentResultListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCallback;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.interfaces.PaymentMethodNoncesUpdatedListener;
import com.braintreepayments.api.models.AndroidPayConfiguration;
import com.braintreepayments.api.models.BraintreePaymentResult;
import com.braintreepayments.api.models.CardConfiguration;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalConfiguration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.model.CardModel;
import com.pointters.model.ClientTokenModel;
import com.pointters.model.PaymentMethodNonceString;
import com.pointters.model.PaymentMethodResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PaymentMethodsActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        PaymentMethodNonceCreatedListener, BraintreeCancelListener, PaymentMethodNoncesUpdatedListener,
        BraintreePaymentResultListener, ConfigurationListener,
        BraintreeErrorListener, PaymentMethodNonceCallback{
    private static final int DROP_IN_REQUEST = 1;


    //PaymentAdapter paymentAdapter;
    ArrayList<PaymentMethodNonce> paymentNonceArray = new ArrayList<>();
    ArrayList<PaymentMethodNonce> cardsNonceArray = new ArrayList<>();
    ArrayList<CardModel> paymentArray = new ArrayList<>();
    ArrayList<CardModel> cardsArray = new ArrayList<>();
    RecyclerView recyclerPayment;
    RecyclerView recyclerCards;
    private Button addCardButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;
    Adapter paymentAdapter, cardAdapter;
    String clientToken;
    protected BraintreeFragment mBraintreeFragment;
    private int REQUEST_ADD_CARD = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        AppUtils.setToolBarWithBothIcon(PaymentMethodsActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon, R.drawable.icon_edit_gray);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(PaymentMethodsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        recyclerPayment = (RecyclerView) findViewById(R.id.recycler_payment);
        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);
        addCardButton= (Button) findViewById(R.id.btn_add_card);
        addCardButton.setOnClickListener(this);

//        paymentArray.add(new CardModel("Paypal", "user@example.com", R.drawable.icon_paypal, true));
//        paymentArray.add(new CardModel("Apple Pay", "user@example.com", R.drawable.icon_apple_pay, false));
//        cardsArray.add(new CardModel("Chase Bank", "**** 6753", R.drawable.icon_master_card, true));
//        cardsArray.add(new CardModel("Wells Fargo Chicago", "**** 8953", R.drawable.icon_apple_pay, false));

        paymentAdapter = new Adapter(this, R.layout.adapter_payment_view, paymentArray);
        recyclerPayment.setLayoutManager(new LinearLayoutManager(this));
        recyclerPayment.setAdapter(paymentAdapter);

        cardAdapter = new Adapter(this, R.layout.adapter_payment_view, cardsArray);
        recyclerCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerCards.setAdapter(cardAdapter);
        getClientToken();

    }

    public void getClientToken(){

        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<ClientTokenModel> putUserSetting = apiService.getBrainTreeClientToken(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        putUserSetting.enqueue(new Callback<ClientTokenModel>() {
            @Override
            public void onResponse(Call<ClientTokenModel> call, Response<ClientTokenModel> response) {
                loader.dismiss();
                if (response.code() == 200) {
                    ClientTokenModel object = response.body();
                    if (object.isSuccess()){
                        assert object != null;
                        clientToken = object.getClientToken();
                    }
                    setupPayment();
                } else if (response.code() == 400) {
                    AndroidUtils.showToast(PaymentMethodsActivity.this, "Please choose atleast one option");
                }


            }

            @Override
            public void onFailure(Call<ClientTokenModel> call, Throwable t) {
                loader.dismiss();

            }
        });

    }

    public void setupPayment(){
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, clientToken);
            getPaymentMethods();
        } catch (InvalidArgumentException e) {
            onError(e);
        }

    }

    public void getPaymentMethods() {
        PaymentMethod.getPaymentMethodNonces(mBraintreeFragment, true);
    }
    public void launchDropIn() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        enableGooglePay(dropInRequest);
        startActivityForResult(dropInRequest.getIntent(this), DROP_IN_REQUEST);
    }


    private void enableGooglePay(DropInRequest dropInRequest) {
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("1.00")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .setCurrencyCode("USD")
                        .build());

        dropInRequest.googlePaymentRequest(googlePaymentRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_CARD) {
                PaymentMethod.getPaymentMethodNonces(mBraintreeFragment);
//                displayNonce(result.getPaymentMethodNonce(), result.getDeviceData());
            }
        }
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
                intent.putExtra("ClientToken", clientToken);
                startActivityForResult(intent, REQUEST_ADD_CARD);
//                launchDropIn();
//                if (mBraintreeFragment != null) {
//                    PayPal.authorizeAccount(mBraintreeFragment);
//                }

                break;

        }
    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onBraintreePaymentResult(BraintreePaymentResult result) {

        Log.e("braintreePaymentResult", "result");
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        loader.show();
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<PaymentMethodResponse> putUserSetting = apiService.postBrainTreePaymentMethod(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), new PaymentMethodNonceString(paymentMethodNonce.getNonce()));
        putUserSetting.enqueue(new Callback<PaymentMethodResponse>() {
            @Override
            public void onResponse(Call<PaymentMethodResponse> call, Response<PaymentMethodResponse> response) {
                if (response.code() == 200) {

                } else if (response.code() == 400) {
                    AndroidUtils.showToast(PaymentMethodsActivity.this, "Please choose atleast one option");
                }


            }

            @Override
            public void onFailure(Call<PaymentMethodResponse> call, Throwable t) {
                loader.dismiss();

            }
        });

    }


    @Override
    public void onConfigurationFetched(Configuration configuration) {
        paymentArray.clear();
        cardsArray.clear();
        if (configuration.getCardConfiguration() != null) {
            CardConfiguration cardConfiguration = configuration.getCardConfiguration();
            List<String> cardTypes = new ArrayList<>();
//            cardTypes.addAll(cardConfiguration.getSupportedCardTypes());
//            for (String s : cardTypes) {
//                CardModel model = new CardModel();
//                model.setCardName(s);
//                if (s.equals("Visa")) {
//                    model.setImageRes(R.drawable.bt_ic_vaulted_visa);
//                    model.setCardDetail(CardType.VISA.getPattern().pattern());
//                }else if (s.equals("JCB")) {
//                    model.setImageRes(R.drawable.bt_ic_vaulted_jcb);
//                    model.setCardDetail(CardType.JCB.getPattern().pattern());
//                }else if (s.equals("MasterCard")) {
//                    model.setImageRes(R.drawable.bt_ic_vaulted_mastercard);
//                    model.setCardDetail(CardType.MASTERCARD.getPattern().pattern());
//                }else if (s.equals("American Express")) {
//                    model.setImageRes(R.drawable.bt_ic_vaulted_amex);
//                    model.setCardDetail(CardType.AMEX.getPattern().pattern());
//                }else if (s.equals("Discover")) {
//                    model.setImageRes(R.drawable.bt_ic_vaulted_discover);
//                    model.setCardDetail(CardType.DISCOVER.getPattern().pattern());
//                }else{
//                    model.setImageRes(R.drawable.bt_ic_vaulted_unknown);
//                }
//                cardsArray.add(model);
//            }
//            cardAdapter.notifyDataSetChanged();
        }
        if (configuration.getPayPal() != null) {
            PayPalConfiguration payPalConfiguration = configuration.getPayPal();
            paymentArray.add(new CardModel(payPalConfiguration.getDisplayName(), payPalConfiguration.getEnvironment(), R.drawable.bt_ic_paypal, false));
        }
        if (configuration.getAndroidPay() != null) {
            AndroidPayConfiguration androidPayConfiguration = configuration.getAndroidPay();
            paymentArray.add(new CardModel(androidPayConfiguration.getDisplayName(), androidPayConfiguration.getEnvironment(), R.drawable.bt_ic_android_pay, false));
        }
        paymentAdapter.notifyDataSetChanged();
    }

    @Override
    public void success(PaymentMethodNonce paymentMethodNonce) {
        Log.e("paymentMethodNonce: ", paymentMethodNonce.getTypeLabel());
    }

    @Override
    public void failure(Exception exception) {

    }

    @Override
    public void onPaymentMethodNoncesUpdated(List<PaymentMethodNonce> paymentMethodNonces) {
        Log.e("nonce: ", String.valueOf(paymentMethodNonces.size()));
        cardsArray.clear();
        for (PaymentMethodNonce paymentMethodNonce: paymentMethodNonces) {
            if (paymentMethodNonce instanceof PayPalAccountNonce) {
                if (paymentMethodNonce.isDefault()) {
                    paymentNonceArray.add(paymentMethodNonce);
                }
            }else if (paymentMethodNonce instanceof CardNonce) {
                cardsNonceArray.add(paymentMethodNonce);
            }
        }
        for (PaymentMethodNonce payNonce: paymentNonceArray) {
            if (payNonce instanceof  PayPalAccountNonce) {
                PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) payNonce;
                CardModel model = paymentArray.get(0);
                model.setCardName(payPalAccountNonce.getTypeLabel());
                model.setCardDetail(payPalAccountNonce.getEmail());
                paymentArray.remove(0);
                paymentArray.add(0, model);
            }
        }

        for (PaymentMethodNonce nonce: cardsNonceArray) {
            if (nonce instanceof CardNonce) {
                CardNonce cardNonce = (CardNonce) nonce;
                CardModel model = new CardModel();
                model.setCardName(cardNonce.getCardType());
                if (cardNonce.getCardType().equals("Visa")) {
                    model.setImageRes(R.drawable.visa_on);
                }else if (cardNonce.getCardType().equals("JCB")) {
                    model.setImageRes(R.drawable.jcb_on);
                }else if (cardNonce.getCardType().equals("MasterCard")) {
                    model.setImageRes(R.drawable.master_on);
                }else if (cardNonce.getCardType().equals("American Express")) {
                    model.setImageRes(R.drawable.amex_on);
                }else if (cardNonce.getCardType().equals("Discover")) {
                    model.setImageRes(R.drawable.discover_on);
                }else{
                    model.setImageRes(R.drawable.bt_ic_vaulted_unknown);
                }
                model.setCardDetail(String.format("**** %s", cardNonce.getLastFour()));
                cardsArray.add(model);
            }
        }
        paymentAdapter.notifyDataSetChanged();
        cardAdapter.notifyDataSetChanged();
        loader.dismiss();
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