package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pointters.R;
import com.pointters.activity.ChatActivity;
import com.pointters.activity.CustomOfferDetailsActivity;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.activity.SendCustomOfferActivity;
import com.pointters.adapter.OffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.SentOfferModel;
import com.pointters.model.response.GetSentOfferResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by prashantkumar on 23/8/17.
 */

public class OffersFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<SentOfferModel> sentOffersList=new ArrayList<>();
    private TextView txtNotFound;
    private OffersAdapter offersAdapter;
    private SwipyRefreshLayout refreshLayout;
    private KProgressHUD loader;

    private String userDetails;
    private String mUserId = "";
    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        getLoginUserId();
        if (!sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "").equals("")) {
            mUserLat = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LATITUDE, "0"));
        }
        if (!sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "").equals("")) {
            mUserLng = Double.parseDouble(sharedPreferences.getString(ConstantUtils.USER_LONGITUDE, "0"));
        }

        txtNotFound = (TextView)view.findViewById(R.id.txt_notfound);
        txtNotFound.setVisibility(View.GONE);

        offersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_services);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipe_refresh);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        offersRecyclerView.setLayoutManager(linearLayoutManager);
        offersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        offersAdapter = new OffersAdapter(getActivity(), sentOffersList, mUserLat, mUserLng, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.upper_view:
                        detailOffer(position);
                        break;

                    case R.id.offer_btn_accept:
                        detailOffer(position);
                        break;

                    case R.id.txt_seller_name:
                        moveToProfile(position);
                        break;

                    case R.id.img_chat_btn:
                        moveToChat(position);
                        break;

                    case R.id.img_call_btn:
                        moveToCall(position);
                        break;
                }
            }
        });
        offersRecyclerView.setAdapter(offersAdapter);

        offersRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getSentOffersApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getSentOffersApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                getSentOffersApiCall(true, "");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getSentOffersApiCall(true, "");
    }

    private void getLoginUserId() {
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            userDetails = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(userDetails);

                if (jsonObject.has("_id")) {
                    if (jsonObject.get("_id") != null && !jsonObject.get("_id").toString().isEmpty())
                        mUserId = jsonObject.get("_id").toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void detailOffer(int position) {
//        if (sentOffersList.get(position).getOfferId() != null && !sentOffersList.get(position).getOfferId().isEmpty()) {
//            String offerId = sentOffersList.get(position).getOfferId();
//
//            if (sentOffersList.get(position).getSellerId() != null && sentOffersList.get(position).getSellerId().equals(mUserId)) {
//                Intent intent = new Intent(getActivity(), SendCustomOfferActivity.class);
//                intent.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
//                intent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 3);
//                startActivity(intent);
//            } else {
//                Intent intent = new Intent(getActivity(), CustomOfferDetailsActivity.class);
//                intent.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
//                startActivity(intent);
//            }
//        } else {
//            Toast.makeText(getActivity(), "Can't get the detail info", Toast.LENGTH_SHORT).show();
//        }

            if (position % 2 == 0) {
                Intent intent = new Intent(getActivity(), SendCustomOfferActivity.class);
                intent.putExtra(ConstantUtils.SELECT_OFFER_ID, "0");
                intent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 3);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), CustomOfferDetailsActivity.class);
                intent.putExtra(ConstantUtils.SELECT_OFFER_ID, "1");
                startActivity(intent);
            }
    }

    private void moveToChat(int position) {
        if (sentOffersList.get(position).getBuyer() != null) {
            String strFirst = "", strLast = "";
            if (sentOffersList.get(position).getBuyer().getFirstName() != null && !sentOffersList.get(position).getBuyer().getFirstName().isEmpty()) {
                strFirst = sentOffersList.get(position).getBuyer().getFirstName();
            }
            if (sentOffersList.get(position).getBuyer().getLastName() != null && !sentOffersList.get(position).getBuyer().getLastName().isEmpty()) {
                strLast = sentOffersList.get(position).getBuyer().getLastName();
            }
            String strOtherName = strFirst + " " + strLast;

            String strOtherPic = "";
            if (sentOffersList.get(position).getBuyer().getProfilePic() != null && !sentOffersList.get(position).getBuyer().getProfilePic().isEmpty()) {
                strOtherPic = sentOffersList.get(position).getBuyer().getProfilePic();
                if (!strOtherPic.contains("https://s3.amazonaws.com")) {
//                    strOtherPic = "https://s3.amazonaws.com" + strOtherPic;
                }
            }

            String strOtherId = "";
            if (sentOffersList.get(position).getBuyer().getBuyerId() != null && !sentOffersList.get(position).getBuyer().getBuyerId().isEmpty()) {
                strOtherId = sentOffersList.get(position).getBuyer().getBuyerId();
            }

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            editor.putInt(ConstantUtils.USER_VERIFIED, 0).apply();
            editor.putString(ConstantUtils.CHAT_USER_ID, strOtherId).apply();
            editor.putString(ConstantUtils.CHAT_USER_NAME, strOtherName).apply();
            editor.putString(ConstantUtils.CHAT_USER_PIC, strOtherPic).apply();
            editor.putString(ConstantUtils.CHAT_CONVERSATION_ID, "");
            startActivity(intent);
        }
    }

    private void moveToCall(int position) {
        if (sentOffersList.get(position).getBuyer() != null) {
            String strPhone = "";
            if (sentOffersList.get(position).getBuyer().getPhone() != null && !sentOffersList.get(position).getBuyer().getPhone().isEmpty()) {
                strPhone = sentOffersList.get(position).getBuyer().getPhone();

                if (strPhone.contains("+")) {
                    strPhone = strPhone.substring(1);
                }
            }

            if (!strPhone.equals("")) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strPhone));
                startActivity(callIntent);
            }
        }
    }

    private void moveToProfile(int position) {
        if (sentOffersList.get(position).getBuyer() != null) {
            String strId = "";
            if (sentOffersList.get(position).getBuyer().getBuyerId() != null && !sentOffersList.get(position).getBuyer().getBuyerId().isEmpty()) {
                strId = sentOffersList.get(position).getBuyer().getBuyerId();
            }

            if (!strId.equals("")) {
                Intent intent = new Intent(getActivity(), ProfileScreenActivity.class);
                intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
                intent.putExtra(ConstantUtils.PROFILE_USERID, strId);
                startActivity(intent);
            }
        }
    }

    private void getSentOffersApiCall(final boolean inited, String lastId) {
        if (inited) {
            sentOffersList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetSentOfferResponse> getOffersSentCall = apiService.getSentOffers(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getOffersSentCall.enqueue(new Callback<GetSentOfferResponse>() {
            @Override
            public void onResponse(Call<GetSentOfferResponse> call, Response<GetSentOfferResponse> response) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    limitCnt = response.body().getLimit();
                    totalCnt = response.body().getTotal();
                    lastDocId = response.body().getLastDocId();

                    sentOffersList.addAll(response.body().getDocs());
                    offersAdapter.notifyItemRangeInserted(offersAdapter.getItemCount(), sentOffersList.size()-1);

                    if (inited) {
                        offersAdapter.notifyDataSetChanged();
                        if (sentOffersList.size() == 0) {
                            txtNotFound.setVisibility(View.VISIBLE);
                            txtNotFound.setText("No offer found");
                        } else {
                            txtNotFound.setVisibility(View.GONE);
                        }
                    }
                }
                else if(response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersSentApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(OffersFragment.this);
                }
                else if(response.code() == 404) {
                    txtNotFound.setVisibility(View.VISIBLE);
                    txtNotFound.setText("No offer found");
                }
            }

            @Override
            public void onFailure(Call<GetSentOfferResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && view != null)
//            getSentOffersApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersSentApi")) {
//            getSentOffersApiCall(true, "");
        }
    }
}
