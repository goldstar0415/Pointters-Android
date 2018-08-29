package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.EditServiceDetailActivity;
import com.pointters.adapter.ExpandableRecyclerView;
import com.pointters.adapter.SelectCategoryAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.CategoryDetails;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.DividerItemDecorationVer;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prashantkumar on 18/9/17.
 */

public class AddServiceFragment extends Fragment implements View.OnClickListener, OnRecyclerViewItemClickListener, OnApiFailDueToSessionListener {

    private GetCategoryResponse getCategoryResponse;
    private KProgressHUD loader;

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
        getCategoryApi();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:

                Intent intent = new Intent(getActivity(), EditServiceDetailActivity.class);
                intent.putExtra("categoryId", homeCategoryAdapter.getSelectedCategoryId());
                intent.putExtra("category", homeCategoryAdapter.getSelectedCategory());
                startActivityForResult(intent, 998);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 998) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
    }

}
