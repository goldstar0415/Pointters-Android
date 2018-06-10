package com.pointters.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.AmericanExpress;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.ThreeDSecure;
import com.braintreepayments.api.UnionPay;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.AmericanExpressListener;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.BraintreePaymentResultListener;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.interfaces.UnionPayListener;
import com.braintreepayments.api.models.AmericanExpressRewardsBalance;
import com.braintreepayments.api.models.Authorization;
import com.braintreepayments.api.models.BinData;
import com.braintreepayments.api.models.BraintreePaymentResult;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.UnionPayCapabilities;
import com.braintreepayments.api.models.UnionPayCardBuilder;
import com.braintreepayments.cardform.OnCardFormFieldFocusedListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.pointters.R;
import com.pointters.model.CardModel;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class AddPaymentActivity extends AppCompatActivity implements View.OnClickListener, ConfigurationListener, UnionPayListener,
        PaymentMethodNonceCreatedListener, BraintreeErrorListener, OnCardFormSubmitListener,
        OnCardFormFieldFocusedListener, AmericanExpressListener, BraintreeCancelListener, BraintreePaymentResultListener {
    static final String EXTRA_PAYMENT_RESULT = "payment_result";
    static final String EXTRA_DEVICE_DATA = "device_data";
    static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";
    static final String EXTRA_ANDROID_PAY_CART = "android_pay_cart";

    protected String mAuthorization;
    protected String mCustomerId;
    protected BraintreeFragment mBraintreeFragment;

    private Button setDefaultButton;

    private static final String EXTRA_THREE_D_SECURE_REQUESTED = "com.braintreepayments.demo.EXTRA_THREE_D_SECURE_REQUESTED";
    private static final String EXTRA_UNIONPAY = "com.braintreepayments.demo.EXTRA_UNIONPAY";
    private static final String EXTRA_UNIONPAY_ENROLLMENT_ID = "com.braintreepayments.demo.EXTRA_UNIONPAY_ENROLLMENT_ID";

    private Configuration mConfiguration;
    private String mDeviceData;
    private boolean mIsUnionPay;
    private String mEnrollmentId;
    private boolean mThreeDSecureRequested;

    private ProgressDialog mLoading;
    private CardForm cardForm;
    private TextInputLayout mSmsCodeContainer;
    private EditText mSmsCode;
    private Button mSendSmsButton;
    private Button mPurchaseButton;

    private CardType mCardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_methods);
        AppUtils.setToolBarWithBothIcon(AddPaymentActivity.this, getResources().getString(R.string.payment_method), R.drawable.back_icon, R.drawable.icon_edit_gray);

        if (getIntent() != null) {
            Intent i = getIntent();
            mAuthorization = i.getStringExtra("ClientToken");
        }

        setDefaultButton= (Button) findViewById(R.id.btn_set_default);
        setDefaultButton.setOnClickListener(this);

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this);
        cardForm.setOnFormFieldFocusedListener(this);
        cardForm.setOnCardFormSubmitListener(this);

        mSmsCodeContainer = (TextInputLayout)findViewById(R.id.sms_code_container);
        mSmsCode = findViewById(R.id.sms_code);
        mSendSmsButton = findViewById(R.id.unionpay_enroll_button);
        mPurchaseButton = findViewById(R.id.purchase_button);

        if (savedInstanceState != null) {
            mThreeDSecureRequested = savedInstanceState.getBoolean(EXTRA_THREE_D_SECURE_REQUESTED);
            mIsUnionPay = savedInstanceState.getBoolean(EXTRA_UNIONPAY);
            mEnrollmentId = savedInstanceState.getString(EXTRA_UNIONPAY_ENROLLMENT_ID);

            if (mIsUnionPay) {
                mSendSmsButton.setVisibility(VISIBLE);
            }
        }

        onAuthorizationFetched();
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

        }
    }

    @Override
    public void onRewardsBalanceFetched(AmericanExpressRewardsBalance rewardsBalance) {
        safelyCloseLoadingView();
        showDialog(Integer.parseInt(getAmexRewardsBalanceString(rewardsBalance)));

    }

    private void safelyCloseLoadingView() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }


    private static String getDisplayString(BinData binData) {
        return null;
    }

    public static String getAmexRewardsBalanceString(AmericanExpressRewardsBalance rewardsBalance) {
        return  "Amex Rewards Balance: \n" +
                "- amount: " + rewardsBalance.getRewardsAmount() + "\n" +
                "- errorCode: " + rewardsBalance.getErrorCode();
    }

    @Override
    public void onError(Exception error) {

    }

    @Override
    public void onConfigurationFetched(Configuration configuration) {
        mConfiguration = configuration;

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(configuration.isCvvChallengePresent())
                .postalCodeRequired(configuration.isPostalCodeChallengePresent())
                .mobileNumberRequired(false)
                .actionLabel(getString(R.string.purchase))
                .setup(this);

        if (getIntent().getBooleanExtra(EXTRA_COLLECT_DEVICE_DATA, false)) {
            DataCollector.collectDeviceData(mBraintreeFragment, new BraintreeResponseListener<String>() {
                @Override
                public void onResponse(String deviceData) {
                    mDeviceData = deviceData;
                }
            });
        }

    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

            Intent intent = new Intent()
                    .putExtra(EXTRA_PAYMENT_RESULT, paymentMethodNonce);
            setResult(RESULT_OK, intent);
            finish();
//        }

    }

    @Override
    public void onCapabilitiesFetched(UnionPayCapabilities capabilities) {
        mSmsCodeContainer.setVisibility(GONE);
        mSmsCode.setText("");

        if (capabilities.isUnionPay()) {
            if (!capabilities.isSupported()) {
                cardForm.setCardNumberError(getString(R.string.bt_card_not_accepted));
                return;
            }
            mIsUnionPay = true;
            mEnrollmentId = null;

            cardForm.cardRequired(true)
                    .expirationRequired(true)
                    .cvvRequired(true)
                    .postalCodeRequired(mConfiguration.isPostalCodeChallengePresent())
                    .mobileNumberRequired(true)
                    .actionLabel(getString(R.string.purchase))
                    .setup(this);

            mSendSmsButton.setVisibility(VISIBLE);
        } else {
            mIsUnionPay = false;

            cardForm.cardRequired(true)
                    .expirationRequired(true)
                    .cvvRequired(mConfiguration.isCvvChallengePresent())
                    .postalCodeRequired(mConfiguration.isPostalCodeChallengePresent())
                    .mobileNumberRequired(false)
                    .actionLabel(getString(R.string.purchase))
                    .setup(this);

            if (!mConfiguration.isCvvChallengePresent()) {
                ((EditText) findViewById(R.id.bt_card_form_cvv)).setText("");
            }
        }

    }

    @Override
    public void onSmsCodeSent(String enrollmentId, boolean smsCodeRequired) {
        mEnrollmentId = enrollmentId;
        if (smsCodeRequired) {
            mSmsCodeContainer.setVisibility(VISIBLE);
        } else {
            onCardFormSubmit();
        }

    }

    @Override
    public void onCardFormFieldFocused(View field) {
        if (mBraintreeFragment == null) {
            return;
        }

        if (!(field instanceof CardEditText) && !TextUtils.isEmpty(cardForm.getCardNumber())) {
            CardType cardType = CardType.forCardNumber(cardForm.getCardNumber());
            if (mCardType != cardType) {
                mCardType  = cardType;

//                if (mConfiguration.getUnionPay().isEnabled() && !Authorization.isTokenizationKey(mAuthorization)) {
//                    UnionPay.fetchCapabilities(mBraintreeFragment, cardForm.getCardNumber());
//                }
            }
        }

    }

    @Override
    public void onCardFormSubmit() {

    }

    public void sendSms(View v) {
        UnionPayCardBuilder unionPayCardBuilder = new UnionPayCardBuilder()
                .cardNumber(cardForm.getCardNumber())
                .expirationMonth(cardForm.getExpirationMonth())
                .expirationYear(cardForm.getExpirationYear())
                .cvv(cardForm.getCvv())
                .postalCode(cardForm.getPostalCode())
                .mobileCountryCode(cardForm.getCountryCode())
                .mobilePhoneNumber(cardForm.getMobileNumber());

        UnionPay.enroll(mBraintreeFragment, unionPayCardBuilder);
    }

    public void onPurchase(View v) {
        setProgressBarIndeterminateVisibility(true);

        if (mIsUnionPay) {
            UnionPayCardBuilder unionPayCardBuilder = new UnionPayCardBuilder()
                    .cardNumber(cardForm.getCardNumber())
                    .expirationMonth(cardForm.getExpirationMonth())
                    .expirationYear(cardForm.getExpirationYear())
                    .cvv(cardForm.getCvv())
                    .postalCode(cardForm.getPostalCode())
                    .mobileCountryCode(cardForm.getCountryCode())
                    .mobilePhoneNumber(cardForm.getMobileNumber())
                    .smsCode(mSmsCode.getText().toString())
                    .enrollmentId(mEnrollmentId);

            UnionPay.tokenize(mBraintreeFragment, unionPayCardBuilder);
        } else {
            CardBuilder cardBuilder = new CardBuilder()
                    .cardNumber(cardForm.getCardNumber())
                    .expirationMonth(cardForm.getExpirationMonth())
                    .expirationYear(cardForm.getExpirationYear())
                    .cvv(cardForm.getCvv())
                    .postalCode(cardForm.getPostalCode());

            Card.tokenize(mBraintreeFragment, cardBuilder);
        }
    }

    protected void onAuthorizationFetched() {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
        } catch (InvalidArgumentException e) {
            onError(e);
        }

        mPurchaseButton.setEnabled(true);
    }


    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void onBraintreePaymentResult(BraintreePaymentResult result) {

    }
}