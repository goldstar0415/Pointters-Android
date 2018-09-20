package com.pointters.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.adapter.ChatMessageAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.ChatMessageModel;
import com.pointters.model.ChatMsgModel;
import com.pointters.model.ChatOfferModel;
import com.pointters.model.ChatResultModel;
import com.pointters.model.ChatServiceModel;
import com.pointters.model.CustomOfferModels;
import com.pointters.model.Media;
import com.pointters.model.UserChatModel;
import com.pointters.model.response.GetChatHistoryResponse;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.CallLoginApiIfFails;
import com.pointters.utils.CommonUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.EndlessRecyclerViewScrollListener;
import com.pointters.utils.GPSTracker;
import com.pointters.utils.SocketManager;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by mac on 12/25/17.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, OnApiFailDueToSessionListener {

    private final int SEND_SERVICE_REQUEST = 10;
    private final int CUSTOM_OFFER_REQUEST = 20;
    private final int CAMERA_IMAGE_REQUEST = 30;
    private final int CAMERA_VIDEO_REQUEST = 40;
    private final int PERMISSION_REQUEST_CAMERA = 16;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TransferUtility transferUtility;
    private KProgressHUD loader;
    private RecyclerView chatRecycler;
    private TextView txtTitle;
    private EditText editMsg;
    private ImageView btnSend;
    private RoundedImageView imgProfile;
    private LinearLayout layoutTabs;
    private RelativeLayout tabSend, tabCustom, tabCamera, tabPhoto;
    private String uncompressedFilePath;
    private FFmpeg ffmpeg;
    private String OBJECT_KEY, filePath, fileUrl;
    private AmazonS3 s3;

    private String userDetails;
    private String mUserId = "";
    private String mUserPic = "";
    private String mUserFirst = "";
    private String mUserLast = "";

    private io.socket.client.Socket mSocket;
    private String conversationId = "";
    private String otherUserId = "";
    private String otherUserPic = "";

    private String strMsg = "";

    List<ChatResultModel> chatHistoryList = new ArrayList<>();
    ChatMessageAdapter chatMessageAdapter;

    private Boolean isPagination = false;
    private String lastDocId = "";
    private int limitCnt = 0;
    private int totalCnt = 0;

    private String buyer_id, offer_id, job_id;
    private int sell_offer_post = -1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPreferences = getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        buyer_id = getIntent().getStringExtra(ConstantUtils.BUYER);
        offer_id = getIntent().getStringExtra(ConstantUtils.SELECT_OFFER_ID);
        job_id = getIntent().getStringExtra(ConstantUtils.SELECT_JOB_ID);
        sell_offer_post = getIntent().getIntExtra(ConstantUtils.SELL_OFFER_POST, -1);

        loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        loader.show();

        findViewById(R.id.toolbar_lft_img).setOnClickListener(this);

        txtTitle = (TextView) findViewById(R.id.toolbar_title);
        txtTitle.setText(sharedPreferences.getString(ConstantUtils.CHAT_USER_NAME, ""));

        String userPic = sharedPreferences.getString(ConstantUtils.CHAT_USER_PIC, "");
        imgProfile = (RoundedImageView) findViewById(R.id.toolbar_right_img);
        if (!userPic.equals("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.user_avatar_placeholder)
                    .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                    .showImageOnFail(R.drawable.user_avatar_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoader.getInstance().displayImage(userPic, imgProfile, options);
        }

        getLoginUserInfo();

        //Set Up Aws S3 Bucket
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        s3 = new AmazonS3Client(basicAWSCredentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        mSocket = SocketManager.getInstance().getSocket();
        if (!mSocket.connected()) {
            SocketManager.getInstance().init(sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
        }

        initUI();
        processMessage();

        String convId = sharedPreferences.getString(ConstantUtils.CHAT_CONVERSATION_ID, "");
        otherUserId = sharedPreferences.getString(ConstantUtils.CHAT_USER_ID, "");
        if (buyer_id == null) {
            buyer_id = otherUserId;
        }

        getSocketForChatting(convId, otherUserId);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ProfileScreenActivity.class);
                intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
                intent.putExtra(ConstantUtils.PROFILE_USERID, otherUserId);
                startActivity(intent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI() {
        editMsg = (EditText) findViewById(R.id.edit_send_msg);
        editMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strMsg = String.valueOf(s);
                if (!strMsg.equals("")) {
                    btnSend.setImageResource(R.drawable.chat_message_icon);
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setImageResource(R.drawable.chat_message_icon_grey);
                    btnSend.setEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend = (ImageView) findViewById(R.id.btn_send_msg);
        btnSend.setOnClickListener(this);
        btnSend.setEnabled(false);

        chatRecycler = (RecyclerView) findViewById(R.id.recycler_chat_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatRecycler.setLayoutManager(linearLayoutManager);
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.bg_transparent_divider));
        chatRecycler.addItemDecoration(divider);
        chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatHistoryList, mUserId, CUSTOM_OFFER_REQUEST);
        chatRecycler.setAdapter(chatMessageAdapter);
        chatRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
        chatRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalCnt > limitCnt && isPagination) {
                    isPagination = false;
                    loader.show();
                    callGetMessages(false, lastDocId, conversationId);
                }
            }
        });

        layoutTabs = (LinearLayout) findViewById(R.id.layout_tabbar);
        layoutTabs.setVisibility(View.VISIBLE);

        tabSend = (RelativeLayout) findViewById(R.id.chat_send_tool);
        tabSend.setOnClickListener(this);
        tabCustom = (RelativeLayout) findViewById(R.id.chat_custom_tool);
        tabCustom.setOnClickListener(this);
        tabCamera = (RelativeLayout) findViewById(R.id.chat_camera_tool);
        tabCamera.setOnClickListener(this);
        tabPhoto = (RelativeLayout) findViewById(R.id.chat_photo_tool);
        tabPhoto.setOnClickListener(this);
    }

    private void getLoginUserInfo() {
        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            userDetails = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(userDetails);

                if (jsonObject.has("_id")) {
                    if (jsonObject.get("_id") != null && !jsonObject.get("_id").toString().isEmpty())
                        mUserId = jsonObject.get("_id").toString();
                }

                if (jsonObject.has("firstName")) {
                    if (jsonObject.get("firstName") != null && !jsonObject.get("firstName").toString().isEmpty())
                        mUserFirst = jsonObject.get("firstName").toString();
                }

                if (jsonObject.has("lastName")) {
                    if (jsonObject.get("lastName") != null && !jsonObject.get("lastName").toString().isEmpty())
                        mUserLast = jsonObject.get("lastName").toString();
                }

                if (jsonObject.has("profilePic")) {
                    if (jsonObject.get("profilePic") != null && !jsonObject.get("profilePic").toString().isEmpty()) {
                        mUserPic = jsonObject.get("profilePic").toString();
                        if (!mUserPic.contains("https://s3.amazonaws.com")) {
//                            mUserPic = "https://s3.amazonaws.com" + mUserPic;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getSocketForChatting(String convId, String otherId) {
        JSONArray arrUsers = new JSONArray();
        arrUsers.put(mUserId);
        arrUsers.put(otherId);

        SocketManager.getInstance().setStartConversation(convId, arrUsers);
    }

    private void processMessage() {
        mSocket.on("start_conversation", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data;
                        try {
                            data = (JSONObject) args[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (data == null)
                            return;
                        getChatHistory(data);
                    }
                });
            }
        });

        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data;
                        try {
                            data = (JSONObject) args[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (data == null)
                            return;
                        receivedMessage(data);
                    }
                });
            }
        });
    }

    private void getChatHistory(JSONObject jsonObject) {
        try {
            if (jsonObject.has("conversationId")) {
                if (jsonObject.get("conversationId") != null && !jsonObject.get("conversationId").toString().isEmpty()) {
                    conversationId = jsonObject.get("conversationId").toString();
                    callGetMessages(true, "", conversationId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receivedMessage(JSONObject jsonObject) {
        try {
            if (jsonObject.has("result") && jsonObject.get("result") != null) {
                JSONObject result = (JSONObject) jsonObject.get("result");

                if (result.has("message") && result.get("message") != null) {
                    JSONObject jsonMessage = (JSONObject) result.get("message");

                    Gson gson = new Gson();
                    ChatMsgModel objMessage = gson.fromJson(String.valueOf(jsonMessage), ChatMsgModel.class);

                    UserChatModel objUser = new UserChatModel();
                    objUser.setUserId(otherUserId);
                    objUser.setProfilePic(otherUserPic);

                    ChatMessageModel objResult = new ChatMessageModel();
                    objResult.setMessage(objMessage);
                    objResult.setUser(objUser);

                    ChatResultModel objInfo = new ChatResultModel();
                    objInfo.setResult(objResult);

                    addMessageBySocket(objInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessageBySocket(ChatResultModel data) {
        chatHistoryList.add(0, data);
        chatMessageAdapter.notifyDataSetChanged();
        chatRecycler.scrollToPosition(0);
    }

    private String getCurrentDateTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);

        return df.format(new Date());
    }

    private void sendMessageText() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // send message to server
        JSONObject objSend = new JSONObject();
        try {
            objSend.put("userId", mUserId);
            objSend.put("conversationId", conversationId);
            objSend.put("messageText", strMsg);

            SocketManager.getInstance().sendMessage(objSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show the message
        ChatMsgModel objMessage = new ChatMsgModel();
        objMessage.setMessageText(strMsg);
        objMessage.setCreatedAt(getCurrentDateTime());

        UserChatModel objUser = new UserChatModel();
        objUser.setUserId(mUserId);
        objUser.setProfilePic(mUserPic);

        ChatMessageModel objResult = new ChatMessageModel();
        objResult.setMessage(objMessage);
        objResult.setUser(objUser);

        ChatResultModel objInfo = new ChatResultModel();
        objInfo.setResult(objResult);

        addMessageBySocket(objInfo);

        // initialize after sending
        strMsg = "";
        editMsg.setText("");
        btnSend.setImageResource(R.drawable.chat_message_icon_grey);
        btnSend.setEnabled(false);
    }

    private void sendMessageForMedia(String fileName, String type) {
        // send message to server
        JSONObject objMedia = new JSONObject();
        try {
            objMedia.put("mediaType", type);
            objMedia.put("fileName", fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject objSend = new JSONObject();
        try {
            objSend.put("userId", mUserId);
            objSend.put("conversationId", conversationId);
            JSONArray arrMedia = new JSONArray();
            arrMedia.put(objMedia);
            objSend.put("media", arrMedia);

            SocketManager.getInstance().sendMessage(objSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show the message
        List<Media> arrMedia = new ArrayList<>();
        arrMedia.add(new Media(fileName, type));

        ChatMsgModel objMessage = new ChatMsgModel();
        objMessage.setMedia(arrMedia);
        objMessage.setCreatedAt(getCurrentDateTime());

        UserChatModel objUser = new UserChatModel();
        objUser.setUserId(mUserId);
        objUser.setProfilePic(mUserPic);

        ChatMessageModel objResult = new ChatMessageModel();
        objResult.setMessage(objMessage);
        objResult.setUser(objUser);

        ChatResultModel objInfo = new ChatResultModel();
        objInfo.setResult(objResult);

        addMessageBySocket(objInfo);
    }

    private void sendMessageForService(ChatServiceModel service) {
        if (service == null) { return; }

        // send message to server
        JSONObject objSend = new JSONObject();
        try {
            if (service.getServiceId() != null && !service.getServiceId().isEmpty()) {
                objSend.put("userId", mUserId);
                objSend.put("conversationId", conversationId);
                objSend.put("serviceId", service.getServiceId());

                SocketManager.getInstance().sendMessage(objSend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show the message
        ChatMsgModel objMessage = new ChatMsgModel();
        objMessage.setService(service);
        objMessage.setCreatedAt(getCurrentDateTime());

        UserChatModel objUser = new UserChatModel();
        objUser.setUserId(mUserId);
        objUser.setProfilePic(mUserPic);

        ChatMessageModel objResult = new ChatMessageModel();
        objResult.setMessage(objMessage);
        objResult.setUser(objUser);

        ChatResultModel objInfo = new ChatResultModel();
        objInfo.setResult(objResult);

        addMessageBySocket(objInfo);
    }

    private void sendMessageForOffer(String offerId, CustomOfferModels offerInfo, ChatServiceModel offerService, Boolean linked) {
        if (offerId.equals("") || offerInfo == null) { return; }

        // send message to server
        JSONObject objSend = new JSONObject();
        try {
            objSend.put("userId", mUserId);
            objSend.put("conversationId", conversationId);
            objSend.put("offerId", offerId);

            SocketManager.getInstance().sendMessage(objSend);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show the message
        ChatOfferModel objOffer = new ChatOfferModel();
        objOffer.setOfferId(offerId);
        objOffer.setPrice(offerInfo.getPrice());
        objOffer.setCurrencySymbol(offerInfo.getCurrencySymbol());
        objOffer.setWorkDuration(offerInfo.getWorkDuration());
        objOffer.setWorkDurationUom(offerInfo.getWorkDurationUom());
        if (linked) { objOffer.setService(offerService); }

        ChatMsgModel objMessage = new ChatMsgModel();
        objMessage.setOffer(objOffer);
        objMessage.setCreatedAt(getCurrentDateTime());

        UserChatModel objUser = new UserChatModel();
        objUser.setUserId(mUserId);
        objUser.setProfilePic(mUserPic);

        ChatMessageModel objResult = new ChatMessageModel();
        objResult.setMessage(objMessage);
        objResult.setUser(objUser);

        ChatResultModel objInfo = new ChatResultModel();
        objInfo.setResult(objResult);

        addMessageBySocket(objInfo);
    }

    //Dialog Box
    private void showDiag(final Boolean isCamera) {
        final View dialogView = View.inflate(this, R.layout.dialog, null);

        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final View viewDialog = dialogView.findViewById(R.id.dialog);

        RelativeLayout relativeLayoutFullPicker = (RelativeLayout) dialog.findViewById(R.id.layout_full_picker_title);
        RelativeLayout relativeLayoutDialog = (RelativeLayout) dialog.findViewById(R.id.layout_picker);
        LinearLayout linearImage = (LinearLayout) dialog.findViewById(R.id.linear_image);
        LinearLayout linearVideo = (LinearLayout) dialog.findViewById(R.id.linear_video);

        relativeLayoutFullPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });

        relativeLayoutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing
            }
        });

        linearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                if (isCamera) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
                } else {
                    new ImagePicker.Builder(ChatActivity.this)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .mode(ImagePicker.Mode.GALLERY)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.JPG)
                            .allowMultipleImages(false)
                            .enableDebuggingMode(true)
                            .build();
                }
            }
        });

        linearVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                if (isCamera) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
                } else {
                    new VideoPicker.Builder(ChatActivity.this)
                            .mode(VideoPicker.Mode.GALLERY)
                            .directory(VideoPicker.Directory.DEFAULT)
                            .extension(VideoPicker.Extension._MP4)
                            .build();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int cy = height / 2;
        int cx = width / 2;

        if (b) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
                view.setVisibility(View.VISIBLE);
                revealAnimator.setDuration(700);
                revealAnimator.start();
            } else {
                view.setVisibility(View.VISIBLE);
            }

        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        dialog.dismiss();
                        view.setVisibility(View.INVISIBLE);

                    }
                });
                anim.setDuration(500);
                anim.start();
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(ChatActivity.this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d("ffmpeg4", "onFailure: ");
                    // showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg4", "onSuccess: ");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //  showUnsupportedExceptionDialog();
        } catch (Exception e) {
        }
    }
    //FFmpeg

    public void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    if (loader.isShowing()) { loader.dismiss(); }
                    Log.d("ffmpeg4", "finalfailed: " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d("ffmpeg4", "finalSuccess: " + s);
                    //Perform action on success
                    OBJECT_KEY = generateFileName();
                    fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                    uploadFilesToAws(getResources().getString(R.string.video));
                }

                @Override
                public void onFinish() {
                    Log.d("ffmpeg4", "finish: ");
                }

                @Override
                public void onProgress(String s) {
                }

                @Override
                public void onStart() {
                    Log.d("ffmpeg4", "start: ");
                }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("ffmpeg4", "eroro: " + e.getMessage());
        }
    }

    private String generateFileName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        return simpleDateFormat.format(calendar.getTime());
    }

    public File saveBitmapToFile(File file) {
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            inputStream = null;

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            if (originalWidth > 160) {
                int reqWidth = 640;
                int reqHeight = (reqWidth * originalHeight) / originalWidth;

                // decode full image pre-resized
                inputStream = new FileInputStream(file);
                options = new BitmapFactory.Options();

                // calc rought re-size (this is no exact resize)
                options.inSampleSize = Math.max(originalWidth / reqWidth, originalHeight / reqHeight);
                // decode full image
                Bitmap roughBitmap = BitmapFactory.decodeStream(inputStream, null, options);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
                RectF outRect = new RectF(0, 0, reqWidth, reqHeight);
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);

                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

                // override resized bitmap image
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            }

            return file;

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
            return null;
        }
    }

    private void sendFile(File file) {
        saveBitmapToFile(file);

        filePath = file.getAbsolutePath();
        OBJECT_KEY = generateFileName();
        fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;

        loader.show();
        uploadFilesToAws(getResources().getString(R.string.image));
    }

    private void uploadFilesToAws(final String mediaType) {
        transferUtility = new TransferUtility(s3, getApplicationContext());

        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(filePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    if (loader.isShowing()) { loader.dismiss(); }
                    sendMessageForMedia(fileUrl, mediaType);
                } else if (state.equals(TransferState.FAILED)) {
                    if (loader.isShowing()) { loader.dismiss(); }
                    Toast.makeText(ChatActivity.this, "Uploading failed. Please try again!", Toast.LENGTH_SHORT).show();
                    //AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {
                if (loader.isShowing()) { loader.dismiss(); }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_msg:
                sendMessageText();
                break;

            case R.id.toolbar_lft_img:
                onBackPressed();
                break;

            case R.id.chat_send_tool:
                Intent sendIntent = new Intent(ChatActivity.this, SendServiceActivity.class);
                editor.putString(ConstantUtils.SERVICE_TYPE, "Send Service").apply();
                startActivityForResult(sendIntent, SEND_SERVICE_REQUEST);
                break;

            case R.id.chat_custom_tool:
                Intent customIntent = new Intent(ChatActivity.this, SendCustomOfferActivity.class);

                if (offer_id != null) {
                    if (offer_id.equals("")) {
                        customIntent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 1);
                        customIntent.putExtra(ConstantUtils.SELECT_JOB_ID, job_id);
                    } else {
                        customIntent.putExtra(ConstantUtils.CHAT_OFFER_DIRECTION, 2);
                        customIntent.putExtra(ConstantUtils.SELECT_OFFER_ID, offer_id);
                    }
                }
                customIntent.putExtra(ConstantUtils.SELL_OFFER_POST, sell_offer_post);
                customIntent.putExtra(ConstantUtils.BUYER, buyer_id);
                startActivityForResult(customIntent, CUSTOM_OFFER_REQUEST);
                break;

            case R.id.chat_camera_tool:
                if (checkCameraPermission()) {
                    showDiag(true);
                }
                break;

            case R.id.chat_photo_tool:
                showDiag(false);
                break;


            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEND_SERVICE_REQUEST:
                    Gson gsonService = new Gson();
                    String strService = data.getStringExtra(ConstantUtils.CHAT_SEND_SERVICE);
                    ChatServiceModel selService = gsonService.fromJson(strService, ChatServiceModel.class);
                    sendMessageForService(selService);
                    break;

                case CUSTOM_OFFER_REQUEST:
                    Gson gsonOffer = new Gson();
                    String strOfferId = data.getStringExtra(ConstantUtils.CHAT_CUSTOM_OFFER_ID);
                    String strOfferInfo = data.getStringExtra(ConstantUtils.CHAT_CUSTOM_OFFER_INFO);
                    CustomOfferModels selOfferInfo = gsonOffer.fromJson(strOfferInfo, CustomOfferModels.class);
                    Boolean isLinked = data.getBooleanExtra(ConstantUtils.CHAT_CUSTOM_OFFER_LINKED, false);

                    ChatServiceModel selOfferService = null;
                    if (isLinked) {
                        String strOfferService = data.getStringExtra(ConstantUtils.CHAT_CUSTOM_OFFER_SERVICE);
                        selOfferService = gsonOffer.fromJson(strOfferService, ChatServiceModel.class);
                    }

                    sendMessageForOffer(strOfferId, selOfferInfo, selOfferService, isLinked);
                    break;

                case ImagePicker.IMAGE_PICKER_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);

                        if (mPaths != null) {
                            loader.show();
                            filePath = mPaths.get(0);
                            saveBitmapToFile(new File(filePath));
                            OBJECT_KEY = generateFileName();
                            fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                            uploadFilesToAws(getResources().getString(R.string.image));
                        }
                    }
                    break;

                case VideoPicker.VIDEO_PICKER_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        uncompressedFilePath = (String) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);

                        if (uncompressedFilePath != null) {
                            MediaPlayer mp = MediaPlayer.create(ChatActivity.this, Uri.parse(uncompressedFilePath));
                            int duration = mp.getDuration();
                            mp.release();

                            if ((duration / 1000) > 10) {
                                AndroidUtils.showToast(ChatActivity.this, "Duration of video can not be exceed by 10 sec");
                            }
                            else {
                                loader.show();
                                loadFFMpegBinary();
                                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                                String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                                execFFmpegBinary(command);
                            }
                        }
                    }
                    break;

                case CAMERA_IMAGE_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
