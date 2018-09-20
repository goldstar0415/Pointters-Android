package com.pointters.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.pointters.adapter.CustomOffersAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.ReceivedOfferModel;
import com.pointters.model.response.GetReceivedOffersResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prashantkumar on 26/9/17.
 */

public class CustomOffersFragment extends Fragment implements OnApiFailDueToSessionListener {

    private View view;
    private RecyclerView offersRecyclerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<ReceivedOfferModel> receivedOffersList = new ArrayList<>();
    private CustomOffersAdapter customOffersAdapter;
    private TextView txtNotFound;
    private SwipyRefreshLayout refreshLayout;
    private KProgressHUD loader;

    private Double mUserLat = 0.0;
    private Double mUserLng = 0.0;

    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;
    private final int CALL_PHONE_REQUEST = 3;
    private int selectedPosition = 0;

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
        customOffersAdapter = new CustomOffersAdapter(getActivity(), receivedOffersList, mUserLat, mUserLng, new OnRecyclerViewButtonClickListener(){
            @Override
            public void onButtonClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.upper_view:
                        detailOffer(position);
                        break;

                    case R.id.offer_btn_accept:
                        detailOffer(position);
                        break;

                    case R.id.img_call_btn:
                        selectedPosition = position;
                        moveToCall(position);
                        break;

                    case R.id.img_chat_btn:
                        moveToChat(position);
                        break;

                    case R.id.txt_buyer_name:
                        moveToProfile(position);
                        break;
                }
            }

        });
        offersRecyclerView.setAdapter(customOffersAdapter);

        offersRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt) {
                    getOffersReceivedApiCall(false, lastDocId);
                }
            }
        });

        loader.show();
        getOffersReceivedApiCall(true, "");
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                getOffersReceivedApiCall(true, "");
            }
        });
    }

    private void detailOffer(int position) {
        if (receivedOffersList.get(position).getOfferId() != null && !receivedOffersList.get(position).getOfferId().isEmpty()) {
            String offerId = receivedOffersList.get(position).getOfferId();

            Intent intent = new Intent(getActivity(), CustomOfferDetailsActivity.class);
            intent.putExtra(ConstantUtils.SELECT_OFFER_ID, offerId);
            intent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 1);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Can't get the detail info", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToChat(int position) {
        if (receivedOffersList.get(position).getSeller() != null) {
            String strFirst = "", strLast = "";
            if (receivedOffersList.get(position).getSeller().getFirstName() != null && !receivedOffersList.get(position).getSeller().getFirstName().isEmpty()) {
                strFirst = receivedOffersList.get(position).getSeller().getFirstName();
            }
            if (receivedOffersList.get(position).getSeller().getLastName() != null && !receivedOffersList.get(position).getSeller().getLastName().isEmpty()) {
                strLast = receivedOffersList.get(position).getSeller().getLastName();
            }
            String strOtherName = strFirst + " " + strLast;

            String strOtherPic = "";
            if (receivedOffersList.get(position).getSeller().getProfilePic() != null && !receivedOffersList.get(position).getSeller().getProfilePic().isEmpty()) {
                strOtherPic = receivedOffersList.get(position).getSeller().getProfilePic();
                if (!strOtherPic.contains("https://s3.amazonaws.com")) {
//                    strOtherPic = "https://s3.amazonaws.com" + strOtherPic;
                }
            }

            String strOtherId = "";
            if (receivedOffersList.get(position).getSeller().getSellerId() != null && !receivedOffersList.get(position).getSeller().getSellerId().isEmpty()) {
                strOtherId = receivedOffersList.get(position).getSeller().getSellerId();
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
        if(!checkPhoneCallPermission()) {
            return;
        }
        if (receivedOffersList.get(position).getSeller() != null) {
            String strPhone = "";
            if (receivedOffersList.get(position).getSeller().getPhone() != null && !receivedOffersList.get(position).getSeller().getPhone().isEmpty()) {
                strPhone = receivedOffersList.get(position).getSeller().getPhone();

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

    private boolean checkPhoneCallPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveToCall(selectedPosition);
                }
                break;

            default:
                break;
        }
    }

    private void moveToProfile(int position) {
        if (receivedOffersList.get(position).getSeller() != null) {
            String strId = "";
            if (receivedOffersList.get(position).getSeller().getSellerId() != null && !receivedOffersList.get(position).getSeller().getSellerId().isEmpty()) {
                strId = receivedOffersList.get(position).getSeller().getSellerId();
            }

            if (!strId.equals("")) {
                Intent intent = new Intent(getActivity(), ProfileScreenActivity.class);
                intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
                intent.putExtra(ConstantUtils.PROFILE_USERID, strId);
                startActivity(intent);
            }
        }
    }

    private void getOffersReceivedApiCall(final boolean inited, String lastId) {
        if (inited) {
            receivedOffersList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<GetReceivedOffersResponse> getOffersReceivedCall = apiService.getReceivedOffer(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), lastId);
        getOffersReceivedCall.enqueue(new Callback<GetReceivedOffersResponse>() {
            @Override
            public void onResponse(Call<GetReceivedOffersResponse> call, Response<GetReceivedOffersResponse> response) {
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

                    receivedOffersList.addAll(response.body().getDocs());
                    customOffersAdapter.notifyItemRangeInserted(customOffersAdapter.getItemCount(), receivedOffersList.size()-1);

                    if (inited && receivedOffersList.size() == 0) {
                        txtNotFound.setText("No offer found");
                        txtNotFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNotFound.setVisibility(View.GONE);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(getActivity(), "callGetOffersReceivedApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(CustomOffersFragment.this);
                }
                else if (response.code() == 404) {
                    txtNotFound.setText("No offer found");
                    txtNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetReceivedOffersResponse> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser && view != null)
//            getOffersReceivedApiCall(true, "");
    }

    @Override
    public void onApiFail(String apiSource) {
        if(apiSource.equals("callGetOffersReceivedApi")) {
//            getOffersReceivedApiCall(true, "");
        }
    }
}
