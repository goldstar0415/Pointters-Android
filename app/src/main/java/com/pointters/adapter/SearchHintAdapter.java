package com.pointters.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.SearchHint;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.listener.OnSearchItemClickListener;
import com.pointters.utils.DividerItemDecorationVer;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchHintAdapter extends RecyclerView.Adapter<SearchHintAdapter.SearchHintViewHolder> {

    ArrayList<SearchHint> hintArrayList;
    private Context context;
    private OnSearchItemClickListener onSearchItemClickListener;

    public SearchHintAdapter(ArrayList<SearchHint> hintArrayList) {
        this.hintArrayList = hintArrayList;
    }

    @Override
    public SearchHintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_search_hint, parent, false);

        return new SearchHintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHintViewHolder holder, final int position) {

        holder.txtTitle.setText(hintArrayList.get(position).getTitle());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.recyclerViewOptions.setLayoutManager(linearLayoutManager);
        SearchOptionsAdapter searchOptionsAdapter = new SearchOptionsAdapter(hintArrayList.get(position).getHintsList());

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorationVer(ContextCompat.getDrawable(context, R.drawable.bg_light_gray_divider));
        holder.recyclerViewOptions.addItemDecoration(dividerItemDecoration);

        holder.recyclerViewOptions.setAdapter(searchOptionsAdapter);

        holder.recyclerViewOptions.addOnItemTouchListener(new OnRecycleItemClickListener(context, new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                onSearchItemClickListener.searchItemClicked(hintArrayList.get(position).getHintsList()[i]);
            }
        }));

    }

    @Override
    public int getItemCount() {
        return hintArrayList.size();
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

    public class SearchHintViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private RecyclerView recyclerViewOptions;

        public SearchHintViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            recyclerViewOptions = (RecyclerView) itemView.findViewById(R.id.recycler_search_options);
        }
    }
}
