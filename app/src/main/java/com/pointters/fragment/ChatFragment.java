package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.ChatAdapter;
import com.pointters.listener.OnRecyclerViewItemClickListener;

/**
 * Created by prashantkumar on 18/8/17.
 */

public class ChatFragment extends Fragment implements View.OnClickListener, OnRecyclerViewItemClickListener {
    private View view;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private TextView searchBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat_service, container, false);
        searchBar = (TextView) view.findViewById(R.id.txt_search_here_hint);
        searchBar.setOnClickListener(this);
        searchBar.setText(getResources().getString(R.string.search_conversations));
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.mChatService);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        chatRecyclerView.addItemDecoration(divider);
        chatAdapter = new ChatAdapter(getActivity(), this);
        chatRecyclerView.setAdapter(chatAdapter);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_search_here_hint:


                break;
        }
    }

    @Override
    public void onItemClick(int position) {

        switch (position)
        {

        }


    }
}
