package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pointters.R;
import com.pointters.activity.PlayVideoActivity;
import com.pointters.model.FileAndBitmapModel;
import com.pointters.utils.ConstantUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.pointters.R.id.player;
import static com.pointters.R.id.video_view;
import static com.pointters.R.string.video;

/**
 * Created by Vishal Sharma on 29-Jul-17.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {

    private RoundedImageView roundedImageView,uploadImageView;
    private RelativeLayout relativeLayoutFirstFrame,layoutTagServiceSeller,tempVideoViewLayout;
    private ImageView imagePlayBtn,imgDelete;
    private VideoView videoView;
    private  Bitmap thumbImg;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crosswall_video, container, false);
        layoutTagServiceSeller = (RelativeLayout) view.findViewById(R.id.layout_tag_service);
        roundedImageView = (RoundedImageView) view.findViewById(R.id.img_first_frame);
        relativeLayoutFirstFrame = (RelativeLayout) view.findViewById(R.id.first_frame_layout);
        imagePlayBtn = (ImageView) view.findViewById(R.id.play_icon);
        imgDelete = (ImageView) view.findViewById(R.id.delete_btn);
        imgDelete.setOnClickListener(this);
        uploadImageView=(RoundedImageView)view.findViewById(R.id.img_upload);
        uploadImageView.setOnClickListener(this);
        layoutTagServiceSeller.setOnClickListener(this);
        videoView = (VideoView) view.findViewById(R.id.crosswall_video);
        tempVideoViewLayout=(RelativeLayout)view.findViewById(R.id.temp_view);

       // videoView.setZOrderMediaOverlay(true);
        videoView.setZOrderOnTop(true);

        view.findViewById(R.id.btn_full_screen).setOnClickListener(this);
        videoView.setVisibility(View.GONE);
        relativeLayoutFirstFrame.setVisibility(View.VISIBLE);

        if(getArguments().getString("showImage").equals("yes"))
        {
            uploadImageView.setVisibility(View.VISIBLE);
        }else
        if(getArguments().getString("showImage").equals("no"))
        {
            uploadImageView.setVisibility(View.GONE);
        }

        if (getArguments().getString(ConstantUtils.SOURCE).equals("POSTUPDATE")) {
            layoutTagServiceSeller.setVisibility(View.VISIBLE);
            if (getArguments().getString("lastPosition").equals("yes")) {
                layoutTagServiceSeller.setVisibility(View.INVISIBLE);

            } else if (getArguments().getString("lastPosition").equals("no")) {
                layoutTagServiceSeller.setVisibility(View.VISIBLE);
            }
        }
        else if (getArguments().getString(ConstantUtils.SOURCE).equals("ADDSERVICE")) {
            layoutTagServiceSeller.setVisibility(View.INVISIBLE);
        }


        if (getArguments().getString("lastPosition").equals("yes")) {
            imgDelete.setVisibility(View.GONE);
            roundedImageView.setOnClickListener(this);

        } else if (getArguments().getString("lastPosition").equals("no")) {
            imgDelete.setVisibility(View.VISIBLE);
            roundedImageView.setOnClickListener(null);
        }



        if (getArguments().getString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO) != null) {
            roundedImageView.setBackground(ContextCompat.getDrawable(getActivity(),R.color.color_black_info));
        }

        imagePlayBtn.setOnClickListener(this);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                relativeLayoutFirstFrame.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);

            }
        });

        return view;
    }
    private void saveCurrentFrame(String path) {
        thumbImg = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

        thumbImg = Bitmap.createScaledBitmap(thumbImg, (int) (thumbImg.getWidth() * 0.8), (int) (thumbImg.getHeight() * 0.8), true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        thumbImg.compress(Bitmap.CompressFormat.JPEG, 70, out);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_btn:
                Intent intent = new Intent("delete");
                intent.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                break;
            case R.id.btn_full_screen:
                Intent  intent1=new Intent(getActivity(),PlayVideoActivity.class);
                intent1.putExtra(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO,getArguments().getString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO).toString());
                startActivity(intent1);
                break;
            case R.id.play_icon:
                relativeLayoutFirstFrame.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);

                Uri video = Uri.parse(getArguments().getString(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO).toString());
              /*  videoView.hideControls();
                videoView.setSource(video);*/
                videoView.setVideoURI(video);
                videoView.start();
                break;

            case R.id.img_upload:
                Intent intent2 = new Intent("pick");
                intent2.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent2);
                break;
            case R.id.layout_tag_service:
                Intent intent3 = new Intent("openActivity");
                intent3.putExtra(ConstantUtils.POSITION, getArguments().getInt(ConstantUtils.POSITION));
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent3);
                break;
        }

    }


}
