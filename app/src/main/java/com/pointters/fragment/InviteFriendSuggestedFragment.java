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
import com.pointters.adapter.SuggestedRecyclerAdapter;

/**
 * Created by prashantkumar on 24/8/17.
 */

public class InviteFriendSuggestedFragment extends Fragment {
    private RecyclerView suggestedRecyclerView;
    private SuggestedRecyclerAdapter suggestedRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friend_suggested, container, false);
        suggestedRecyclerView = (RecyclerView) view.findViewById(R.id.invite_friend_suggested_rv);
        suggestedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        suggestedRecyclerAdapter = new SuggestedRecyclerAdapter();
        suggestedRecyclerView.setAdapter(suggestedRecyclerAdapter);

        return view;
    }
}
