package com.pointters.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.adapter.TransactionCarouselAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.NotificationModel;
import com.pointters.model.TransactionModel;
import com.pointters.model.response.GetNotificationsResponse;
import com.pointters.model.response.GetTransactionHistoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AppUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.PowerMenuUtils;
import com.pointters.utils.RoundedCornerLayout;
import com.pointters.utils.TypefaceSpan;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TransactionHistoryActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewItemClickListener, DatePickerDialog.OnDateSetListener, SwipyRefreshLayout.OnRefreshListener, DiscreteScrollView.OnItemChangedListener, OnApiFailDueToSessionListener{

    private DiscreteScrollView transactionCarousel;
    private String[] transactionType = {"Purchases", "Sales Earning", "Active Order Purchases", "Completed Order Purchases", "Personal Balance"};

    private String[] filterArray = {"Showing All Transactions", "Purchases", "Sales Earning", "Active Order Purchases", "Completed Order Purchases", "Personal Balance"};
    private String[] filtertextArray = {"", "purchases", "Sales", "active_purchases", "completed_purchases", "personal_balance"};
    private ArrayList<String> purchaseAmounts = new ArrayList<>();

    private TransactionCarouselAdapter adapter;
    private InfiniteScrollAdapter infiniteScrollAdapter;
    private KProgressHUD loader;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String lastDocId = "";
    private ArrayList<TransactionModel> transactions = new ArrayList<>();
    private RecyclerView recyclerTransactions;
    private DatePickerDialog datePickerDialog;
    private GetTransactionHistoryResponse transData;
    private SwipyRefreshLayout swipyRefreshLayout;

    private EditText statementPeriodPicker;
    private RelativeLayout transFilterPicker;
    private Adapter transAdapter;

    private PowerMenu filterMenu;
    private ImageView dropdownIcon;
    private TextView txtFilterText;
    private  boolean isSelectedPeriod = false;
    private int selectedYear = 0;
    private int selectedMonth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        AppUtils.setToolBarWithBothIcon(TransactionHistoryActivity.this, getResources().getString(R.string.transactions_history), R.drawable.back_icon, 0);
        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh);
        swipyRefreshLayout.setOnRefreshListener(this);
        filterMenu = PowerMenuUtils.getHamburgerPowerMenu(this, this, onHamburgerItemClickListener);
        dropdownIcon = (ImageView) findViewById(R.id.dropdown_icon);
        txtFilterText = (TextView) findViewById(R.id.txt_filter);
        transactionCarousel = (DiscreteScrollView) findViewById(R.id.transaction_carousel);
        recyclerTransactions = (RecyclerView) findViewById(R.id.recycler_transactions);
        statementPeriodPicker = (EditText) findViewById(R.id.edt_date);
        transFilterPicker = (RelativeLayout) findViewById(R.id.transaction_filter);
        transFilterPicker.setOnClickListener(this);
        statementPeriodPicker.setOnClickListener(this);

        DividerItemDecoration divider = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider_option)));
        recyclerTransactions.addItemDecoration(divider);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        String currentMonth = dateFormat.format(date);
        statementPeriodPicker.setText(currentMonth);
        transAdapter = new Adapter(this, R.layout.adapter_transaction_history, transactions, this);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerTransactions.setAdapter(transAdapter);
        loader.show();
        CallGetTransactionHistory(0);
    }

    private OnMenuItemClickListener<PowerMenuItem> onHamburgerItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
