package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pointters.R;
import com.pointters.activity.CheckoutActivity;
import com.pointters.adapter.CurrentPagePostAdapter;
import com.pointters.adapter.SuggestedRvAdapter;
import com.pointters.utils.AppUtils;

/**
 * Created by prashantkumar on 22/8/17.
 */

public class CurrentUpdatesFragment extends Fragment  implements View.OnClickListener{
    private View view;
    private RecyclerView suggestedRecyclerView, currentUpdatesRecyclerView;
    private SuggestedRvAdapter suggestedRvAdapter;
    private CurrentPagePostAdapter currentPagePostAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_updates, container, false);

        suggestedRecyclerView = (RecyclerView) view.findViewById(R.id.suggested_recycler_view);
        suggestedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        suggestedRvAdapter = new SuggestedRvAdapter();
        suggestedRecyclerView.setAdapter(suggestedRvAdapter);

        currentUpdatesRecyclerView=(RecyclerView)view.findViewById(R.id.suggested_update_recycler_view);
        currentUpdatesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        currentUpdatesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        currentPagePostAdapter=new CurrentPagePostAdapter(getActivity());
        currentUpdatesRecyclerView.setAdapter(currentPagePostAdapter);

    AppUtils.setToolBarWithBothIconFragment(getActivity(), getResources().getString(R.string.current_update), R.drawable.menu_icon, R.drawable.current_update_search_icon_grey,new CurrentUpdatesFragment(),view);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:

                break;
            case R.id.toolbar_right_img:
                break;


        }
    }
}
