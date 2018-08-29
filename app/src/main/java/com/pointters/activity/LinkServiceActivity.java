package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.LinkServiceAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.ChatServiceModel;
import com.pointters.model.GetserviceSearch;
import com.pointters.model.Media;
import com.pointters.model.Prices;
import com.pointters.model.SendServicesModel;
import com.pointters.model.UserChatModel;
import com.pointters.model.request.SendServicesRequest;
import com.pointters.model.response.GetSendServicesResponse;
import com.pointters.model.response.SearchSendServicesResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mac on 12/22/17.
 */

public class LinkServiceActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private RecyclerView recyclerSendServices;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtTitle, txtNotFound, txtYourServices;
    private KProgressHUD loader;

    LinkServiceAdapter sendServiceAdapter;
    List<SendServicesModel> arrSendServices = new ArrayList<>();
    List<GetserviceSearch> arrSearchServices = new ArrayList<>();

    private Boolean isSearch = false;

    private String serviceType;
    private String userDetails;
    private String mUserId = "";
    private String mUsername = "";
    private String mUserFirst = "";
    private String mUserLast = "";

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    private int limitSearchCnt = 10;
    private int totalSearchCnt = 0;
    private String strKey = "";

    private ChatServiceModel chatService;
    private String send_toUserId = "";
    private String service_Id = "";
    private String service_desc = "";
    private String service_pic = "";

    private RelativeLayout searchLayout;
    private EditText edtSearch;
    private ImageView backIImageView, crossImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_services);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AppUtils.setToolBarWithBothIconWithShadow(LinkServiceActivity.this, getResources().getString(R.string.send_service),
                R.drawable.icon_back_arrow, R.drawable.icon_search_medium);

        getLoginUserInfo();
        serviceType = sharedPreferences.getString(ConstantUtils.SERVICE_TYPE, "");
        send_toUserId = sharedPreferences.getString(ConstantUtils.CHAT_USER_ID, "");

        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(serviceType);

        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        txtNotFound = (TextView) findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        txtYourServices = (TextView) findViewById(R.id.txt_your_services);
        txtYourServices.setVisibility(View.VISIBLE);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);




        recyclerSendServices = (RecyclerView) findViewById(R.id.recycler_services);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LinkServiceActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerSendServices.setLayoutManager(linearLayoutManager);
        recyclerSendServices.setItemAnimator(new DefaultItemAnimator());

        sendServiceAdapter = new LinkServiceAdapter(LinkServiceActivity.this, mUsername, serviceType, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                getServiceInfo(position);

                if (serviceType.equals("Send Service")) {
                    callSendServiceApi();
                }
                else {
                    Gson gson = new Gson();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.LINK_SERVICE_ID, service_Id);
                    intent.putExtra(ConstantUtils.LINK_SERVICE_DESC, service_desc);
                    intent.putExtra(ConstantUtils.LINK_SERVICE_PIC, service_pic);
                    intent.putExtra(ConstantUtils.LINK_SERVICE_INFO, gson.toJson(chatService));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

        });
        recyclerSendServices.setAdapter(sendServiceAdapter);

        recyclerSendServices.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (isSearch) {
                } else {
                    if (totalCnt > limitCnt) {
                        getSendServicesApiCall(false, lastDocId);
                    }
                }
            }
        });

        searchLayout = (RelativeLayout) findViewById(R.id.layout_search);
        backIImageView = (ImageView) findViewById(R.id.btn_back);
        crossImageView = (ImageView) findViewById(R.id.img_cross_search_here);
        edtSearch = (EditText) findViewById(R.id.edt_search_here);
        edtSearch.setOnEditorActionListener(mEditorActionListener);

        crossImageView.setOnClickListener(this);
        backIImageView.setOnClickListener(this);
        loader.show();
        getSendServicesApiCall(true, "");

    }

    public void CallGetSearchService(String query) {
        Map<String, String> params = new HashMap<>();
        params.put("q", query);

        ApiInterface apiService = ApiClient.getClient(true).create(ApiInterface.class);
        final Call<List<GetserviceSearch>> searchSendServicesRequest = apiService.getServiceSearch(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        searchSendServicesRequest.enqueue(new Callback<List<GetserviceSearch>>() {
            @Override
            public void onResponse(Call<List<GetserviceSearch>> call, Response<List<GetserviceSearch>> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    arrSearchServices = response.body();
                    sendServiceAdapter.setType(true);
                    sendServiceAdapter.setSearchServicesList(arrSearchServices);
                    sendServiceAdapter.notifyDataSetChanged();
                    if (arrSearchServices.size() > 0) {
                        txtNotFound.setVisibility(View.GONE);
                    }else{
                        txtNotFound.setVisibility(View.VISIBLE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LinkServiceActivity.this, "callSearchSendServicesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LinkServiceActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No service found.");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<List<GetserviceSearch>> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LinkServiceActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            case R.id.toolbar_right_img:
                searchLayout.setVisibility(View.VISIBLE);
                isSearch = true;
                sendServiceAdapter.setType(true);
                sendServiceAdapter.notifyDataSetChanged();
                break;

            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.img_cross_search_here:
                isSearch = false;
                searchLayout.setVisibility(View.GONE);
                sendServiceAdapter.setType(false);
                sendServiceAdapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // TODO Auto-generated method stub
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                strKey = v.getText().toString();
                if (strKey.equals("")) {
                    isSearch = false;
                    loader.show();
                    txtYourServices.setVisibility(View.VISIBLE);
                    getSendServicesApiCall(true, "");
                } else {
                    isSearch = true;
                    loader.show();
                    txtYourServices.setVisibility(View.GONE);
                    CallGetSearchService(edtSearch.getText().toString());
//                    getSendServicesSearchApiCall(true, strKey, 0);
                }

                hideKeyboard((EditText)v);

                return true;
            }
            return false;
        }
    };

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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

                if (!mUserFirst.equals("")) { mUsername += mUserFirst + " "; }
                if (!mUserLast.equals(""))  { mUsername += mUserLast;        }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getServiceInfo(int index) {
        String strMediaType = "image";
        Prices chatPrices = null;
        if (isSearch) {
            if (arrSearchServices.get(index).get_id() != null && !arrSearchServices.get(index).get_id().isEmpty()) {
                service_Id = arrSearchServices.get(index).get_id();
            }
            if (arrSearchServices.get(index).get_source() != null) {
                if (arrSearchServices.get(index).get_source().getDescription() != null && (!arrSearchServices.get(index).get_source().getDescription().isEmpty())) {
                    service_desc = arrSearchServices.get(index).get_source().getDescription();
                }
                if (arrSearchServices.get(index).get_source().getMedia() != null && arrSearchServices.get(index).get_source().getMedia().size() > 0) {
                    if (arrSearchServices.get(index).get_source().getMedia().get(0).getFileName() != null && !arrSearchServices.get(index).get_source().getMedia().get(0).getFileName().isEmpty()) {
                        service_pic = arrSearchServices.get(index).get_source().getMedia().get(0).getFileName();
                        if (!service_pic.contains("https://s3.amazonaws.com")) {
//                            service_pic = "https://s3.amazonaws.com" + service_pic;
                        }
                    }
                    if (arrSearchServices.get(index).get_source().getMedia().get(0).getMediaType() != null && !arrSearchServices.get(index).get_source().getMedia().get(0).getMediaType().isEmpty()) {
                        strMediaType = arrSearchServices.get(index).get_source().getMedia().get(0).getMediaType();
                    }
                }
                if (arrSearchServices.get(index).get_source().getPrices() != null && arrSearchServices.get(index).get_source().getPrices().size() > 0) {
                    chatPrices = arrSearchServices.get(index).get_source().getPrices().get(0);
                }
            }
        }
        else {
            if (arrSendServices.get(index).getService() != null) {
                if (arrSendServices.get(index).getService().getId() != null && !arrSendServices.get(index).getService().getId().isEmpty()) {
                    service_Id = arrSendServices.get(index).getService().getId();
                }
                if (arrSendServices.get(index).getService().getDescription() != null && !arrSendServices.get(index).getService().getDescription().isEmpty()) {
                    service_desc = arrSendServices.get(index).getService().getDescription();
                }
                if (arrSendServices.get(index).getService().getMedia() != null) {
                    if (arrSendServices.get(index).getService().getMedia().getFileName() != null && !arrSendServices.get(index).getService().getMedia().getFileName().isEmpty()) {
                        service_pic = arrSendServices.get(index).getService().getMedia().getFileName();
                        if (!service_pic.contains("https://s3.amazonaws.com")) {
//                            service_pic = "https://s3.amazonaws.com" + service_pic;
                        }
                    }
                    if (arrSendServices.get(index).getService().getMedia().getMediaType() != null && !arrSendServices.get(index).getService().getMedia().getMediaType().isEmpty()) {
                        strMediaType = arrSendServices.get(index).getService().getMedia().getMediaType();
                    }
                }
                if (arrSendServices.get(index).getService().getPrices() != null) {
                    chatPrices = arrSendServices.get(index).getService().getPrices();
                }
            }
        }

        UserChatModel seller = new UserChatModel();
        seller.setFirstName(mUserFirst);
        seller.setLastName(mUserLast);

        chatService = new ChatServiceModel();
        chatService.setServiceId(service_Id);
        chatService.setDescription(service_desc);
        chatService.setMedia(new Media(service_pic, strMediaType));
        chatService.setPrice(chatPrices);
        chatService.setSeller(seller);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void getSendServicesApiCall(final boolean inited, String lastId) {
        if (inited) {
            arrSendServices.clear();
            arrSearchServices.clear();
            sendServiceAdapter.notifyDataSetChanged();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSendServicesResponse> getSendServicesCall = apiService.getSendServices(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), "send_service", lastId);
        getSendServicesCall.enqueue(new Callback<GetSendServicesResponse>() {
            @Override
            public void onResponse(Call<GetSendServicesResponse> call, Response<GetSendServicesResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if(response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    arrSendServices.addAll(response.body().getDocs());
                    sendServiceAdapter.setType(false);
                    sendServiceAdapter.setSendServicesList(arrSendServices);
                    sendServiceAdapter.notifyItemRangeInserted(sendServiceAdapter.getItemCount(), arrSendServices.size()-1);

                    if (inited && arrSendServices.size() == 0) {
                        txtNotFound.setText("No service found");
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LinkServiceActivity.this, "callGetSendServicesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LinkServiceActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No service found");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetSendServicesResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(LinkServiceActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getSendServicesSearchApiCall(final boolean inited, String searchKey, int lastNum) {
        if (inited) {
            arrSearchServices.clear();
            arrSendServices.clear();
            sendServiceAdapter.notifyDataSetChanged();
        }

        String query = "description:" + searchKey;

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("size", "" + limitSearchCnt);
        params.put("from", "" + lastNum);

        ApiInterface apiService = ApiClient.getClient(true).create(ApiInterface.class);
        final Call<SearchSendServicesResponse> searchSendServicesRequest = apiService.getSearchSendServices(params);
        searchSendServicesRequest.enqueue(new Callback<SearchSendServicesResponse>() {
            @Override
            public void onResponse(Call<SearchSendServicesResponse> call, Response<SearchSendServicesResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalSearchCnt = response.body().getUpHits().getTotal();

//                    arrSearchServices.addAll(response.body().getUpHits().getHits());
//                    for (int i = 0; i < arrSearchServices.size(); i ++) {
//                        ServicesWoArray subTemp = new ServicesWoArray();
//                        subTemp.setDescription(arrSearchServices.get(i).getSource().getDescription());
//                        subTemp.setMedia(arrSearchServices.get(i).getSource().getMedia().get(0));
//                        subTemp.setPrices(arrSearchServices.get(i).getSource().getPrices().get(0));
//
//                        SendServicesModel temp = new SendServicesModel();
//                        temp.setService(subTemp);
//
//                        arrSendServices.add(temp);
//                    }

                    sendServiceAdapter.notifyItemRangeInserted(sendServiceAdapter.getItemCount(), arrSendServices.size()-1);

                    if (inited && arrSendServices.size() == 0) {
                        txtNotFound.setText("No service found.");
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(LinkServiceActivity.this, "callSearchSendServicesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(LinkServiceActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No service found.");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<SearchSendServicesResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(LinkServiceActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callSendServiceApi() {
        if (mUserId.equals("") || send_toUserId.equals("") || service_Id.equals("")) { return; }

        loader.show();

        List<String> dictToUsers = new ArrayList<>();
        dictToUsers.add(send_toUserId);

        SendServicesRequest sendServicesRequest = new SendServicesRequest(mUserId, dictToUsers, service_Id);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> sendServiceRequestCall = apiService.sendService(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), sendServicesRequest);
        sendServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Gson gson = new Gson();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtils.CHAT_SEND_SERVICE, gson.toJson(chatService));
                    setResult(RESULT_OK, intent);

                    Toast.makeText(LinkServiceActivity.this, "Send service success!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(LinkServiceActivity.this, "Send service failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(LinkServiceActivity.this, "Send service failed!", Toast.LENGTH_LONG);
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing())     loader.dismiss();
        Log.e("Send Services Api Failed: ", apiSource);
    }
}