//            Toast.makeText(getBaseContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            txtFilterText.setText(filterArray[position]);
            filterMenu.setSelectedPosition(position);
            filterMenu.dismiss();
            lastDocId = "";
            CallGetTransactionHistory( filterMenu.getSelectedPosition());
        }
    };

    public void showFilter() {
        if(filterMenu.isShowing()) {
            filterMenu.dismiss();
            return;
        }
        filterMenu.showAsDropDown(transFilterPicker);
    }

    public void setAdapter(){
        adapter = new TransactionCarouselAdapter(getBaseContext(), transactionType, purchaseAmounts);
        infiniteScrollAdapter = InfiniteScrollAdapter.wrap(adapter);
        transactionCarousel.setAdapter(infiniteScrollAdapter);
        transactionCarousel.setOrientation(DSVOrientation.HORIZONTAL);
        transactionCarousel.setItemTransitionTimeMillis(150);
        transactionCarousel.addOnItemChangedListener(this);
        transactionCarousel.setItemTransformer(new ScaleTransformer.Builder().setMaxScale(1).build());

        adapter.setCurrencySymbol(transData.getCurrencySymbol() != null ? transData.getCurrencySymbol() : "" );
        transAdapter.setData(transactions);
    }

    public void CallGetTransactionHistory(int filter){
        HashMap<String, String> params = new HashMap<>();
        params.put("lt_id", lastDocId);
        if (filter > 0){
            params.put("transactionFilter", filtertextArray[filter]);
        }
        if (selectedYear > 0){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, selectedYear);
            calendar.set(Calendar.MONTH, selectedMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            String monthyear = simpleDateFormat.format(calendar.getTime());
            params.put("statementPeriod", monthyear);
        }
        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetTransactionHistoryResponse> callGetCategoryApi = apiService.getTransactionHisotry(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        callGetCategoryApi.enqueue(new Callback<GetTransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<GetTransactionHistoryResponse> call, Response<GetTransactionHistoryResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    if (lastDocId == "") {
                        transactions = new ArrayList<>();
                    }
                    GetTransactionHistoryResponse result = response.body();
                    ArrayList<TransactionModel> models = result.getDocs();
                    transactions.addAll(models);
                    transData = result;
                    lastDocId = response.body().getLastDocId();
                    purchaseAmounts = new ArrayList<>();
                    purchaseAmounts.add(result.getTotalPurchases());
                    purchaseAmounts.add(result.getTotalSalesEarning());
                    purchaseAmounts.add(result.getTotalActiveOrderPurchases());
                    purchaseAmounts.add(result.getTotalCompletedOrderPurchases());
                    purchaseAmounts.add(result.getTotalPersonalBalance());
                    setAdapter();
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(TransactionHistoryActivity.this, "callGetTagServiceApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(TransactionHistoryActivity.this);
                }
                else if (response.code() == 404) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetTransactionHistoryResponse> call, Throwable t) {
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(TransactionHistoryActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

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
                break;

            case R.id.edt_date:
                Calendar now = Calendar.getInstance();
                if (datePickerDialog == null) {
                    datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }else{
                    datePickerDialog.initialize(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }
                datePickerDialog.setThemeDark(false);
                datePickerDialog.vibrate(true);
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
                datePickerDialog.show(getFragmentManager(), "Date");

                break;

            case R.id.transaction_filter:
                if(filterMenu.isShowing()) {
                    filterMenu.dismiss();
                    return;
                }
                filterMenu.showAsDropDown(dropdownIcon);
                break;
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onApiFail(String apiSource) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedYear = year;
        selectedMonth = monthOfYear;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy");
        String monthyear = simpleDateFormat.format(calendar.getTime());
        statementPeriodPicker.setText(monthyear);
        lastDocId = "";
        CallGetTransactionHistory( filterMenu.getSelectedPosition());

    }

    @Override
    public void onItemClick(int position) {
        TransactionModel model = transactions.get(position);
        Intent intent = new Intent(TransactionHistoryActivity.this, FulfillmentActivity.class);
        intent.putExtra("orderId", model.getOrderId());
        startActivity(intent);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipyRefreshLayout.setRefreshing(false);
        if (direction == SwipyRefreshLayoutDirection.BOTTOM){
            CallGetTransactionHistory(0);
        }else{
            lastDocId = "";
            CallGetTransactionHistory(0);
        }

    }


    //    ===========================================       adapter          =====================================================================

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

        Activity obj;
        int res;
        ArrayList<TransactionModel> Al;
        LayoutInflater inflater;
        OnRecyclerViewItemClickListener listener;

        Adapter(Activity obj, int res, ArrayList<TransactionModel> Al, OnRecyclerViewItemClickListener listener) {
            this.obj = obj;
            this.res = res;
            this.Al = Al;
            inflater = LayoutInflater.from(obj);
            this.listener = listener;
        }

        public void setData(ArrayList<TransactionModel> models){
            this.Al = models;
            notifyDataSetChanged();
        }
        @Override
        public Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Adapter.MyHolder hold;

            View vv = LayoutInflater.from(obj).inflate(res, parent, false);
            hold = new Adapter.MyHolder(vv);
            return hold;

        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(Adapter.MyHolder hold, final int position) {
            TransactionModel model = Al.get(position);
            hold.title.setText(model.getDescription());

            if (model.getAmount() != null) {
                if (model.getCurrencySymbol() != null){
                    hold.txtAmount.setText(String.format("%s%.2f", model.getCurrencySymbol(), model.getAmount()));
                }else{
                    hold.txtAmount.setText(String.format("%.2f", model.getAmount()));
                }
                hold.txtAmount.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_red_for_warning));
            }else{
                hold.txtAmount.setText("");
            }

            String date = model.getDate();
            String displayDate = DateTimeUtils.formatWithPattern(date, "dd/MM/yyyy");
            hold.timeago.setText(displayDate);
            hold.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Al.size();

        }

        //holder
        public class MyHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView timeago;
            TextView txtAmount;

            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.txt_title);
                timeago = (TextView) itemView.findViewById(R.id.txt_time_ago);
                txtAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            }
        }
    }

}