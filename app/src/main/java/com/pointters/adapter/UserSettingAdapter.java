package com.pointters.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.UserSettingsModel;

import java.util.List;

/**
 * Created by prashantkumar on 22/9/17.
 */

public class UserSettingAdapter extends RecyclerView.Adapter<UserSettingAdapter.MyViewHolder> implements RadioGroup.OnCheckedChangeListener {
    private Context context;
    private List<UserSettingsModel> settingsModelList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public UserSettingAdapter(Context context, List<UserSettingsModel> settingsModelList, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.settingsModelList = settingsModelList;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_setting, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (settingsModelList.get(position).getTitle() != null && !settingsModelList.get(position).getTitle().isEmpty())
            holder.txtTitle.setText(settingsModelList.get(position).getTitle().toString());

        if (position == 0) {
            holder.layoutAnootation.setVisibility(View.VISIBLE);
        } else {
            holder.layoutAnootation.setVisibility(View.GONE);
        }
        if (settingsModelList.get(position).getSeletedItem() != null) {
            if (!settingsModelList.get(position).getSeletedItem().toString().isEmpty() && settingsModelList.get(position).getSeletedItem().toString().equals("onlyme")) {
                holder.imgOnlyme.setChecked(true);


            } else if (!settingsModelList.get(position).getSeletedItem().toString().isEmpty() && settingsModelList.get(position).getSeletedItem().toString().equals("followers")) {
                holder.imgFollowers.setChecked(true);

            } else if (!settingsModelList.get(position).getSeletedItem().toString().isEmpty() && settingsModelList.get(position).getSeletedItem().toString().equals("public")) {
                holder.imgPublic.setChecked(true);

            }
        }
        holder.radioGroup.setTag(position);
        holder.radioGroup.setOnCheckedChangeListener(this);


    }

    @Override
    public int getItemCount() {
        return settingsModelList.size();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch (checkedId) {
            case R.id.rb_public:
                onRecyclerViewItemClickListener.onItemClick((int) group.getTag());
                settingsModelList.get((int) group.getTag()).setSeletedItem("public");
                break;
            case R.id.rb_followers:
                onRecyclerViewItemClickListener.onItemClick((int) group.getTag());
                settingsModelList.get((int) group.getTag()).setSeletedItem("followers");
                break;
            case R.id.rb_onlyMe:
                onRecyclerViewItemClickListener.onItemClick((int) group.getTag());
                settingsModelList.get((int) group.getTag()).setSeletedItem("onlyme");
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private RadioButton imgPublic, imgFollowers, imgOnlyme;
        private RadioGroup radioGroup;
        private LinearLayout layoutAnootation;

        public MyViewHolder(final View itemView) {
            super(itemView);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            imgPublic = (RadioButton) itemView.findViewById(R.id.rb_public);
            imgFollowers = (RadioButton) itemView.findViewById(R.id.rb_followers);
            imgOnlyme = (RadioButton) itemView.findViewById(R.id.rb_onlyMe);
            layoutAnootation = (LinearLayout) itemView.findViewById(R.id.layout_annotation);


        }
    }
}
