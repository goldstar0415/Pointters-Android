package com.pointters.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.TagServiceActivity;
import com.pointters.adapter.AddFragmentsInCrosswallPagerAdapter;
import com.pointters.listener.OnApiFailDueToSessionListener;
import com.pointters.model.Media;
import com.pointters.model.Tags;
import com.pointters.model.request.PostUpdateRequest;
import com.pointters.rest.ApiClient;
import com.pointters.rest.ApiInterface;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.CustomTabLayout;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.crosswall.lib.coverflow.CoverFlow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by mac on 12/7/17.
 */

public class PostUpdateFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, OnApiFailDueToSessionListener {

    public static final String APP_DIR = "VideoCompressor";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed Videos/";
    public static final String TEMP_DIR = "/Temp/";

    private static final int REQUEST_FOR_TAG_CODE = 3;

    private View view;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CustomTabLayout tabLayout;
    private AddFragmentsInCrosswallPagerAdapter addFragmentsInCrosswallPagerAdapter;
    private RelativeLayout layoutCamera, layoutCrossWall, layoutChooseFromGallery, layoutChooseTag, layoutPostTag;
    private EditText messgeEditText;
    private TextView txtTimer, btnPost;
    private CameraView cameraPreview;
    private Boolean IS_RECORDING_START = false;
    private CountDownTimer countDownTimer;
    private ImageView takeImage;
    private ViewPager containerViewPager;
    private Runnable updateRecordingThread;
    private KProgressHUD loader;
    private String OBJECT_KEY, filePath, fileUrl;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private BroadcastReceiver message;
    private Media media;
    private String uncompressedFilePath;
    private FFmpeg ffmpeg;
    private ArrayList<Media> backgroundMedia = new ArrayList<>();

    private String postTagId = "";
    private String postTagType = "";
    private String postMessage = "";
    private Integer postMediaId = -1;
    private String postMediaURL = "";
    private String postMediaType = "";
    private boolean isConnectedTag = false;

    public static void try2CreateCompressDir() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        f.mkdirs();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_update, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initViews();
        setOnClickListners();

