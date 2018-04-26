package com.pointters.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String searchType;


    public SearchOptionsAdapter(ArrayList<String> listOptions, Context context, String type) {
        this.listOptions = listOptions;
        this.context = context;
        this.searchType = type;
    }

    @Override
    public SearchOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_option, parent, false);

        return new SearchOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchOptionViewHolder holder, int position) {

        String str = listOptions.get(position);
        holder.txtOption.setText(listOptions.get(position));
        if (searchType.equals("Location")) {
            holder.viewIndicator.setImageResource(R.drawable.location_pin_icon);
        }else if (searchType.equals("Categories")){
            switch (position) {
                case 0:
                    holder.viewIndicator.setImageResource(R.drawable.icons_photographer);
                    break;
                case 1:
                    holder.viewIndicator.setImageResource(R.drawable.icons_entertainer);
                    break;
                case 2:
                    holder.viewIndicator.setImageResource(R.drawable.icons_wedding);
                    break;
            }
        }else{
            holder.viewIndicator.setImageResource(R.drawable.icons_recent_searches);
        }
    }

    @Override
    public int getItemCount() {
        return listOptions.size();
    }

    public class SearchOptionViewHolder extends RecyclerView.ViewHolder {

        private TextView txtOption;
        private ImageView viewIndicator;

        public SearchOptionViewHolder(View itemView) {
            super(itemView);
            txtOption = (TextView) itemView.findViewById(R.id.txt_option);
            viewIndicator = (ImageView) itemView.findViewById(R.id.view_indicator);
        }
    }

}
