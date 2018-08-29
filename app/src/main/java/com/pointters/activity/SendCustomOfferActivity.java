package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.AddressModel;
import com.pointters.model.ChatServiceModel;
import com.pointters.model.CustomOfferModels;
import com.pointters.model.FulfillmentMethodForCustom1;
import com.pointters.model.LinkServiceModel;
import com.pointters.model.ParcelModel;
import com.pointters.model.UserChatModel;
import com.pointters.model.request.SendCustomOfferRequest;
import com.pointters.model.response.GetCustomOfferDetailsResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.SquareImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/25/17.
 */

public class SendCustomOfferActivity extends AppCompatActivity implements OnApiFailDueToSessionListener {

    private final int LINK_SERVICE_REQUEST = 9;
    private final int REQUEST_SHIPPING_DETAIL = 3;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtTitle, txtLinked, btnSend, txtRemainCharacters, txtServiceDesc, txtServicePrice, txtServiceDuration, txtServiceUserName;
    private EditText editPrice, editOfferDesc, editWorkTime, editMiles;
    private RelativeLayout layoutShipping, layoutRadius, layoutLink;
    CardView layoutService;
    private LinearLayout layoutUnit, itemHour, itemDay, itemWeek;
    private ImageView btnCross, btnUnit, imgHour, imgDay, imgWeek;
    private SquareImageView imgService;
    private ImageButton check1, check2, check3, check4;
    private KProgressHUD loader;

    private String userDetails;
    private Integer verified = 0;
    private String offer_id = "";
    private String offer_buyerId = "";
    private String offer_sellerId = "";
    private String offer_serviceId = "";
    private String offer_desc = "";
    private Integer offer_price = 0;
    private Integer offer_delivery = 2;
    private Integer offer_workTime = 1;
    private String offer_workTimeUnit = "day";
    private Integer offer_radius = 15;
    private String offer_currencySymbol = "$";
    private String offer_currencyCode = "USD";

    private String mUserId = "";
    private String mUserFirst = "";
    private String mUserLast = "";

    private Integer isMessage = 0;
    private Boolean isLinked = false;
    private ChatServiceModel linkService = null;

