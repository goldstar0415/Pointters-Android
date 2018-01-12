package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.ChatServiceModel;
import com.pointters.model.CustomOfferModels;
import com.pointters.model.FulfillmentMethodForCustom;
import com.pointters.model.LinkServiceModel;
import com.pointters.model.Media;
import com.pointters.model.Tags;
import com.pointters.model.UserChatModel;
import com.pointters.model.request.PostUpdateRequest;
import com.pointters.model.request.SendCustomOfferRequest;
import com.pointters.model.response.GetCustomOfferDetailsResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import net.alhazmy13.mediapicker.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/25/17.
 */

public class SendCustomOfferActivity extends AppCompatActivity implements OnApiFailDueToSessionListener {

    private final int LINK_SERVICE_REQUEST = 9;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtTitle, txtLinked, txtServiceDesc, txtWorkUnit, txtAlert, btnSend;
    private EditText editPrice, editOfferDesc, editWorkTime, editMiles;
    private RelativeLayout layoutLink, layoutService, layoutMiles;
    private LinearLayout layoutUnit, itemHour, itemDay, itemWeek;
    private ImageView btnCross, btnUnit, imgHour, imgDay, imgWeek;
    private RoundedImageView imgService;
    private RadioButton radioOnline, radioShip, radioLocal, radioStore;
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
            btnSend.setSelected(false);

