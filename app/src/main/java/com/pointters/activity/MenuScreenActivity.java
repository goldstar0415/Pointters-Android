package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.pointters.R;
import com.pointters.adapter.MenuAdapter;
import com.pointters.listener.RecyclerViewListener;
import com.pointters.model.MenuScreenDetails;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.AppUtils;
import com.pointters.utils.ConnectivityController;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MenuScreenActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<MenuScreenDetails> mMenuScreenDetailses = new ArrayList<>();
    ArrayList<MenuScreenDetails> mMenuScreenDetailses1 = new ArrayList<>();
    ArrayList<MenuScreenDetails> mMenuScreenDetailses2 = new ArrayList<>();
    MenuAdapter menuAdapter;
    private RecyclerView recyclerView, recyclerView1, recyclerView2;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        AppUtils.setToolBarWithBothIconWithShadow(MenuScreenActivity.this, getResources().getString(R.string.menu_header), R.drawable.back_icon_grey, 0);
        recyclerView = (RecyclerView) findViewById(R.id.rv_purchases);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView1 = (RecyclerView) findViewById(R.id.rv_sales);
        recyclerView1.setNestedScrollingEnabled(false);
        recyclerView2 = (RecyclerView) findViewById(R.id.rv_general);
        recyclerView2.setNestedScrollingEnabled(false);
//===================1st========================================================================
        MenuScreenDetails menuScreenDetails = new MenuScreenDetails();
        menuScreenDetails.setMenuDetails("Orders (10 Active)");
        mMenuScreenDetailses.add(menuScreenDetails);

        MenuScreenDetails menuScreenDetails11 = new MenuScreenDetails();
        menuScreenDetails11.setMenuDetails("Watching");
        mMenuScreenDetailses.add(menuScreenDetails11);

        MenuScreenDetails menuScreenDetails12 = new MenuScreenDetails();
        menuScreenDetails12.setMenuDetails("Likes");
        mMenuScreenDetailses.add(menuScreenDetails12);

        MenuScreenDetails menuScreenDetails13 = new MenuScreenDetails();
        menuScreenDetails13.setMenuDetails("Live Offers");
        mMenuScreenDetailses.add(menuScreenDetails13);
//=================================2nd============================================================
        MenuScreenDetails menuScreenDetails1 = new MenuScreenDetails();
        menuScreenDetails1.setMenuDetails("Orders (10 Active)");
        mMenuScreenDetailses1.add(menuScreenDetails1);

        MenuScreenDetails menuScreenDetails112 = new MenuScreenDetails();
        menuScreenDetails112.setMenuDetails("Become a Seller");
        mMenuScreenDetailses1.add(menuScreenDetails112);


        MenuScreenDetails menuScreenDetails1123 = new MenuScreenDetails();
        menuScreenDetails1123.setMenuDetails("Offers Sent");
        mMenuScreenDetailses1.add(menuScreenDetails1123);

        MenuScreenDetails menuScreenDetails23 = new MenuScreenDetails();
        menuScreenDetails23.setMenuDetails("Job Opportanities");
        mMenuScreenDetailses1.add(menuScreenDetails23);


        MenuScreenDetails menuScreenDetails3 = new MenuScreenDetails();
        menuScreenDetails3.setMenuDetails("Premium Membership");
        mMenuScreenDetailses1.add(menuScreenDetails3);

        MenuScreenDetails menuScreenDetails4 = new MenuScreenDetails();
        menuScreenDetails4.setMenuDetails("Background Check");
        mMenuScreenDetailses1.add(menuScreenDetails4);

        MenuScreenDetails menuScreenDetails5 = new MenuScreenDetails();
        menuScreenDetails5.setMenuDetails("Business Verification");
        mMenuScreenDetailses1.add(menuScreenDetails5);

        MenuScreenDetails menuScreenDetails6 = new MenuScreenDetails();
        menuScreenDetails6.setMenuDetails("Edit Store Location");
        mMenuScreenDetailses1.add(menuScreenDetails6);
