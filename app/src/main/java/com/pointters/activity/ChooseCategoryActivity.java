package com.pointters.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.adapter.ChooseCategoryAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.CategoryModel;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.model.response.GetTagServiceSellerResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ChooseCategoryActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private List<CategoryModel> childList;
    private ExpandableListView expListView;
    private List<String> listDataHeader = new ArrayList<String>();
    private HashMap<String, List<CategoryModel>> listDataChild = new HashMap<String, List<CategoryModel>>();
    private GetCategoryResponse getCategoryResponse;
    private ChooseCategoryAdapter chooseCategoryAdapter;
    private KProgressHUD loader;
    private CategoryModel categoryModel;

    private int groupPosition = -1, childPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose__category);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        AppUtils.setToolBarWithBothIcon(ChooseCategoryActivity.this, getResources().getString(R.string.choose_category), R.drawable.back_icon_grey, 0);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setBackgroundResource(R.drawable.bg_bottom_corne);

        if (getIntent() != null) {
            groupPosition = getIntent().getIntExtra(ConstantUtils.GROUP_POSITION, -1);
            childPosition = getIntent().getIntExtra(ConstantUtils.CHILD_POSITION, -1);

            listDataHeader = getIntent().getStringArrayListExtra(ConstantUtils.CATEGORY_LIST_HEADRES);
            listDataChild = (HashMap<String, List<CategoryModel>>) getIntent().getSerializableExtra(ConstantUtils.CATEGORY_LIST_Child);

            if (listDataHeader.size() > 0 && listDataChild.size() > 0) {
                chooseCategoryAdapter = new ChooseCategoryAdapter(ChooseCategoryActivity.this, listDataHeader, listDataChild, groupPosition, childPosition);
                expListView.setAdapter(chooseCategoryAdapter);
                categoryModel = (CategoryModel) getIntent().getSerializableExtra(ConstantUtils.CATEGORY_MODEL);
                if (groupPosition != -1) {
                    expListView.expandGroup(groupPosition);
                }
            } else {
                loader.show();
                getCategoryApi();
            }
        }

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                categoryModel = new CategoryModel(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getId(), listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getName());
                Intent intent = new Intent();
                intent.putExtra(ConstantUtils.CHOOSED_CATEGORY, categoryModel.getName());
                intent.putExtra(ConstantUtils.CATEGORY_MODEL, categoryModel);
                intent.putExtra(ConstantUtils.GROUP_POSITION, groupPosition);
                intent.putExtra(ConstantUtils.CHILD_POSITION, childPosition);
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }
        });
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

                    for (int i = 0; i < getCategoryResponse.getCategories().size(); i++) {
                        listDataHeader.add(getCategoryResponse.getCategories().get(i).getName());
                        childList = new ArrayList<CategoryModel>();

                        if (getCategoryResponse.getCategories().get(i).getSubCategories().size() > 0) {
                            for (int j = 0; j < getCategoryResponse.getCategories().get(i).getSubCategories().size(); j++) {
                                CategoryModel categoryModel = new CategoryModel(getCategoryResponse.getCategories().get(i).getSubCategories().get(j).get_id(), getCategoryResponse.getCategories().get(i).getSubCategories().get(j).getName());
                                childList.add(categoryModel);
                            }
                        }

                        listDataChild.put(listDataHeader.get(i), childList);
                    }

                    chooseCategoryAdapter = new ChooseCategoryAdapter(ChooseCategoryActivity.this, listDataHeader, listDataChild, groupPosition, childPosition);
                    expListView.setAdapter(chooseCategoryAdapter);
                    categoryModel = (CategoryModel) getIntent().getSerializableExtra(ConstantUtils.CATEGORY_MODEL);

                    if (groupPosition != -1) {
                        expListView.expandGroup(groupPosition);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ChooseCategoryActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ChooseCategoryActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetCategoryResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(ChooseCategoryActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing())     loader.dismiss();
        Log.e("Get Category Api Failed: ", apiSource);
    }
}
