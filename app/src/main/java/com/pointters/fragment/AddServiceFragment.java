package com.pointters.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.AddPriceOptionActivity;
import com.pointters.activity.AddServiceActivity;
import com.pointters.activity.ChooseCategoryActivity;
import com.pointters.activity.EditServiceDetailActivity;
import com.pointters.activity.TagServiceActivity;
import com.pointters.activity.UserSettingsActivity;
import com.pointters.adapter.AddFragmentsInCrosswallPagerAdapter;
import com.pointters.adapter.ChooseCategoryAdapter;
import com.pointters.adapter.DeliveryMethodsRecyclerViewAdapter;
import com.pointters.adapter.ExpandableRecyclerView;
import com.pointters.adapter.ExploreServiceAdapter;
import com.pointters.adapter.PricingRvAdapter;
import com.pointters.adapter.SelectCategoryAdapter;
import com.pointters.adapter.UserSettingAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.CategoryDetails;
import com.pointters.model.CategoryModel;
import com.pointters.model.DeliveryMethod;
import com.pointters.model.FulfillmentMethodForCustom1;
import com.pointters.model.Media;
import com.pointters.model.Prices;
import com.pointters.model.UserSettingsModel;
import com.pointters.model.request.AddServiceRequest;
import com.pointters.model.request.LocationRequestModel;
import com.pointters.model.request.LongitudeLatitude;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;
import com.pointters.utils.DividerItemDecorationVer;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.crosswall.lib.coverflow.CoverFlow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by prashantkumar on 18/9/17.
 */

public class AddServiceFragment extends Fragment implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {

    private List<CategoryModel> childList;
    private List<String> listDataHeader = new ArrayList<String>();
    private HashMap<String, List<CategoryModel>> listDataChild = new HashMap<String, List<CategoryModel>>();
    private GetCategoryResponse getCategoryResponse;
    private KProgressHUD loader;
    private CategoryModel categoryModel;

    private TextView layoutChooseCategory;

    private View view;
    private ExpandableRecyclerView recyclerHomeImprovement;
    private SelectCategoryAdapter homeCategoryAdapter;
    private ArrayList<CategoryDetails> categoryDetails = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_service, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        recyclerHomeImprovement = (ExpandableRecyclerView) view.findViewById(R.id.recycler_view_categories);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        recyclerHomeImprovement.setItemAnimator(new DefaultItemAnimator());
        recyclerHomeImprovement.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecorationVer divider = new DividerItemDecorationVer(ContextCompat.getDrawable(getContext(), R.drawable.divider_option));
        recyclerHomeImprovement.addItemDecoration(divider);
        homeCategoryAdapter = new SelectCategoryAdapter(getContext(), categoryDetails, this);
        recyclerHomeImprovement.setAdapter(homeCategoryAdapter);

        loader = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        initViews();

        return view;
    }

    private void getCategoryApi() {
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetCategoryResponse> callGetCategoryApi = apiService.getCategories();
        callGetCategoryApi.enqueue(new Callback<GetCategoryResponse>() {
            @Override
            public void onResponse(Call<GetCategoryResponse> call, Response<GetCategoryResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    getCategoryResponse = response.body();
                    ArrayList<String> keywords = new ArrayList<>();
                    HashMap<String, ArrayList<CategoryDetails>> maps = new HashMap<>();

                    if (getCategoryResponse.getCategories().size() > 0) {
                        for (int i = 0; i < getCategoryResponse.getCategories().size(); i++) {
                            CategoryDetails details =  getCategoryResponse.getCategories().get(i);
                            for (String key: details.getKeywords()){
                                if (!keywords.contains(key)){
                                    keywords.add(key);
                                }
                            }
                            for (String key : keywords){
                                for (String key1: details.getKeywords()){
                                    if (key.equals(key1)){
                                        if (maps.get(key) != null){
                                            ArrayList<CategoryDetails> categoryDetails = maps.get(key);
                                            if (!categoryDetails.contains(details)) {
                                                categoryDetails.add(details);
                                                maps.put(key, categoryDetails);
                                            }
                                        }else{
                                            ArrayList<CategoryDetails> categoryDetails = new ArrayList<>();
                                            categoryDetails.add(details);
                                            maps.put(key, categoryDetails);
                                        }
                                    }
                                }
                            }
                        }

                    }


                    categoryDetails = new ArrayList<>();
                    for (int i =0; i < maps.keySet().toArray().length; i++){
                        String key = maps.keySet().toArray()[i].toString();
//                        categoryDetails.add(new CategoryDetails(key));
                        categoryDetails.addAll(maps.get(key));
                    }
                    homeCategoryAdapter.setData(categoryDetails);

                }
                else if (response.code() == 401) {
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetCategoryResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
//                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews() {
        layoutChooseCategory = (TextView) view.findViewById(R.id.txt_choose_categories);
        layoutChooseCategory.setOnClickListener(this);

        loader.show();
        getCategoryApi();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:

                Intent intent = new Intent(getActivity(), EditServiceDetailActivity.class);
                intent.putExtra("category", homeCategoryAdapter.getSelectedCategory());
                startActivity(intent);
                break;
            case R.id.txt_choose_categories:
                break;
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onApiFail(String apiSource) {

    }
}