            offer_buyerId = sharedPreferences.getString(ConstantUtils.CHAT_USER_ID, "");
            verified = sharedPreferences.getInt(ConstantUtils.USER_VERIFIED, 0);
            setDeliveryMethod(2);
            hideLinkedService();
        }
    }

    private void initUI() {
        layoutLink = (RelativeLayout) findViewById(R.id.layout_link);
        layoutLink.setOnClickListener(mBaseClickListener);
        txtLinked = (TextView) findViewById(R.id.txt_link_service);
        txtLinked.setText(R.string.link_a_service);

        layoutService = (RelativeLayout) findViewById(R.id.layout_service);
        layoutService.setVisibility(View.GONE);
        imgService = (RoundedImageView) findViewById(R.id.img_profile_round);
        txtServiceDesc = (TextView) findViewById(R.id.txt_linked_desc);
        btnCross = (ImageView) findViewById(R.id.btn_linked_close);
        btnCross.setOnClickListener(mBaseClickListener);

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
        });

        editOfferDesc = (EditText) findViewById(R.id.edit_offer_desc);
        editOfferDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offer_desc = String.valueOf(s);
                allowSendOffer();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        editWorkTime = (EditText) findViewById(R.id.edit_work_time);
        editWorkTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0) {
                    offer_workTime = Integer.parseInt(str);
                } else {
                    offer_workTime = 0;
                }
                allowSendOffer();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtWorkUnit = (TextView) findViewById(R.id.txt_work_unit);
        txtWorkUnit.setOnClickListener(mBaseClickListener);
        btnUnit = (ImageView) findViewById(R.id.img_down);
        btnUnit.setOnClickListener(mBaseClickListener);

        radioOnline = (RadioButton) findViewById(R.id.rb_online);
        radioOnline.setOnClickListener(mRadioClickListener);
        radioShip = (RadioButton) findViewById(R.id.rb_shipment);
        radioShip.setOnClickListener(mRadioClickListener);
        radioLocal = (RadioButton) findViewById(R.id.rb_local);
        radioLocal.setOnClickListener(mRadioClickListener);
        radioStore = (RadioButton) findViewById(R.id.rb_store);
        radioStore.setOnClickListener(mRadioClickListener);

        layoutMiles = (RelativeLayout) findViewById(R.id.layout_miles);
        editMiles = (EditText) findViewById(R.id.edit_miles_value);
        editMiles.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = String.valueOf(s);
                if (str.length() > 0) {
                    offer_radius = Integer.parseInt(str);
                } else {
                    offer_radius = 0;
                }
                allowSendOffer();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        layoutUnit = (LinearLayout) findViewById(R.id.layout_worktime_unit);
        layoutUnit.setVisibility(View.GONE);

        itemHour = (LinearLayout) findViewById(R.id.layout_hour_item);
        itemHour.setOnClickListener(mUnitClickListener);
        itemDay = (LinearLayout) findViewById(R.id.layout_day_item);
        itemDay.setOnClickListener(mUnitClickListener);
        itemWeek = (LinearLayout) findViewById(R.id.layout_week_item);
        itemWeek.setOnClickListener(mUnitClickListener);

        imgHour = (ImageView) findViewById(R.id.img_hour_item);
        imgDay = (ImageView) findViewById(R.id.img_day_item);
        imgWeek = (ImageView) findViewById(R.id.img_week_item);

        txtAlert = (TextView) findViewById(R.id.txt_alert);
        if (verified == 1) {
            txtAlert.setVisibility(View.VISIBLE);
        } else {
            txtAlert.setVisibility(View.GONE);
        }

        btnSend = (TextView) findViewById(R.id.btn_send);
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

        radioOnline.setChecked(false);
        radioShip.setChecked(false);
        radioLocal.setChecked(false);
        radioStore.setChecked(false);

        switch (index) {
            case 0:
                radioOnline.setChecked(true);
                layoutMiles.setVisibility(View.GONE);
                break;
            case 1:
                radioShip.setChecked(true);
                layoutMiles.setVisibility(View.GONE);
                break;
            case 2:
                radioLocal.setChecked(true);
                layoutMiles.setVisibility(View.VISIBLE);
                break;
            case 3:
                radioStore.setChecked(true);
                layoutMiles.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        allowSendOffer();
    }

    private void setPaidTime(int index) {
        imgHour.setVisibility(View.INVISIBLE);
        imgDay.setVisibility(View.INVISIBLE);
        imgWeek.setVisibility(View.INVISIBLE);

        switch (index) {
            case 0:
                imgHour.setVisibility(View.VISIBLE);
                txtWorkUnit.setText("Hour(s)");
                offer_workTimeUnit = "hour";
                break;
            case 1:
                imgDay.setVisibility(View.VISIBLE);
                txtWorkUnit.setText("Day(s)");
                offer_workTimeUnit = "day";
                break;
            case 2:
                imgWeek.setVisibility(View.VISIBLE);
                txtWorkUnit.setText("Week(s)");
                offer_workTimeUnit = "week";
                break;
            default:
                break;
        }

        layoutUnit.setVisibility(View.GONE);
        btnUnit.setImageResource(R.drawable.down_arrow);
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
        }
        if (selected.getPrice() != null && selected.getPrice() > 0) {
            offer_price = selected.getPrice();
        }
        if (selected.getWorkDuration() != null && selected.getWorkDuration() > 0) {
            offer_workTime = selected.getWorkDuration();
        }
        if (selected.getWorkDurationUom() != null && !selected.getWorkDurationUom().isEmpty()) {
            offer_workTimeUnit = selected.getWorkDurationUom();
        }

        if (selected.getFulfillmentMethod() != null) {
            if (selected.getFulfillmentMethod().getLocalServiceRadius() > 0) {
                offer_radius = selected.getFulfillmentMethod().getLocalServiceRadius();
            }
            if (selected.getFulfillmentMethod().getOnline()) {
                offer_delivery = 0;
            }
            if (selected.getFulfillmentMethod().getShipment()) {
                offer_delivery = 1;
            }
            if (selected.getFulfillmentMethod().getLocal()) {
                offer_delivery = 2;
            }
            if (selected.getFulfillmentMethod().getStore()) {
                offer_delivery = 3;
            }
        }
    }

    private void displayCustomOfferDetails() {
        editPrice.setText("$" + String.valueOf(offer_price));
        editOfferDesc.setText(offer_desc);
        editWorkTime.setText(String.valueOf(offer_workTime));
        setDeliveryMethod(offer_delivery);

        if (offer_workTimeUnit.equals("hour")) {
            setPaidTime(0);
        } else if (offer_workTimeUnit.equals("week")) {
            setPaidTime(2);
        } else {
            setPaidTime(1);
        }

        editMiles.setText(String.valueOf(offer_radius));
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
                    strPic = "https://s3.amazonaws.com" + strPic;
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
            txtServiceDesc.setText(service.getDescription());
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
        if (offer_delivery != 2) {
            if (offer_price != 0 && !offer_currencySymbol.equals("") && !offer_desc.equals("") && offer_workTime != 0) {
                btnSend.setSelected(true);
                btnSend.setEnabled(true);
            } else {
                btnSend.setSelected(false);
                btnSend.setSelected(false);
            }
        } else {
            if (offer_price != 0 && !offer_currencySymbol.equals("") && !offer_desc.equals("") && offer_workTime != 0 && offer_radius != 0) {
                btnSend.setSelected(true);
                btnSend.setEnabled(true);
            } else {
                btnSend.setSelected(false);
                btnSend.setSelected(false);
            }
        }
    }

    private void sendCustomOffer() {
        if (offer_serviceId.equals("")) {
            offer_serviceId = null;
        }

        FulfillmentMethodForCustom offer_fulfillment = null;
        if (offer_delivery == 2) {
            offer_fulfillment = new FulfillmentMethodForCustom("false", "false", "true", "false", offer_radius, "mile");
        }
        else {
            switch (offer_delivery) {
                case 0:
                    offer_fulfillment = new FulfillmentMethodForCustom("true", "false", "false", "false", 0, "mile");
                    break;
                case 1:
                    offer_fulfillment = new FulfillmentMethodForCustom("false", "true", "false", "false", 0, "mile");
                    break;
                case 3:
                    offer_fulfillment = new FulfillmentMethodForCustom("false", "false", "false", "true", 0, "mile");
                    break;
                default:
                    break;
            }
        }

        if (isMessage == 2 || isMessage == 3) {
            if (!offer_id.equals("")) {
                loader.show();
                callUpdateCustomOffer(offer_id, offer_fulfillment);
            }
        } else {
            loader.show();
            callPostCustomOffer(offer_fulfillment);
        }
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
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetCustomOfferDetailsResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(SendCustomOfferActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                finish();
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

    private void callUpdateCustomOffer(final String offerId, FulfillmentMethodForCustom offer_fulfillment) {
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

    private void callPostCustomOffer(FulfillmentMethodForCustom offer_fulfillment) {
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

                case R.id.btn_linked_close:
                    hideLinkedService();
                    break;

                case R.id.txt_work_unit:
                    btnUnit.setImageResource(R.drawable.up_arrow);
                    layoutUnit.setVisibility(View.VISIBLE);
                    break;

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
                case R.id.rb_online:
                    setDeliveryMethod(0);
                    break;
                case R.id.rb_shipment:
                    setDeliveryMethod(1);
                    break;
                case R.id.rb_local:
                    setDeliveryMethod(2);
                    break;
                case R.id.rb_store:
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
                case R.id.layout_hour_item:
                    setPaidTime(0);
                    break;
                case R.id.layout_day_item:
                    setPaidTime(1);
                    break;
                case R.id.layout_week_item:
                    setPaidTime(2);
                    break;
                default:
                    break;
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
