package com.pointters.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.TagServiceSellerAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.TagServiceSellerModel;
import com.pointters.model.response.GetTagServiceSellerResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/7/17.
 */

public class TagServiceActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private RecyclerView recyclerTagServices;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView txtNotFound;
    private EditText editSearch;
    private KProgressHUD loader;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private int limitCnt = 10;
    private int totalCnt = 0;
    private String strKey = "";

    private boolean isSearch = false;

    List<TagServiceSellerModel> arrTagServices = new ArrayList<>();
    TagServiceSellerAdapter tagServiceSellerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_service);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        findViewById(R.id.btn_back).setOnClickListener(this);
        txtNotFound = (TextView) findViewById(R.id.tag_not_found);
        editSearch = (EditText) findViewById(R.id.txt_tag_search);
        editSearch.setOnEditorActionListener(mEditorActionListener);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        recyclerTagServices = (RecyclerView) findViewById(R.id.recycler_tag_services);
        recyclerTagServices.addOnItemTouchListener(new OnRecycleItemClickListener(this, new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String strName="", strPos="", strPic="";
                String strId = arrTagServices.get(position).getId();
                String strType = arrTagServices.get(position).getType();

                if (strType != null && strType.equals("user")) {
                    strName = arrTagServices.get(position).getSource().getFirstName() + " " + arrTagServices.get(position).getSource().getLastName();
                } else {
                    strName = arrTagServices.get(position).getSource().getDescription();
                }

                if (arrTagServices.get(position).getSource().getLocation() != null) {
                    String strCity = "", strState = "", strKm="NA";
                    if (arrTagServices.get(position).getSource().getLocation().getCity() != null && !arrTagServices.get(position).getSource().getLocation().getCity().equals(""))
                        strCity = arrTagServices.get(position).getSource().getLocation().getCity();
                    if (arrTagServices.get(position).getSource().getLocation().getState() != null && !arrTagServices.get(position).getSource().getLocation().getState().equals(""))
                        strState = arrTagServices.get(position).getSource().getLocation().getState();
                    if (arrTagServices.get(position).getSource().getLocation().getGeoJson() != null) {
                        GeoJsonModel geoJson = arrTagServices.get(position).getSource().getLocation().getGeoJson();
                        if (geoJson.getCoordinates() != null && geoJson.getCoordinates().size() > 0) {
                            Location servicePos = new Location("");
                            servicePos.setLatitude(geoJson.getCoordinates().get(1));
                            servicePos.setLongitude(geoJson.getCoordinates().get(0));

                            Location userPos = new Location("");
                            userPos.setLatitude(mUserLat);
                            userPos.setLongitude(mUserLng);

                            strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";
                        }
                    }

                    if (strCity.equals("")) {
                        if (!strState.equals("")) {
                            strPos = strKm + "@" + strState;
                        } else {
                            strPos = strKm;
                        }
                    } else {
                        strPos = strKm + "@" + strCity + ", " + strState;
                    }
                }

                if (arrTagServices.get(position).getSource().getProfilePic() != null && !arrTagServices.get(position).getSource().getProfilePic().isEmpty()) {
                    strPic = arrTagServices.get(position).getSource().getProfilePic();
                    if (!strPic.contains("https://s3.amazonaws.com")) {
                        strPic = "https://s3.amazonaws.com" + strPic;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra(ConstantUtils.CHOOSE_TAG_ID, strId);
                intent.putExtra(ConstantUtils.CHOOSE_TAG_TYPE, strType);
                intent.putExtra(ConstantUtils.CHOOSE_TAG_NAME, strName);
                intent.putExtra(ConstantUtils.CHOOSE_TAG_POS, strPos);
                intent.putExtra(ConstantUtils.CHOOSE_TAG_PIC, strPic);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TagServiceActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerTagServices.setLayoutManager(linearLayoutManager);
        recyclerTagServices.setItemAnimator(new DefaultItemAnimator());

        tagServiceSellerAdapter = new TagServiceSellerAdapter(TagServiceActivity.this, arrTagServices, mUserLat, mUserLng);
        recyclerTagServices.setAdapter(tagServiceSellerAdapter);

        recyclerTagServices.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt - arrTagServices.size() > 0) {
                    getTagServicesApi(false, strKey, arrTagServices.size());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
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
                    arrTagServices.clear();
                    tagServiceSellerAdapter.notifyDataSetChanged();
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("Please enter the search key");
                } else {
                    isSearch = true;
                    loader.show();
                    getTagServicesApi(true, strKey, 0);
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

    void getTagServicesApi(final boolean inited, String searchKey, int lastNum) {
        if (inited) {
            arrTagServices.clear();
            txtNotFound.setVisibility(View.GONE);
        }

        String query = "firstName:" + searchKey + " OR "
                     + "lastName:" + searchKey + " OR "
                     + "description:" + searchKey;

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("size", "" + limitCnt);
        params.put("from", "" + lastNum);

        ApiInterface apiService = ApiClient.getClient(true).create(ApiInterface.class);
        final Call<GetTagServiceSellerResponse> tagServiceRequest = apiService.getSearchTagServiceSeller(params);
        tagServiceRequest.enqueue(new Callback<GetTagServiceSellerResponse>() {
            @Override
            public void onResponse(Call<GetTagServiceSellerResponse> call, Response<GetTagServiceSellerResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getUpHits().getTotal();

                    arrTagServices.addAll(response.body().getUpHits().getHits());
                    tagServiceSellerAdapter.notifyItemRangeInserted(tagServiceSellerAdapter.getItemCount(), arrTagServices.size()-1);

                    if (inited && arrTagServices.size() == 0) {
                        if (isSearch) {
                            txtNotFound.setText("No service or seller found");
                        } else {
                            txtNotFound.setText("Please enter the search key");
                        }
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(TagServiceActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(TagServiceActivity.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    if (isSearch) {
                        txtNotFound.setText("No service or seller found");
                    } else {
                        txtNotFound.setText("Please enter the search key");
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetTagServiceSellerResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(TagServiceActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing())     loader.dismiss();
        Log.e("User Followers Api Failed: ", apiSource);
    }
}
