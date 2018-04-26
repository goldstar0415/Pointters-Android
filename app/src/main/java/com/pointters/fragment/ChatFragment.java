package com.pointters.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.ChatActivity;
import com.pointters.adapter.ChatAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.model.ConversationsModel;
import com.pointters.model.SearchConversationsModel;
import com.pointters.model.response.GetConversationsResponse;
import com.pointters.model.response.SearchConversationsResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prashantkumar on 18/8/17.
 */

public class ChatFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView chatRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText searchBar;
    private TextView txtNotFound;
    private SwipyRefreshLayout refreshLayout;
    private KProgressHUD loader;

    private ChatAdapter chatAdapter;
    private List<ConversationsModel> conversationsList = new ArrayList<>();
    private List<SearchConversationsModel> conversationsSearchList = new ArrayList<>();

    private Boolean isSearch = false;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    private int limitSearchCnt = 10;
    private int totalSearchCnt = 0;
    private String strKey = "";

    private ImageView toolbarLeftIcon;
    private ImageView toolbarRightIcon;
    private ImageButton backButton;
    private TextView toolbarTitle;
    private View toolBar;
    private View searchView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat_service, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        toolBar = (View) view.findViewById(R.id.main_toolbar_home);
        searchView = (View) view.findViewById(R.id.main_toolbar_search);


        txtNotFound = (TextView) view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Messages");

        toolbarLeftIcon = (ImageView) view.findViewById(R.id.toolbar_lft_img);
        toolbarLeftIcon.setImageResource(R.drawable.icon_menu);

        backButton = (ImageButton) view.findViewById(R.id.menu_button);
        backButton.setImageResource(R.drawable.back_icon);

        toolbarRightIcon = (ImageView) view.findViewById(R.id.toolbar_right_img);
        toolbarRightIcon.setImageResource(R.drawable.icon_search_medium);

        toolbarRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
                toolBar.setVisibility(View.INVISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.INVISIBLE);
                toolBar.setVisibility(View.VISIBLE);
            }
        });

        searchBar = (EditText) view.findViewById(R.id.txt_search_here_hint);
        searchBar.setHint(getResources().getString(R.string.search_conversations));

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.mChatService);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchBar.setOnEditorActionListener(mEditorActionListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_option));
        chatRecyclerView.addItemDecoration(divider);

        chatAdapter = new ChatAdapter(getActivity(), conversationsList);
        chatRecyclerView.setAdapter(chatAdapter);

        chatRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (isSearch) {
                    if (totalSearchCnt - conversationsSearchList.size() > 0) {
                        getConversationsSearchApiCall(false, strKey, conversationsSearchList.size());
                    }
                } else {
                    if (totalCnt > limitCnt) {
                        getConversationsApiCall(false, lastDocId);
                    }
                }
            }
        });

        chatRecyclerView.addOnItemTouchListener(new OnRecycleItemClickListener(getActivity(), new OnRecycleItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int verified = 0;
                String select_userId = "", select_username = "", firstname = "", lastname = "", strPic = "", conversation_id = "";

                if (isSearch) {
                    if (conversationsSearchList.get(position).getSource() != null) {
                        if (conversationsSearchList.get(position).getSource().getUsers() != null && conversationsSearchList.get(position).getSource().getUsers().size() > 0) {
                            select_userId = conversationsSearchList.get(position).getSource().getUsers().get(0).getUserId();
                            firstname = conversationsSearchList.get(position).getSource().getUsers().get(0).getFirstName();
                            lastname = conversationsSearchList.get(position).getSource().getUsers().get(0).getLastName();
                            select_username = firstname + " " + lastname;
                            if (conversationsSearchList.get(position).getSource().getUsers().get(0).getVerified() != null && conversationsSearchList.get(position).getSource().getUsers().get(0).getVerified().booleanValue()) {
                                verified = 1;
                            }
                            strPic = conversationsSearchList.get(position).getSource().getUsers().get(0).getProfilePic();
                        }
                        if (conversationsSearchList.get(position).getId() != null && !conversationsSearchList.get(position).getId().isEmpty()) {
                            conversation_id = conversationsSearchList.get(position).getId();
                        }
                    }
                } else {
                    if (conversationsList.get(position).getUsers() != null && conversationsList.get(position).getUsers().size() > 0) {
                        select_userId = conversationsList.get(position).getUsers().get(0).getUserId();
                        firstname = conversationsList.get(position).getUsers().get(0).getFirstName();
                        lastname = conversationsList.get(position).getUsers().get(0).getLastName();
                        select_username = firstname + " " + lastname;
                        if (conversationsList.get(position).getUsers().get(0).getVerified() != null && conversationsList.get(position).getUsers().get(0).getVerified().booleanValue()) {
                            verified = 1;
                        }
                        strPic = conversationsList.get(position).getUsers().get(0).getProfilePic();
                    }
                    if (conversationsList.get(position).getId() != null && !conversationsList.get(position).getId().isEmpty()) {
                        conversation_id = conversationsList.get(position).getId();
                    }
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                editor.putInt(ConstantUtils.USER_VERIFIED, verified).apply();
                editor.putString(ConstantUtils.CHAT_USER_ID, select_userId).apply();
                editor.putString(ConstantUtils.CHAT_USER_NAME, select_username).apply();
                editor.putString(ConstantUtils.CHAT_USER_PIC, strPic).apply();
                editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, conversation_id);
                startActivity(intent);
            }
        }));

        loader.show();
        getConversationsApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (isSearch) {
                    getConversationsSearchApiCall(true, strKey, 0);
                } else {
                    getConversationsApiCall(true, "");
                }
            }
        });
    }

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // TODO Auto-generated method stub
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                strKey = v.getText().toString();
                if (strKey.equals("")) {
                    isSearch = false;
                    loader.show();
                    getConversationsApiCall(true, "");
                } else {
                    isSearch = true;
                    loader.show();
                    getConversationsSearchApiCall(true, strKey, 0);
                }

                hideKeyboard((EditText)v);

                return true;
            }
            return false;
        }
    };

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void getConversationsApiCall(final boolean inited, String lastId) {
        if (inited) {
            conversationsList.clear();
            conversationsSearchList.clear();
            chatAdapter.notifyDataSetChanged();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetConversationsResponse> getConversationsCall = apiService.getConversations(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getConversationsCall.enqueue(new Callback<GetConversationsResponse>() {
            @Override
            public void onResponse(Call<GetConversationsResponse> call, Response<GetConversationsResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if(response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    conversationsList.addAll(response.body().getDocs());
                    chatAdapter.notifyItemRangeInserted(chatAdapter.getItemCount(), conversationsList.size()-1);

                    if (inited && conversationsList.size() == 0) {
                        txtNotFound.setText("No conversation or user found");
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersReceivedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ChatFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No conversation or user found");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetConversationsResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getConversationsSearchApiCall(final boolean inited, String searchKey, int lastNum) {
        if (inited) {
            conversationsSearchList.clear();
            conversationsList.clear();
            chatAdapter.notifyDataSetChanged();
        }

        Map<String, String> params = new HashMap<>();
        params.put("q", searchKey);
        params.put("size", "" + limitSearchCnt);
        params.put("from", "" + lastNum);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<SearchConversationsResponse> searchConversationsRequest = apiService.getSearchConversations(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), params);
        searchConversationsRequest.enqueue(new Callback<SearchConversationsResponse>() {
            @Override
            public void onResponse(Call<SearchConversationsResponse> call, Response<SearchConversationsResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalSearchCnt = response.body().getUpHits().getTotal();

                    conversationsSearchList.addAll(response.body().getUpHits().getHits());
                    for (int i = 0; i < conversationsSearchList.size(); i ++) {
                        conversationsList.add(conversationsSearchList.get(i).getSource());
                    }
                    chatAdapter.notifyItemRangeInserted(chatAdapter.getItemCount(), conversationsList.size()-1);

                    if (inited) {
                        chatAdapter.notifyDataSetChanged();
                        if (conversationsList.size() == 0) {
                            txtNotFound.setText("No conversation or user found");
                            txtNotFound.setVisibility(View.VISIBLE);
                        } else {
                            txtNotFound.setVisibility(View.GONE);
                        }
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callSearchConversationsApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ChatFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No conversation or user found.");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<SearchConversationsResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing())     loader.dismiss();
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser && view != null)
//            getConversationsApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetConversationsApi")) {
//            getConversationsApiCall(true, "");
        }
    }
}
