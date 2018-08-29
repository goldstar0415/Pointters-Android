package com.pointters.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.pointters.model.CategoryDetails;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by prashantkumar on 22/9/17.
 */

public class SelectCategoryAdapter extends ExpandableRecyclerView.Adapter<SelectCategoryAdapter.ChildViewHolder, SelectCategoryAdapter.GroupViewHolder, String, String> implements View.OnClickListener {
    private Context context;
    private List<CategoryDetails> settingsModelList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    public String selectedCategory = "";
    public String selectedCategoryid = "";
    public SelectCategoryAdapter(Context context, List<CategoryDetails> settingsModelList, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.context = context;
        this.settingsModelList = settingsModelList;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public String getSelectedCategoryId() {
        return selectedCategoryid;
    }


    @Override
    public int getGroupItemCount() {
        return settingsModelList.size() - 1;
    }

    @Override
    public int getChildItemCount(int group) {
        if (settingsModelList.get(group).getSubCategories() == null){
            return 0;
        }
        return settingsModelList.get(group).getSubCategories().size();
    }

    public void setData(ArrayList<CategoryDetails> details){
        this.settingsModelList = details;
        notifyDataSetChanged();
    }

    @Override
    public String getGroupItem(int position) {
        if (position < settingsModelList.size()) {
            return settingsModelList.get(position).getName();
        }else{
            return "";
        }
    }

    @Override
    public String getChildItem(int group, int position) {
        CategoryDetails models = settingsModelList.get(group);
        return models.getSubCategories().get(position).getName();
    }

    @Override
    protected GroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
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

        if (settingsModelList.size() > group){
            if (settingsModelList.get(group).getUserId() == null) {
                holder.expandedIndicator.setVisibility(View.INVISIBLE);
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gg_color));
                holder.setText(getGroupItem(group));
                holder.itemView.setClickable(false);
            }else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                holder.expandedIndicator.setVisibility(View.VISIBLE);
                holder.itemView.setClickable(true);
                holder.setText(getGroupItem(group));
            }
        }
    }


    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int group, final int position) {
        super.onBindChildViewHolder(holder, group, position);
        CategoryDetails models = settingsModelList.get(group);
        holder.tv.setText(getChildItem(group, position));

        holder.checkbox.setTag(group+"0"+position);
        holder.checkbox.setTag(models.getSubCategories().get(position).get_id());
        if (selectedCategoryid.equals(models.getSubCategories().get(position).get_id())){
            selectedCategory = models.getName();
            holder.checkbox.setSelected(true);
        }else{
            holder.checkbox.setSelected(false);
        }
        holder.checkbox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ((ImageButton)v != null) {
            ((ImageButton)v).setSelected(!((ImageButton)v).isSelected());
            selectedCategoryid = (String) ((ImageButton)v).getTag();
            notifyDataSetChanged();
        }
    }

    public static class GroupViewHolder extends ExpandableRecyclerView.GroupViewHolder {
        ImageView expandedIndicator;
        TextView text;
        private boolean expanded;

        public GroupViewHolder(Context context) {
            super(View.inflate(context, R.layout.adapter_category_group, (ViewGroup)null));
            this.itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            this.expandedIndicator = (ImageView)this.itemView.findViewById(R.id.carbon_groupExpandedIndicator);
            this.text = (TextView)this.itemView.findViewById(R.id.carbon_groupText);
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