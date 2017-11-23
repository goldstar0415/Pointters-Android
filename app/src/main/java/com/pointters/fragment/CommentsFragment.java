package com.pointters.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.adapter.CommentsAdapter;
import com.pointters.model.CommentsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantkumar on 29/8/17.
 */

public class CommentsFragment extends Fragment {
    private View view;
    private RecyclerView commentsRecyclerView;
    private EditText writeCommentEditText;
    private CommentsAdapter commentsAdapter;
    private TextView sendButton;
    private List<CommentsModel> commentList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentList.add(new CommentsModel("", "", ""));
        commentList.add(new CommentsModel("", "", ""));
        commentList.add(new CommentsModel("", "", ""));
        commentList.add(new CommentsModel("", "", ""));
        commentList.add(new CommentsModel("", "", ""));


        commentsRecyclerView=(RecyclerView)view.findViewById(R.id.rv_comments);
        commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider2 = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider2.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_option));
        commentsAdapter=new CommentsAdapter(getActivity(),commentList);
        commentsRecyclerView.setAdapter(commentsAdapter);
        return view;
    }
}