//================================3rd============================================================
        MenuScreenDetails menuScreenDetails2 = new MenuScreenDetails();
        menuScreenDetails2.setMenuDetails("Edit Profile");
        mMenuScreenDetailses2.add(menuScreenDetails2);

        MenuScreenDetails menuScreenDetails22 = new MenuScreenDetails();
        menuScreenDetails22.setMenuDetails("Notification Email/Phone");
        mMenuScreenDetailses2.add(menuScreenDetails22);

        MenuScreenDetails menuScreenDetails221 = new MenuScreenDetails();
        menuScreenDetails221.setMenuDetails("Payment Method");
        mMenuScreenDetailses2.add(menuScreenDetails221);


        MenuScreenDetails menuScreenDetails223 = new MenuScreenDetails();
        menuScreenDetails223.setMenuDetails("Following");
        mMenuScreenDetailses2.add(menuScreenDetails223);

        MenuScreenDetails menuScreenDetails224 = new MenuScreenDetails();
        menuScreenDetails224.setMenuDetails("Followers");
        mMenuScreenDetailses2.add(menuScreenDetails224);

        MenuScreenDetails menuScreenDetails225 = new MenuScreenDetails();
        menuScreenDetails225.setMenuDetails("Invite Friends");
        mMenuScreenDetailses2.add(menuScreenDetails225);

        MenuScreenDetails menuScreenDetails26 = new MenuScreenDetails();
        menuScreenDetails26.setMenuDetails("Leave Feedback");
        mMenuScreenDetailses2.add(menuScreenDetails26);

        MenuScreenDetails menuScreenDetails27 = new MenuScreenDetails();
        menuScreenDetails27.setMenuDetails("Terms of Conditions");
        mMenuScreenDetailses2.add(menuScreenDetails27);

        MenuScreenDetails menuScreenDetails28 = new MenuScreenDetails();
        menuScreenDetails28.setMenuDetails("Privacy Policy");
        mMenuScreenDetailses2.add(menuScreenDetails28);

        MenuScreenDetails menuScreenDetails29 = new MenuScreenDetails();
        menuScreenDetails29.setMenuDetails("Logout");
        mMenuScreenDetailses2.add(menuScreenDetails29);
//=======================================================================================================================
        LinearLayoutManager layoutManager = new LinearLayoutManager(MenuScreenActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        menuAdapter = new MenuAdapter(MenuScreenActivity.this, mMenuScreenDetailses);
        recyclerView.setAdapter(menuAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_option));
        recyclerView.addItemDecoration(divider);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MenuScreenActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        menuAdapter = new MenuAdapter(MenuScreenActivity.this, mMenuScreenDetailses1);
        recyclerView1.setAdapter(menuAdapter);

        DividerItemDecoration divider1 = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider1.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_option));
        recyclerView1.addItemDecoration(divider1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(MenuScreenActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        menuAdapter = new MenuAdapter(MenuScreenActivity.this, mMenuScreenDetailses2);
        recyclerView2.setAdapter(menuAdapter);

        DividerItemDecoration divider2 = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider2.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_option));
        recyclerView2.addItemDecoration(divider2);

        recyclerView1.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 1:
                        startActivity(new Intent(MenuScreenActivity.this, BecomeASellerActivity.class));
                        break;

                }

            }
        }
        ));
        recyclerView2.addOnItemTouchListener(new RecyclerViewListener(MenuScreenActivity.this, new RecyclerViewListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MenuScreenActivity.this, EditProfileActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MenuScreenActivity.this, NotificationOptionsActivity.class));
                        break;
                    case 2:
                        // startActivity(new Intent(MenuScreenActivity.this,PaymentMethodsActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MenuScreenActivity.this, InviteFriendActivity.class));
                        break;
                    case 9:
                        if (ConnectivityController.isNetworkAvailable(MenuScreenActivity.this)) {
                            editor.putBoolean(ConstantUtils.PREF_IS_LOGIN, false).apply();
                            finish();
                            startActivity(new Intent(MenuScreenActivity.this, IntroActivity.class));


                            // logOutCall();
                        } else {
                            Toast.makeText(MenuScreenActivity.this, getResources().getString(R.string.no_internet_warning), Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

            }
        }
        ));
    }

    private void logOutCall() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Object> logOutCall = apiService.logOut(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.APP_PREF, ""));
        logOutCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    AndroidUtils.showToast(MenuScreenActivity.this, "Done");

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;
        }


    }


}
