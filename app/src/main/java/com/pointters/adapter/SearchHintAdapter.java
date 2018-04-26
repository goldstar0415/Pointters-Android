package com.pointters.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.listener.OnSearchItemClickListener;
import com.pointters.model.PopularCateGoriyModel;
import com.pointters.model.SearchHint;
import com.pointters.model.SearchModel;
import com.pointters.utils.DividerItemDecorationVer;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchHintAdapter extends RecyclerView.Adapter<SearchHintAdapter.SearchHintViewHolder> {

    ArrayList<SearchHint> hintArrayList;
    ArrayList<String> popularCategories = new ArrayList<>();
    ArrayList<String> recentSearches = new ArrayList<>();
    ArrayList<String> locationSearches = new ArrayList<>();
    private Context context;
    private OnSearchItemClickListener onSearchItemClickListener;
    private String searchType;

    public SearchHintAdapter(ArrayList<SearchHint> hintArrayList, String type) {
        this.hintArrayList = hintArrayList;
        this.searchType = type;
        if (type.equals("Search")) {
            for (int i =0; i < hintArrayList.size(); i ++) {
                if (hintArrayList.get(i).getTitle().equals("Popular Categories")) {
                    for (int j = 0 ; j < hintArrayList.get(i).getHintsList().size(); j++){
                        PopularCateGoriyModel model = (PopularCateGoriyModel) hintArrayList.get(i).getHintsList().get(j);
                        popularCategories.add(model.getName());
                    }
                }else if (hintArrayList.get(i).getTitle().equals("Recent Searches")){
                    for (int j = 0 ; j < hintArrayList.get(i).getHintsList().size(); j++){
                        SearchModel model = (SearchModel) hintArrayList.get(i).getHintsList().get(j);
                        recentSearches.add(model.getQuery());
                    }
                }
            }
        }else{
            for (int j = 0 ; j < hintArrayList.get(0).getHintsList().size(); j++){
                String model = (String) hintArrayList.get(0).getHintsList().get(j);
                locationSearches.add(model);
            }
        }
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
        SearchOptionsAdapter searchOptionsAdapter = null;
        if (searchType.equals("Location")) {
            searchOptionsAdapter = new SearchOptionsAdapter(locationSearches, context, searchType);
        }else{
            if (position == 0) {
                if (hintArrayList.get(position).getTitle().equals("Popular Categories")) {
                    searchOptionsAdapter = new SearchOptionsAdapter(popularCategories, context, "Categories");
                }else if (hintArrayList.get(position).getTitle().equals("Recent Searches")) {
                    searchOptionsAdapter = new SearchOptionsAdapter(recentSearches, context, "Categories");
                }
            }else{
                if (hintArrayList.get(position).getTitle().equals("Popular Categories")) {
                    searchOptionsAdapter = new SearchOptionsAdapter(popularCategories, context, searchType);
                }else if (hintArrayList.get(position).getTitle().equals("Recent Searches")) {
                    searchOptionsAdapter = new SearchOptionsAdapter(recentSearches, context, searchType);
                }
            }
        }

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorationVer(ContextCompat.getDrawable(context, R.drawable.bg_light_gray_divider));
        holder.recyclerViewOptions.addItemDecoration(dividerItemDecoration);

        holder.recyclerViewOptions.setAdapter(searchOptionsAdapter);

        holder.recyclerViewOptions.addOnItemTouchListener(new OnRecycleItemClickListener(context, new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (hintArrayList.get(position).getTitle().equals("Popular Categories")) {
//                    PopularCateGoriyModel model = (PopularCateGoriyModel) hintArrayList.get(i).getHintsList().get(i);
                    onSearchItemClickListener.searchItemClicked(popularCategories.get(i));
                }else if (hintArrayList.get(position).getTitle().equals("Recent Searches")) {
//                    SearchModel model = (SearchModel) hintArrayList.get(i).getHintsList().get(i);
                    onSearchItemClickListener.searchItemClicked(recentSearches.get(i));
                }else{
                    onSearchItemClickListener.searchItemClicked(locationSearches.get(i));
                }
//                onSearchItemClickListener.searchItemClicked(hintArrayList.get(position).getHintsList().get(i));

            }
        }));
//        if (position == 0) {
//            holder.layoutParams.setMargins((int) context.getResources().getDimension(R.dimen._12sdp), (int) context.getResources().getDimension(R.dimen._8sdp), (int) context.getResources().getDimension(R.dimen._12sdp), (int) context.getResources().getDimension(R.dimen._8sdp));
//            holder.layoutParent.setLayoutParams(holder.layoutParams);
//        }
    }

    @Override
    public int getItemCount() {
        return hintArrayList.size();
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

    public class SearchHintViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, btnSeeAll;
        private RecyclerView recyclerViewOptions;
        private ConstraintLayout layoutParent;
        private ConstraintLayout.LayoutParams layoutParams;

        public SearchHintViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            btnSeeAll = (TextView) itemView.findViewById(R.id.btn_see_all);
            recyclerViewOptions = (RecyclerView) itemView.findViewById(R.id.recycler_search_options);
            layoutParent = (ConstraintLayout) itemView.findViewById(R.id.layout_parent);
            layoutParams = (ConstraintLayout.LayoutParams) layoutParent.getLayoutParams();
        }
    }
}
