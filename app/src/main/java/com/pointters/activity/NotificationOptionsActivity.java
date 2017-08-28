package com.pointters.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.NotificationOption;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NotificationOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<NotificationOption> mNotificationOption = new ArrayList<>();
    private RecyclerView recyclerViewNotificationOption;
    private NotificationOptionAdapter notificationOptionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_options);

        //set toolbar
        AppUtils.setToolBarWithBothIcon(NotificationOptionsActivity.this, getResources().getString(R.string.notification_option),
                R.drawable.back_icon_grey, 0);


        recyclerViewNotificationOption = (RecyclerView) findViewById(R.id.rv_notification_option);
        NotificationOption notificationOption = new NotificationOption();

        notificationOption.setHeader("General Notifications");
        notificationOption.setSubHeader("(Point,Like,Comment,Follow)");
        mNotificationOption.add(notificationOption);

        NotificationOption notificationOption1 = new NotificationOption();
        notificationOption1.setHeader("Order Notifications");
        notificationOption1.setSubHeader("(Order updates, Custom offers)");
        mNotificationOption.add(notificationOption1);

        NotificationOption notificationOption2 = new NotificationOption();
        notificationOption2.setHeader("Order Notifications");
        notificationOption2.setSubHeader("(Live Request)");
        mNotificationOption.add(notificationOption2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationOptionsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewNotificationOption.setLayoutManager(layoutManager);
        notificationOptionAdapter = new NotificationOptionAdapter(NotificationOptionsActivity.this, mNotificationOption);
        recyclerViewNotificationOption.setAdapter(notificationOptionAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(NotificationOptionsActivity.this, R.drawable.divider_option));
        recyclerViewNotificationOption.addItemDecoration(divider);

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

    //=====================================Notification Adapter=========================================
    public class NotificationOptionAdapter extends RecyclerView.Adapter<NotificationOptionAdapter.ViewHolder> {

        private ArrayList<NotificationOption> notification_options;
        private Context context;

        public NotificationOptionAdapter(Context context, ArrayList<NotificationOption> notification_options) {
            this.context = context;
            this.notification_options = notification_options;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_option_data_strip, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.titleTextView.setText(notification_options.get(position).getHeader());
            holder.subTitleTextView.setText(notification_options.get(position).getSubHeader());
        }

        @Override
        public int getItemCount() {

            return notification_options.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView titleTextView, subTitleTextView;

            public ViewHolder(View view) {

                super(view);
                titleTextView = (TextView) view.findViewById(R.id.title);
                subTitleTextView = (TextView) view.findViewById(R.id.subtitle);

            }
        }
    }

}
