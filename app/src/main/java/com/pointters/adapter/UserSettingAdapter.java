package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.pointters.R;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.UserSettingsModel;

import java.util.List;


/**
 * Created by prashantkumar on 22/9/17.
 */

public class UserSettingAdapter extends ExpandableRecyclerView.Adapter<UserSettingAdapter.ChildViewHolder, UserSettingAdapter.GroupViewHolder, String, String> implements View.OnClickListener {
    private Context context;
    private List<UserSettingsModel> settingsModelList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public UserSettingAdapter(Context context, List<UserSettingsModel> settingsModelList, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.settingsModelList = settingsModelList;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

//    @Override
//    protected UserSettingAdapter.GroupViewHolder onCreateGroupViewHolder(ViewGroup viewGroup) {
//        return new GroupViewHolder(context);
//    }
//

    @Override
    public int getGroupItemCount() {
        return settingsModelList.size() - 1;
    }

    @Override
    public int getChildItemCount(int group) {
        return 3;
    }

    @Override
    public String getGroupItem(int position) {
        if (position < settingsModelList.size()) {
            return settingsModelList.get(position).getTitle();
        }else{
            return "";
        }
    }

    @Override
    public String getChildItem(int group, int position) {
        switch (position){
            case 0:
                return "Public";
            case 1:
                return "Followers";
            case 2:
                return "Only me";
        }
        return "";
    }

    @Override
    protected GroupViewHolder onCreateGroupViewHolder(ViewGroup var1) {
        return new GroupViewHolder(context);
    }


    @Override
    protected ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_user_setting, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public int getChildItemViewType(int group, int position) {
        return 1;
    }


    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int group) {
        super.onBindGroupViewHolder(holder, group);
        holder.setText(getGroupItem(group));
        holder.setSubtext(getChildItem(group, settingsModelList.get(group).getSeletedItem()));
    }


    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int group, final int position) {
        super.onBindChildViewHolder(holder, group, position);
        holder.tv.setText(getChildItem(group, position));
        if (position == settingsModelList.get(group).getSeletedItem()) {
            holder.checkbox.setSelected(true);
        }else{
            holder.checkbox.setSelected(false);
        }
        holder.checkbox.setTag(group+"0"+position);
        holder.checkbox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onRecyclerViewItemClickListener.onItemClick(0);
        switch ((String)v.getTag()) {
            case "000":
                UserSettingsModel model = settingsModelList.get(0);
                model.setSeletedItem(0);
                settingsModelList.set(0, model);
                notifyDataSetChanged();
                break;
            case "001":
                UserSettingsModel model1 = settingsModelList.get(0);
                model1.setSeletedItem(1);
                settingsModelList.set(0, model1);
                notifyDataSetChanged();
                break;
            case "002":
                UserSettingsModel model2 = settingsModelList.get(0);
                model2.setSeletedItem(2);
                settingsModelList.set(0, model2);
                notifyDataSetChanged();
                break;
            case "100":
                UserSettingsModel model3 = settingsModelList.get(1);
                model3.setSeletedItem(0);
                settingsModelList.set(1, model3);
                notifyDataSetChanged();
                break;
            case "101":
                UserSettingsModel model4 = settingsModelList.get(1);
                model4.setSeletedItem(1);
                settingsModelList.set(1, model4);
                notifyDataSetChanged();
                break;
            case "102":
                UserSettingsModel model5 = settingsModelList.get(1);
                model5.setSeletedItem(2);
                settingsModelList.set(1, model5);
                notifyDataSetChanged();
                break;
        }
    }

    public static class GroupViewHolder extends ExpandableRecyclerView.GroupViewHolder {
        ImageView expandedIndicator;
        TextView text;
        TextView subtext;
        private boolean expanded;

        public GroupViewHolder(Context context) {
            super(View.inflate(context, R.layout.adapter_user_setting_group, (ViewGroup)null));
            this.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            this.expandedIndicator = (ImageView)this.itemView.findViewById(R.id.carbon_groupExpandedIndicator);
            this.text = (TextView)this.itemView.findViewById(R.id.carbon_groupText);
            this.subtext = (TextView)this.itemView.findViewById(R.id.carbon_groupsubText);
        }

        public void expand() {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(200L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewHelper.setRotation(GroupViewHolder.this.expandedIndicator, 180.0F * ((Float)animation.getAnimatedValue()).floatValue());
                    GroupViewHolder.this.expandedIndicator.postInvalidate();
                }
            });
            animator.start();
            this.expanded = true;
        }

        public void collapse() {
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{1.0F, 0.0F});
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(200L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewHelper.setRotation(GroupViewHolder.this.expandedIndicator, 180.0F * ((Float)animation.getAnimatedValue()).floatValue());
                    GroupViewHolder.this.expandedIndicator.postInvalidate();
                }
            });
            animator.start();
            this.expanded = false;
        }

        public void setExpanded(boolean expanded) {
            ViewHelper.setRotation(this.expandedIndicator, expanded?180.0F:0.0F);
            this.expanded = expanded;
        }

        public boolean isExpanded() {
            return this.expanded;
        }

        public void setText(String t) {
            this.text.setText(t);
        }

        public void setSubtext(String t) {
            this.subtext.setText(t);
        }

        public String getText() {
            return this.text.getText().toString();
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv;
        private final ImageButton checkbox;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.txt_title);
            checkbox = (ImageButton)itemView.findViewById(R.id.checkbox);
        }

    }
}