;                       new fileFromBitmap(bitmap, ChatActivity.this).execute();
                    }
                    break;

                case CAMERA_VIDEO_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Uri video;
                        if (data.getData() != null)
                            video = data.getData();
                        else if (data.getDataString() != null) {
                            String path = data.getDataString();
                            video = Uri.parse(path);
                        } else {
                            return;
                        }

                        loader.show();
                        uncompressedFilePath = CommonUtils.getVideoFilePathFromURI(ChatActivity.this, video);
                        loadFFMpegBinary();
                        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressed.mp4";
                        String[] command = {"-y", "-i", uncompressedFilePath, "-s", "640x480", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
                        execFFmpegBinary(command);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkCameraPermission()) {
                    showDiag(true);
                }
            }
        }
    }

    private boolean checkCameraPermission() {
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            return false;

        }
        return true;
    }

    void callGetMessages(final boolean inited, String lastId, String convId) {
        if (inited) {
            chatHistoryList.clear();
        }

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        final Call<GetChatHistoryResponse> chatHistoryRequest = apiService.getChatHistory(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), convId, lastId);
        chatHistoryRequest.enqueue(new Callback<GetChatHistoryResponse>() {
            @Override
            public void onResponse(Call<GetChatHistoryResponse> call, Response<GetChatHistoryResponse> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200 && response.body() != null) {
                    totalCnt = response.body().getTotal();
                    limitCnt = response.body().getLimit();
                    lastDocId = response.body().getLastDocId();

                    chatHistoryList.addAll(response.body().getDocs());
                    chatMessageAdapter.notifyItemRangeInserted(chatMessageAdapter.getItemCount(), chatHistoryList.size()-1);
                    isPagination = true;

                    if (inited && chatHistoryList.size() > 0) {
                        chatRecycler.scrollToPosition(0);
                    }
                }
                else if (response.code() == 401) {
                    CallLoginApiIfFails callLoginApiIfFails = new CallLoginApiIfFails(ChatActivity.this, "callGetMessagesApi");
                    callLoginApiIfFails.OnApiFailDueToSessionListener(ChatActivity.this);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<GetChatHistoryResponse> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(ChatActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing()) { loader.dismiss(); }
        Log.e("Chat History Api Failed: ", apiSource);
    }

    //Capture Image
    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {
        Context context;
        Bitmap bitmap;
        File file;

        public fileFromBitmap(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            file = new File(getExternalFilesDir(null), "background_" + System.currentTimeMillis() + "_image.jpeg");

            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            sendFile(file);
        }
    }
}
