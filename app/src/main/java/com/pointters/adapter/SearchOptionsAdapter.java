package com.pointters.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.SearchHint;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchOptionsAdapter extends RecyclerView.Adapter<SearchOptionsAdapter.SearchOptionViewHolder> {

    private ArrayList<String> listOptions;
    private Context context;


    public SearchOptionsAdapter(ArrayList<String> listOptions, Context context) {
        this.listOptions = listOptions;
        this.context = context;
    }

    @Override
    public SearchOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_option, parent, false);

        return new SearchOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchOptionViewHolder holder, int position) {

        holder.txtOption.setText(listOptions.get(position));

        switch (position) {
            case 0:
                holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.color_option_1));
                break;
            case 1:
                holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.color_option_2));
                break;
            case 2:
                holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.color_option_3));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return listOptions.size();
    }

    public class SearchOptionViewHolder extends RecyclerView.ViewHolder {

        private TextView txtOption;
        private View viewIndicator;

        public SearchOptionViewHolder(View itemView) {
            super(itemView);
            txtOption = (TextView) itemView.findViewById(R.id.txt_option);
            viewIndicator = itemView.findViewById(R.id.view_indicator);
        }
    }

}
