package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.pointters.R;
import com.pointters.utils.ConstantUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by mac on 1/8/18.
 */

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    protected boolean pausedInOnStop = false;
    private VideoView videoView;
    private ImageView btnBack;

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        intent.putExtra(ConstantUtils.CHAT_TAP_VIDEO, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        initUI();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void initUI() {
        String videoUrl = getIntent().getStringExtra(ConstantUtils.CHAT_TAP_VIDEO);

        btnBack = (ImageView) findViewById(R.id.toolbar_lft_img);
        btnBack.setOnClickListener(this);

        videoView = (VideoView) findViewById(R.id.videoview);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setVideoURI(Uri.parse(videoUrl));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()) {
            pausedInOnStop = true;
            videoView.pause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (pausedInOnStop) {
            videoView.start();
            pausedInOnStop = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                videoView.stopPlayback();
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