    private Button btnRadius;
    private ParcelModel parcelModel = new ParcelModel();
    private AddressModel addressModel = new AddressModel();
    private List<Address> addresses;
    private FulfillmentMethodForCustom1 fulfillmentMethod = new FulfillmentMethodForCustom1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_custom_offer);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getLoginUserInfo();

        offer_sellerId = mUserId;
        isMessage = getIntent().getIntExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 0);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(R.string.send_custom_offer);

        findViewById(R.id.toolbar_right_img).setVisibility(View.GONE);
        findViewById(R.id.toolbar_lft_img).setOnClickListener(mBaseClickListener);

        initUI();


        if (isMessage == 2 || isMessage == 3) {
            btnSend.setSelected(true);
            btnSend.setEnabled(true);

            loader.show();
            offer_id = getIntent().getStringExtra(ConstantUtils.SELECT_OFFER_ID);
            callGetCustomOfferDetails(offer_id);
        } else {
            btnSend.setSelected(false);
            offer_buyerId = sharedPreferences.getString(ConstantUtils.CHAT_USER_ID, "");
            verified = sharedPreferences.getInt(ConstantUtils.USER_VERIFIED, 0);
            setDeliveryMethod(2);
            hideLinkedService();
        }
    }

    @SuppressLint("DefaultLocale")
    private void initUI() {
        btnSend = (Button) findViewById(R.id.btn_send);
        layoutLink = (RelativeLayout) findViewById(R.id.layout_link);
        layoutLink.setOnClickListener(mBaseClickListener);
        layoutShipping = (RelativeLayout) findViewById(R.id.layout_shipping);
        layoutShipping.setOnClickListener(mBaseClickListener);
        layoutRadius = (RelativeLayout) findViewById(R.id.layout_radius);
        layoutRadius.setOnClickListener(mBaseClickListener);
        txtLinked = (TextView) findViewById(R.id.txt_link_service);
        txtLinked.setText(R.string.link_a_service);
        txtRemainCharacters = (TextView) findViewById(R.id.txt_remain_character);
        txtServiceDesc = (TextView) findViewById(R.id.txt_service_name);
        txtServiceDuration = (TextView) findViewById(R.id.txt_price_desc);
        txtServicePrice = (TextView) findViewById(R.id.txt_service_price);
        txtServiceUserName = (TextView) findViewById(R.id.txt_service_provider_name);
        btnRadius = (Button) findViewById(R.id.button_miles);
        btnRadius.setOnClickListener(mBaseClickListener);
        btnRadius.setText(String.format("%d miles", fulfillmentMethod.getLocalServiceRadius()));
        layoutRadius.setVisibility(View.GONE);
        layoutShipping.setVisibility(View.GONE);

        layoutService = (CardView) findViewById(R.id.layout_service);
        layoutService.setVisibility(View.GONE);
        imgService = (SquareImageView) findViewById(R.id.img_service_provider);
        editPrice = (EditText) findViewById(R.id.edit_offer_price);
        editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = String.valueOf(s);
                if (!str.equals("") && !str.contains("$")) {
                    editPrice.setText("");
                    editPrice.setText("$" + str);
                    editPrice.setSelection(str.length()+1);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (editPrice.isFocused()) {
                    String str = s.toString();
                    if (str.length() < 2) {
                        offer_price = 0;
                    } else {
                        offer_price = Integer.parseInt(str.substring(1));
                        offer_currencySymbol = "$";
                        offer_currencyCode = "USD";
                    }
                    allowSendOffer();
                }
            }
        });

        editOfferDesc = (EditText) findViewById(R.id.edit_offer_desc);
        editOfferDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offer_desc = String.valueOf(s);
                int length = editOfferDesc.getText().toString().length();
                txtRemainCharacters.setText(String.format("%d/120", length));
                allowSendOffer();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                allowSendOffer();
            }
            @Override
            public void afterTextChanged(Editable s) {
                allowSendOffer();
            }
        });

        check1 = (ImageButton) findViewById(R.id.check_button1);
        check1.setOnClickListener(mRadioClickListener);
        check2 = (ImageButton) findViewById(R.id.check_button2);
        check2.setOnClickListener(mRadioClickListener);
        check3 = (ImageButton) findViewById(R.id.check_button3);
        check3.setOnClickListener(mRadioClickListener);
        check4 = (ImageButton) findViewById(R.id.check_button4);
        check4.setOnClickListener(mRadioClickListener);

        btnSend.setOnClickListener(mBaseClickListener);
    }

    private void getLoginUserInfo() {
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            userDetails = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(userDetails);

                if (jsonObject.has("_id")) {
                    if (jsonObject.get("_id") != null && !jsonObject.get("_id").toString().isEmpty())
                        mUserId = jsonObject.get("_id").toString();
                }

                if (jsonObject.has("firstName")) {
                    if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty())
                        mUserFirst = jsonObject.get("firstName").toString();
                }

                if (jsonObject.has("lastName")) {
                    if (jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                        mUserLast = jsonObject.get("lastName").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDeliveryMethod(int index) {
        offer_delivery = index;
        check1.setSelected(false);
        check2.setSelected(false);
        check3.setSelected(false);
        check4.setSelected(false);
        fulfillmentMethod.setLocal(false);
        fulfillmentMethod.setOnline(false);
        fulfillmentMethod.setShipment(false);
        fulfillmentMethod.setStore(false);
        layoutRadius.setVisibility(View.GONE);
        layoutShipping.setVisibility(View.GONE);
        switch (index) {
            case 0:
                check1.setSelected(true);
                fulfillmentMethod.setOnline(true);
                break;
            case 1:
                check2.setSelected(true);
                fulfillmentMethod.setShipment(true);
                layoutShipping.setVisibility(View.VISIBLE);
                break;
            case 2:
                check3.setSelected(true);
                layoutRadius.setVisibility(View.VISIBLE);
                if (fulfillmentMethod.getLocalServiceRadius() > 0) {
                    btnRadius.setText(String.format("%d %ss", fulfillmentMethod.getLocalServiceRadius(), fulfillmentMethod.getLocalServiceRadiusUom()));
                }
                fulfillmentMethod.setLocal(true);
                break;
            case 3:
                check4.setSelected(true);
                fulfillmentMethod.setStore(true);
                break;
            default:
                break;
        }

        allowSendOffer();
    }

    private void setOfferInfo(CustomOfferModels selected) {
        if (selected.getBuyerId() != null && !selected.getBuyerId().isEmpty()) {
            offer_buyerId = selected.getBuyerId();
        }
        if (selected.getServiceId() != null && !selected.getServiceId().isEmpty()) {
            offer_serviceId = selected.getServiceId();
        }
        if (selected.getCurrencyCode() != null && !selected.getCurrencyCode().isEmpty()) {
            offer_currencyCode = selected.getCurrencyCode();
        }
        if (selected.getCurrencySymbol() != null && !selected.getCurrencySymbol().isEmpty()) {
            offer_currencySymbol = selected.getCurrencySymbol();
        }
        if (selected.getDescription() != null && !selected.getDescription().isEmpty()) {
            offer_desc = selected.getDescription();
            editOfferDesc.setText(offer_desc);
        }
        if (selected.getPrice() != null) {
            offer_price = selected.getPrice();
            editPrice.setText(String.format("%s%d", offer_currencySymbol, offer_price));
        }
        if (selected.getWorkDuration() != null && selected.getWorkDuration() > 0) {
            offer_workTime = selected.getWorkDuration();
        }
        if (selected.getWorkDurationUom() != null && !selected.getWorkDurationUom().isEmpty()) {
            offer_workTimeUnit = selected.getWorkDurationUom();
        }

        if (selected.getFulfillmentMethod() != null) {
            if (selected.getFulfillmentMethod().get_id() != null) {
                fulfillmentMethod.set_id(selected.getFulfillmentMethod().get_id());
            }
            if (selected.getFulfillmentMethod().getLocal()) {
                offer_radius = selected.getFulfillmentMethod().getLocalServiceRadius();
                fulfillmentMethod.setLocal(true);
                fulfillmentMethod.setLocalServiceRadius(offer_radius);
                fulfillmentMethod.setLocalServiceRadiusUom(selected.getFulfillmentMethod().getLocalServiceRadiusUom());
                setDeliveryMethod(2);
            }else if (selected.getFulfillmentMethod().getOnline()) {
                fulfillmentMethod.setOnline(true);
                setDeliveryMethod(0);
            }else if (selected.getFulfillmentMethod().getStore()) {
                fulfillmentMethod.setStore(true);
                setDeliveryMethod(1);
            }else if (selected.getFulfillmentMethod().getShipment()) {
                fulfillmentMethod.setShipment(true);
                setDeliveryMethod(3);
            }
        }
    }

    private void displayCustomOfferDetails() {
//        editPrice.setText("$" + String.valueOf(offer_price));
//        editOfferDesc.setText(offer_desc);
//        editWorkTime.setText(String.valueOf(offer_workTime));
//        setDeliveryMethod(offer_delivery);
//
//        if (offer_workTimeUnit.equals("hour")) {
//            setPaidTime(0);
//        } else if (offer_workTimeUnit.equals("week")) {
//            setPaidTime(2);
//        } else {
//            setPaidTime(1);
//        }
//
//        editMiles.setText(String.valueOf(offer_radius));
    }

    private void showLinkedService(LinkServiceModel service) {
        linkService = new ChatServiceModel();

        if (service.getId() != null && !service.getId().isEmpty()) {
            offer_serviceId = service.getId();
            linkService.setServiceId(service.getId());
        }
        if (service.getMedia() != null) {
            if (service.getMedia().getFileName() != null && !service.getMedia().getFileName().isEmpty()) {
                String strPic = service.getMedia().getFileName();
                if (!strPic.contains("https://s3.amazonaws.com")) {
//                    strPic = "https://s3.amazonaws.com" + strPic;
                }

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.user_avatar_placeholder)
                        .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                        .showImageOnFail(R.drawable.user_avatar_placeholder)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();
                ImageLoader.getInstance().displayImage(strPic, imgService, options);
            }
            linkService.setMedia(service.getMedia());
        }
        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            linkService.setDescription(service.getDescription());
        }
        if (service.getPrices() != null && service.getPrices().size() > 0) {
            linkService.setPrice(service.getPrices().get(0));
        }

        UserChatModel objSeller = new UserChatModel();
        objSeller.setFirstName(mUserFirst);
        objSeller.setLastName(mUserLast);
        linkService.setSeller(objSeller);

        isLinked = true;
        layoutService.setVisibility(View.VISIBLE);
        txtLinked.setText(R.string.link_another_service);

        allowSendOffer();
    }

    private void hideLinkedService() {
        isLinked = false;
        layoutService.setVisibility(View.GONE);
        txtLinked.setText(R.string.link_a_service);
        offer_serviceId = "";
        linkService = null;

        allowSendOffer();
    }

    private void allowSendOffer() {
        if (!editPrice.getText().toString().equals("") && !editOfferDesc.getText().toString().equals("")){
            if (fulfillmentMethod.getStore() || fulfillmentMethod.getOnline()) {
                btnSend.setEnabled(true);
            }else if (fulfillmentMethod.getShipment() ) {
                if (fulfillmentMethod.getAddress().getCity() != null) {
                    btnSend.setEnabled(true);
                }else{
                    btnSend.setEnabled(false);
                }
            }else if (fulfillmentMethod.getLocal()) {
                btnSend.setEnabled(true);
            }
        }else{
            btnSend.setEnabled(false);
        }
    }

    private void sendCustomOffer() {
        if (offer_serviceId.equals("")) {
            offer_serviceId = null;
        }
        if (isMessage == 2 || isMessage == 3) {
            if (!offer_id.equals("")) {
                loader.show();
                callUpdateCustomOffer(offer_id, fulfillmentMethod);
            }
        } else {
            loader.show();
            callPostCustomOffer(fulfillmentMethod);
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void callGetCustomOfferDetails(final String offerId) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCustomOfferDetailsResponse> customOfferDetailsCall = apiService.getCustomOfferDetails(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), offerId);
        customOfferDetailsCall.enqueue(new Callback<GetCustomOfferDetailsResponse>() {
            @Override
            public void onResponse(Call<GetCustomOfferDetailsResponse> call, Response<GetCustomOfferDetailsResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    CustomOfferModels offerInfo = response.body().getOffer();
                    setOfferInfo(offerInfo);
                    if (offerInfo.getServiceId() != null && !offerInfo.getServiceId().isEmpty()) {
                        callGetServiceInfo(offerInfo.getServiceId());
                    } else {
                        if (loader.isShowing()) { loader.dismiss(); }
                        hideLinkedService();
                    }
                    displayCustomOfferDetails();
                }
                else if (response.code() == 401) {
                    if (loader.isShowing()) { loader.dismiss(); }
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(SendCustomOfferActivity.this, "callCustomOfferDetailsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SendCustomOfferActivity.this);
                }
                else if (response.code() == 404) {
                    if (loader.isShowing()) { loader.dismiss(); }
                    Toast.makeText(SendCustomOfferActivity.this, "Can't get the offer info!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetCustomOfferDetailsResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(SendCustomOfferActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });
    }

    private void callGetServiceInfo(final String serviceId) {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetServiceByIdResponse> getServiceByIdResponseCall = apiService.getServiceById(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), serviceId);
        getServiceByIdResponseCall.enqueue(new Callback<GetServiceByIdResponse>() {
            @Override
            public void onResponse(Call<GetServiceByIdResponse> call, Response<GetServiceByIdResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getService() != null) {
                        showLinkedService(response.body().getService());
                    } else {
                        hideLinkedService();
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(SendCustomOfferActivity.this, "callServiceByIdApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(SendCustomOfferActivity.this);
                }
                else if (response.code() == 404) {
                    Toast.makeText(SendCustomOfferActivity.this, "Can't get the linked service info!", Toast.LENGTH_SHORT).show();
                    hideLinkedService();
                }
            }

            @Override
            public void onFailure(Call<GetServiceByIdResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(SendCustomOfferActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                hideLinkedService();
            }
        });
    }

    private void callUpdateCustomOffer(final String offerId, FulfillmentMethodForCustom1 offer_fulfillment) {
        SendCustomOfferRequest sendCustomOfferRequest = new SendCustomOfferRequest(offer_sellerId, offer_buyerId, offer_currencyCode, offer_currencySymbol, offer_desc, offer_fulfillment, offer_price, offer_workTime, offer_workTimeUnit);
        if (offer_serviceId != null) {
            sendCustomOfferRequest.setServiceId(offer_serviceId);
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> updateCustomOfferRequestCall = apiService.updateCustomOffers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), offerId, sendCustomOfferRequest);
        updateCustomOfferRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }
                if (response.code() == 200) {
                    if (isMessage == 2) {
                        CustomOfferModels postedOffer = new CustomOfferModels();
                        postedOffer.setPrice(offer_price);
                        postedOffer.setCurrencySymbol(offer_currencySymbol);
                        postedOffer.setWorkDuration(offer_workTime);
                        postedOffer.setWorkDurationUom(offer_workTimeUnit);

                        Gson gson = new Gson();
                        Intent intent = new Intent();
                        intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_ID, offerId);
                        intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_INFO, gson.toJson(postedOffer));
                        intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_LINKED, isLinked);

                        if (isLinked) {
                            intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_SERVICE, gson.toJson(linkService));
                        }

                        setResult(RESULT_OK, intent);
                    }

                    Toast.makeText(SendCustomOfferActivity.this, "Update custom offer success!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(SendCustomOfferActivity.this, "Update custom offer failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(SendCustomOfferActivity.this, "Update custom offer failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callPostCustomOffer(FulfillmentMethodForCustom1 offer_fulfillment) {
        SendCustomOfferRequest sendCustomOfferRequest = new SendCustomOfferRequest(offer_sellerId, offer_buyerId, offer_currencyCode, offer_currencySymbol, offer_desc, offer_fulfillment, offer_price, offer_workTime, offer_workTimeUnit);
        if (offer_serviceId != null) {
            sendCustomOfferRequest.setServiceId(offer_serviceId);
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCustomOfferDetailsResponse> sendCustomOfferRequestCall = apiService.sendCustomOffers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), sendCustomOfferRequest);
        sendCustomOfferRequestCall.enqueue(new Callback<GetCustomOfferDetailsResponse>() {
            @Override
            public void onResponse(Call<GetCustomOfferDetailsResponse> call, Response<GetCustomOfferDetailsResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                CustomOfferModels postedOffer = response.body().getOffer();

                if (response.code() == 200) {
                    if (isMessage == 1) {
                        if (postedOffer != null) {
                            if (postedOffer.getId() != null && !postedOffer.getId().isEmpty()) {
                                String postedOfferId = postedOffer.getId();

                                Gson gson = new Gson();
                                Intent intent = new Intent();
                                intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_ID, postedOfferId);
                                intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_INFO, gson.toJson(postedOffer));
                                intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_LINKED, isLinked);

                                if (isLinked) {
                                    intent.putExtra(ConstantUtils.CHAT_CUSTOM_OFFER_SERVICE, gson.toJson(linkService));
                                }

                                setResult(RESULT_OK, intent);
                            }
                        }
                    }

                    Toast.makeText(SendCustomOfferActivity.this, "Send custom offer success!", Toast.LENGTH_LONG).show();
                    finish();
               }
                else {
                    Toast.makeText(SendCustomOfferActivity.this, "Send custom offer failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetCustomOfferDetailsResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(SendCustomOfferActivity.this, "Send custom offer failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showRadiusDialog(){
        int selectedIndex = fulfillmentMethod.getLocalServiceRadius() / 5 - 1;
        new MaterialDialog.Builder(this)
                .title("Choose Radius")
                .items(R.array.radius)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        btnRadius.setText(text);
                        fulfillmentMethod.setLocalServiceRadius(Integer.parseInt(((String)text).replace(" miles", "")));
                        fulfillmentMethod.setLocalServiceRadiusUom("mile");
                        return true;
                    }
                })
                .positiveText("OK")
                .negativeText("CANCEL")
                .show();

    }

    View.OnClickListener mBaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toolbar_lft_img:
                    onBackPressed();
                    break;

                case R.id.layout_link:
                    Intent intent = new Intent(SendCustomOfferActivity.this, SendServiceActivity.class);
                    editor.putString(ConstantUtils.SERVICE_TYPE, "Link Service").apply();
                    startActivityForResult(intent, LINK_SERVICE_REQUEST);
                    break;

                case R.id.layout_radius:
                    showRadiusDialog();
                    break;

                case R.id.layout_shipping:
                    Intent intent1 = new Intent(SendCustomOfferActivity.this, AddShippingDetailActivity.class);
                    Gson gson = new Gson();
                    intent1.putExtra(ConstantUtils.ADD_ADDRESS, gson.toJson(fulfillmentMethod.getAddress()));
                    intent1.putExtra(ConstantUtils.ADD_PARCEL, gson.toJson(fulfillmentMethod.getParcel()));
                    startActivityForResult(intent1, REQUEST_SHIPPING_DETAIL);
                    break;

                case R.id.button_miles:
                    showRadiusDialog();
                    break;
//
//                case R.id.txt_work_unit:
//                    btnUnit.setImageResource(R.drawable.up_arrow);
//                    layoutUnit.setVisibility(View.VISIBLE);
//                    break;
//
                case R.id.img_down:
                    btnUnit.setImageResource(R.drawable.up_arrow);
                    layoutUnit.setVisibility(View.VISIBLE);
                    break;

                case R.id.btn_send:
                    loader.show();
                    sendCustomOffer();
                    break;

                default:
                    break;
            }
        }
    };

    View.OnClickListener mRadioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check_button1:
                    setDeliveryMethod(0);
                    break;
                case R.id.check_button2:
                    setDeliveryMethod(1);
                    break;
                case R.id.check_button3:
                    setDeliveryMethod(2);
                    break;
                case R.id.check_button4:
                    setDeliveryMethod(3);
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener mUnitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.layout_hour_item:
//                    setPaidTime(0);
//                    break;
//                case R.id.layout_day_item:
//                    setPaidTime(1);
//                    break;
//                case R.id.layout_week_item:
//                    setPaidTime(2);
//                    break;
//                default:
//                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LINK_SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();
                String strService = data.getStringExtra(ConstantUtils.LINK_SERVICE_INFO);
                linkService = gson.fromJson(strService, ChatServiceModel.class);

                offer_serviceId = data.getStringExtra(ConstantUtils.LINK_SERVICE_ID);
                String strPic = data.getStringExtra(ConstantUtils.LINK_SERVICE_PIC);
                String strDesc = data.getStringExtra(ConstantUtils.LINK_SERVICE_DESC);

                txtServiceDesc.setText(strDesc);
                txtServiceUserName.setText(linkService.getSeller().getFirstName()+" "+linkService.getSeller().getLastName());

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.user_avatar_placeholder)
                        .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                        .showImageOnFail(R.drawable.user_avatar_placeholder)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();
                ImageLoader.getInstance().displayImage(strPic, imgService, options);

                isLinked = true;
                layoutService.setVisibility(View.VISIBLE);
                txtLinked.setText(R.string.link_another_service);
                if (linkService.getPrice() != null) {
                    String strSymbol="$", strPrice="", strTime="", strTimeUnit="";
                    if (linkService.getPrice().getCurrencySymbol() != null && !linkService.getPrice().getCurrencySymbol().isEmpty())
                        strSymbol = linkService.getPrice().getCurrencySymbol();

                    if (linkService.getPrice().getPrice() != null && linkService.getPrice().getPrice() > 0)
                        strPrice = String.valueOf(linkService.getPrice().getPrice());

                    if (linkService.getPrice().getTime() != null && linkService.getPrice().getTime() > 0)
                        strTime = String.valueOf(linkService.getPrice().getTime());

                    if (linkService.getPrice().getTimeUnitOfMeasure() != null && !linkService.getPrice().getTimeUnitOfMeasure().isEmpty())
                        strTimeUnit = linkService.getPrice().getTimeUnitOfMeasure();

                    String strDuration = strSymbol;
                    if (!strPrice.equals("")) { strDuration += strPrice; }
                    else                      { strDuration += "0"; }

                    if (!strTime.equals("") && !strTimeUnit.equals("")) {
                        if (strTime.equals("1")) { strDuration += " for " + strTime + " " + strTimeUnit; }
                        else                     { strDuration += " for " + strTime + " " + strTimeUnit + "s"; }
                    }

                    txtServiceDuration.setText(strDuration);
                    txtServicePrice.setText(strSymbol+strPrice);
                } else {
                    txtServicePrice.setText("NA");
                    txtServiceDuration.setText("");
                }

                allowSendOffer();
            }
        }else if (requestCode == REQUEST_SHIPPING_DETAIL) {
            if (resultCode == RESULT_OK){
                Gson gson = new Gson();
                String strAddress = data.getStringExtra(ConstantUtils.ADD_ADDRESS);
                addressModel = gson.fromJson(strAddress, AddressModel.class);
                String strParcel = data.getStringExtra(ConstantUtils.ADD_PARCEL);
                parcelModel = gson.fromJson(strParcel, ParcelModel.class);
                fulfillmentMethod.setAddress(addressModel);
                fulfillmentMethod.setParcel(parcelModel);
                allowSendOffer();
            }

        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing())     loader.dismiss();
        Log.e("Send Custom Offer Api Failed: ", apiSource);
    }
}