        tabLayout.addOnTabSelectedListener(this);

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        //Set Up Crosswall
        addFragmentsInCrosswallPagerAdapter = new AddFragmentsInCrosswallPagerAdapter(getChildFragmentManager(), backgroundMedia, getActivity(), "ADDSERVICE");
        containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
        containerViewPager.setClipChildren(false);
        containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());

        if (backgroundMedia != null && backgroundMedia.size() > 0) {
            layoutChooseFromGallery.setVisibility(View.GONE);
            layoutCrossWall.setVisibility(View.VISIBLE);
            layoutChooseTag.setVisibility(View.VISIBLE);
        } else {
            layoutChooseFromGallery.setVisibility(View.VISIBLE);
            layoutCrossWall.setVisibility(View.GONE);
            layoutChooseTag.setVisibility(View.GONE);
        }

        new CoverFlow.Builder()
                .with(containerViewPager)
                .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                .scale(0.3f)
                .spaceSize(0f)
                .rotationY(0f)
                .build();

        //Set Up Aws S3 Bucket
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ConstantUtils.AWS_ACCESS_KEY, ConstantUtils.AWS_SECRATE_KEY);
        s3 = new AmazonS3Client(basicAWSCredentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        message = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                switch (intent.getAction()) {
                    case "delete":
                        if (backgroundMedia.size() > 0) {
                            backgroundMedia.remove(intent.getIntExtra(ConstantUtils.POSITION, 0));
                            getPostMediaInfo(backgroundMedia.size()-1);
                            layoutChooseTag.setVisibility(View.GONE);
                            allowPostUpdate();

                            containerViewPager.setAdapter(null);
                            containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
                            containerViewPager.setCurrentItem(addFragmentsInCrosswallPagerAdapter.getCount());
                            containerViewPager.setClipChildren(false);
                            containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());

                            new CoverFlow.Builder()
                                    .with(containerViewPager)
                                    .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                                    .scale(0.3f)
                                    .spaceSize(0f)
                                    .rotationY(0f)
                                    .build();

                            if (backgroundMedia != null && backgroundMedia.size() == 1) {
                                layoutChooseFromGallery.setVisibility(View.VISIBLE);
                                layoutCrossWall.setVisibility(View.GONE);
                            } else {
                                layoutChooseFromGallery.setVisibility(View.GONE);
                                layoutCrossWall.setVisibility(View.VISIBLE);
                            }
                        }
                        break;

                    case "pick":
                        showDiag();
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("pick");
        intentFilter.addAction("delete");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message, intentFilter);

        updateRecordingThread = new Runnable() {
            @Override
            public void run() {
                IS_RECORDING_START = false;
                takeImage.setImageResource(R.drawable.capture_button);
                cameraPreview.stopRecordingVideo();
            }
        };

        return view;
    }

    private void initViews() {
        //Tab Layout
        tabLayout = (CustomTabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("UPLOAD"));
        tabLayout.addTab(tabLayout.newTab().setText("SNAP PICTURE"));
        tabLayout.addTab(tabLayout.newTab().setText("TAKE VIDEO"));

        //Camera Layout
        layoutCamera = (RelativeLayout) view.findViewById(R.id.layout_camera);
        takeImage = (ImageView) view.findViewById(R.id.take_image);
        cameraPreview = (CameraView) view.findViewById(R.id.camera_preview);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        //Crosswall Layout
        layoutCrossWall = (RelativeLayout) view.findViewById(R.id.layout_crosswall);
        containerViewPager = (ViewPager) view.findViewById(R.id.container_viewpager);

        //Choose From Gallery Layout
        layoutChooseFromGallery = (RelativeLayout) view.findViewById(R.id.layout_choose_gallery);

        //Choose Tag Layout
        layoutChooseTag = (RelativeLayout) view.findViewById(R.id.layout_choose_tag);
        layoutPostTag = (RelativeLayout) view.findViewById(R.id.layout_post_tag);

        messgeEditText = (EditText) view.findViewById(R.id.edittext_post_messagge);
        messgeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postMessage = String.valueOf(s);
                allowPostUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnPost = (TextView) view.findViewById(R.id.btn_post);
    }

    private void setOnClickListners() {
        view.findViewById(R.id.img_choose_bg_images).setOnClickListener(this);
        containerViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                if (postMediaId != -1) {
                    Fragment fragment = addFragmentsInCrosswallPagerAdapter.getRegisteredFragment(postMediaId);
                    if (fragment instanceof VideoFragment) {
                        ((VideoFragment) fragment).stopPlayBack();
                    }
                }

                getPostMediaInfo(position);

                if (position < backgroundMedia.size()-1) {
                    layoutChooseTag.setVisibility(View.VISIBLE);
                } else {
                    layoutChooseTag.setVisibility(View.GONE);
                }

                allowPostUpdate();
            }
        });

        takeImage.setOnClickListener(this);
        layoutChooseTag.setOnClickListener(this);
        btnPost.setOnClickListener(this);
    }

    private void uploadFilesToAws(final String mediaType) {
        transferUtility = new TransferUtility(s3, getApplicationContext());
        TransferObserver observer = transferUtility.upload(ConstantUtils.MY_BUCKET, OBJECT_KEY, new File(filePath), CannedAccessControlList.PublicRead);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    loader.dismiss();

                    if (backgroundMedia.size() == 0) {
                        media = new Media("", getResources().getString(R.string.image));
                        backgroundMedia.add(media);
                    }

                    if (mediaType.equals(getResources().getString(R.string.image))) {
                        media = new Media(fileUrl, getResources().getString(R.string.image));
                        backgroundMedia.add((backgroundMedia.size() - 1), media);
                    } else if (mediaType.equals(getResources().getString(R.string.video))) {
                        media = new Media(fileUrl, getResources().getString(R.string.video));
                        backgroundMedia.add((backgroundMedia.size() - 1), media);
                    }

                    getPostMediaInfo(backgroundMedia.size() - 2);

                    containerViewPager.setAdapter(null);
                    // containerViewPager.setCurrentItem(backgroundMedia.size() - 1);

                    containerViewPager.setAdapter(addFragmentsInCrosswallPagerAdapter);
                    if (backgroundMedia.size() > 1)
                        containerViewPager.setCurrentItem(backgroundMedia.size() - 2);
                    containerViewPager.setClipChildren(false);
                    containerViewPager.setOffscreenPageLimit(addFragmentsInCrosswallPagerAdapter.getCount());

                    new CoverFlow.Builder()
                            .with(containerViewPager)
                            .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                            .scale(0.3f)
                            .spaceSize(0f)
                            .rotationY(0f)
                            .build();

                    if (backgroundMedia != null && backgroundMedia.size() > 0) {
                        layoutChooseFromGallery.setVisibility(View.GONE);
                        layoutCrossWall.setVisibility(View.VISIBLE);
                        layoutChooseTag.setVisibility(View.VISIBLE);
                    } else {
                        layoutChooseFromGallery.setVisibility(View.VISIBLE);
                        layoutCrossWall.setVisibility(View.GONE);
                        layoutChooseTag.setVisibility(View.GONE);
                    }

                    if (tabLayout != null) {
                        tabLayout.getTabAt(0).select();
                    }

                } else if (state.equals(TransferState.FAILED)) {
                    //AndroidUtils.showToast(RegistrationDetailsActivity.this, "Uploading failed please try again");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {}
        });
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.btn_post:
                loader.show();
                callPostUpdateApi();
                break;

            case R.id.layout_choose_tag:
                Intent intent = new Intent(getActivity(), TagServiceActivity.class);
                startActivityForResult(intent, REQUEST_FOR_TAG_CODE);
                break;

            case R.id.post_tag_close:
                isConnectedTag = false;
                layoutPostTag.setVisibility(View.GONE);
                postTagId = "";
                postTagType = "";
                allowPostUpdate();
                break;

            case R.id.take_image:
                if (tabLayout.getSelectedTabPosition() == 1) {
                    capturePhoto();
                }
                else if (tabLayout.getSelectedTabPosition() == 2) {
                    if (IS_RECORDING_START) {
                        if (countDownTimer != null)
                            countDownTimer.cancel();
                        takeImage.setImageResource(R.drawable.capture_button);
                        IS_RECORDING_START = false;
                        cameraPreview.stopRecordingVideo();
                        // cameraPreview.removeCallbacks(updateRecordingThread);
                    }
                    else {
                        takeImage.setImageResource(R.drawable.video_capture_stop_button);
                        cameraPreview.startRecordingVideo();
                        IS_RECORDING_START = true;
                        takeImage.setVisibility(View.GONE);
                        CountDownTimer countDownTimerOneSec = new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {}

                            @Override
                            public void onFinish() {
                                takeImage.setVisibility(View.VISIBLE);
                            }
                        }.start();

                        countDownTimer = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtTimer.setText("" + millisUntilFinished / 1000 + " Secs");
                            }

                            @Override
                            public void onFinish() {
                                IS_RECORDING_START = false;
                                takeImage.setImageResource(R.drawable.capture_button);
                                cameraPreview.stopRecordingVideo();
                            }
                        }.start();

                        //   cameraPreview.postDelayed(updateRecordingThread, 10000);
                        captureVideo();
                    }
                }
                break;

            case R.id.img_choose_bg_images:
                showDiag();
                break;
        }
    }

    private void callPostUpdateApi() {
        if (postMessage.equals(""))
            postMessage = null;

        ArrayList<Media> dictMedia = new ArrayList<>();
        if (!postMediaURL.equals("") && !postMediaType.equals(""))
            dictMedia.add(new Media(postMediaURL, postMediaType));

        ArrayList<Tags> dictTags = new ArrayList<>();
        if (!postTagId.equals("") && !postTagType.equals(""))
            dictTags.add(new Tags(postTagId, postTagType));

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(postMessage, dictMedia, dictTags);

        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
        Call<Object> addServiceRequestCall = apiService.postUpdate(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""), postUpdateRequest);
        addServiceRequestCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (loader.isShowing()) {
                    loader.dismiss();
                }

                if (response.code() == 200) {
                    Toast.makeText(getActivity(), "Update success!", Toast.LENGTH_LONG).show();

                    postMessage = "";
                    messgeEditText.setText("");

                    postTagId = "";
                    postTagType = "";
                    layoutPostTag.setVisibility(View.GONE);

                    postMediaId = -1;
                    postMediaURL = "";
                    postMediaType = "";

                    backgroundMedia.clear();
                    containerViewPager.setAdapter(null);

                    layoutCrossWall.setVisibility(View.GONE);
                    layoutChooseTag.setVisibility(View.GONE);
                    layoutChooseFromGallery.setVisibility(View.VISIBLE);

                    allowPostUpdate();
                }
                else {
                    Toast.makeText(getActivity(), "Update failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (loader.isShowing()) { loader.dismiss(); }
                Toast.makeText(getActivity(), "Update failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    void capturePhoto() {
        cameraPreview.captureImage();

        // Here is callback of snap taken
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                Matrix mat = new Matrix();
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    mat.postRotate(90);  // angle is the desired angle you wish to rotate
                } else {
                    mat.postRotate(0);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                new PostUpdateFragment.fileFromBitmap(bitmap, getActivity()).execute();
            }
        });
    }

    void captureVideo() {
        // Here is callback of video taken
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                filePath = video.getAbsolutePath();
                OBJECT_KEY = generateFileName();
                fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
                if (cameraPreview != null) {
                    cameraPreview.stop();
                }
                loader.show();

                uploadFilesToAws(getResources().getString(R.string.video));
            }
        });
    }

    private void sendFile(File file) {
        saveBitmapToFile(file);

        filePath = file.getAbsolutePath();
        OBJECT_KEY = generateFileName();
        fileUrl = "https://s3.amazonaws.com/pointters_dev/dev/" + OBJECT_KEY;
        if (cameraPreview != null) {
            cameraPreview.stop();
        }

        loader.show();
        uploadFilesToAws(getResources().getString(R.string.image));
    }

    //Dialog Box
    private void showDiag() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog, null);

        final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
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

                new ImagePicker.Builder(getActivity())
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .mode(ImagePicker.Mode.GALLERY)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        linearVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDialog.setVisibility(View.INVISIBLE);

                new VideoPicker.Builder(getActivity())
                        .mode(VideoPicker.Mode.GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension._MP4)
                        .build();
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

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

    private void setTagServiceView(String strId, String strType, String strName, String strPos, String strPic) {
        TextView txtTagName = (TextView) view.findViewById(R.id.post_tag_name);
        TextView txtTagPos = (TextView) view.findViewById(R.id.txt_tag_location);
        ImageView postImgService = (ImageView) view.findViewById(R.id.post_tag_profile_rect);
        RoundedImageView postImgUser = (RoundedImageView) view.findViewById(R.id.post_tag_profile_round);

        ImageView btnTagDelete = (ImageView) view.findViewById(R.id.post_tag_close);
        btnTagDelete.setOnClickListener(this);

        postTagId = strId;
        postTagType = strType;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.photo_placeholder)
                .showImageForEmptyUri(R.drawable.photo_placeholder)
                .showImageOnFail(R.drawable.photo_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (postTagType.equals("user")) {
            postImgService.setVisibility(View.GONE);
            postImgUser.setVisibility(View.VISIBLE);
            if (strPic != null && !strPic.equals(""))
                ImageLoader.getInstance().displayImage(strPic, postImgUser, options);
        }
        else {
            postImgUser.setVisibility(View.GONE);
            postImgService.setVisibility(View.VISIBLE);
            if (strPic != null && !strPic.equals(""))
                ImageLoader.getInstance().displayImage(strPic, postImgService, options);
        }

        txtTagName.setText(strName);
        txtTagPos.setText(strPos);
    }

    private void allowPostUpdate() {
        if (!postMessage.equals("") || (!postMediaURL.equals("") && !postMediaType.equals(""))) {
            btnPost.setSelected(true);
            btnPost.setEnabled(true);
        } else {
            btnPost.setSelected(false);
            btnPost.setEnabled(false);
        }
    }

    private void getPostMediaInfo(int index) {
        if (index < backgroundMedia.size()-1) {
            Media dictBackgroundMedia = backgroundMedia.get(index);
            postMediaId = index;
            if (dictBackgroundMedia.getMediaType() != null)
                postMediaType = dictBackgroundMedia.getMediaType();
            if (dictBackgroundMedia.getFileName() != null)
                postMediaURL = dictBackgroundMedia.getFileName();
        }
        else {
            postMediaId = -1;
            postMediaURL = "";
            postMediaType = "";
        }

        allowPostUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_TAG_CODE:
                    isConnectedTag = true;
                    layoutPostTag.setVisibility(View.VISIBLE);

                    String strId = data.getStringExtra(ConstantUtils.CHOOSE_TAG_ID);
                    String strType = data.getStringExtra(ConstantUtils.CHOOSE_TAG_TYPE);
                    String strName = data.getStringExtra(ConstantUtils.CHOOSE_TAG_NAME);
                    String strPos = data.getStringExtra(ConstantUtils.CHOOSE_TAG_POS);
                    String strPic = data.getStringExtra(ConstantUtils.CHOOSE_TAG_PIC);

                    setTagServiceView(strId, strType, strName, strPos, strPic);
                    allowPostUpdate();
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
                            MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(uncompressedFilePath));
                            int duration = mp.getDuration();
                            mp.release();

                            if ((duration / 1000) > 10) {
                                AndroidUtils.showToast(getActivity(), "Duration of video can not be exceed by 10 sec");
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
            }
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                if (backgroundMedia != null && backgroundMedia.size() > 0) {
                    layoutChooseFromGallery.setVisibility(View.GONE);
                    layoutCrossWall.setVisibility(View.VISIBLE);
                } else {
                    layoutChooseFromGallery.setVisibility(View.VISIBLE);
                    layoutCrossWall.setVisibility(View.GONE);
                }
                layoutCamera.setVisibility(View.GONE);

                if (IS_RECORDING_START) {
                    countDownTimer.cancel();
                    cameraPreview.stop();
                    cameraPreview.stopRecordingVideoWithRecording();
                    IS_RECORDING_START = false;
                }

                break;

            case 1:
                layoutCrossWall.setVisibility(View.GONE);
                layoutChooseFromGallery.setVisibility(View.GONE);
                layoutCamera.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.GONE);
                takeImage.setImageResource(R.drawable.capture_button);

                if (IS_RECORDING_START) {
                    countDownTimer.cancel();
                    cameraPreview.stopRecordingVideoWithRecording();
                    IS_RECORDING_START = false;
                }

                tabLayout.getTabAt(1).select();
                if (cameraPreview != null && !cameraPreview.isStarted()) {
                    cameraPreview.start();
                }
                break;

            case 2:
                txtTimer.setText(10 + " Secs");
                layoutChooseFromGallery.setVisibility(View.GONE);
                layoutCamera.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.VISIBLE);
                takeImage.setImageResource(R.drawable.capture_button);
                layoutCrossWall.setVisibility(View.GONE);

                if (cameraPreview != null && !cameraPreview.isStarted()) {
                    cameraPreview.start();
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CameraKit.Constants.PERMISSION_REQUEST_CAMERA) {
            if (this.cameraPreview != null) {
                this.cameraPreview.stop();
                this.cameraPreview.start();
            }
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

    @Override
    public void onResume() {
        super.onResume();
        if (cameraPreview != null)
            cameraPreview.start();

        allowPostUpdate();
    }

    @Override
    public void onPause() {
        if (cameraPreview != null) {
            //  cameraPreview.stop();
            if (IS_RECORDING_START) {
                if (countDownTimer != null)
                    countDownTimer.cancel();
                cameraPreview.stopRecordingVideoWithRecording();
                takeImage.setImageResource(R.drawable.capture_button);
                IS_RECORDING_START = false;
                txtTimer.setText("10 Secs");
            }
        }

        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraPreview != null) {
            cameraPreview.stop();
            if (IS_RECORDING_START) {
                if (countDownTimer != null)
                    countDownTimer.cancel();
                cameraPreview.stopRecordingVideoWithRecording();
            }
        }

        if (message != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(message);
    }


    @Override
    public void onApiFail(String apiSource) {
        if (loader.isShowing()) { loader.dismiss(); }
    }


    public void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(getActivity());
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

    @Override
    public void onStop() {
        super.onStop();
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
            file = new File(getActivity().getExternalFilesDir(null), "background_" + System.currentTimeMillis() + "_image.jpeg");

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