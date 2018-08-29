package com.pointters.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pointters.R;
import com.pointters.model.FileAndBitmapModel;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import com.afollestad.easyvideoplayer.EasyVideoCallback;
//import com.afollestad.easyvideoplayer.EasyVideoPlayer;


public class PlayVideoActivity extends AppCompatActivity {//implements EasyVideoCallback {
    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//    private EasyVideoPlayer player;
    private FileAndBitmapModel fileAndBitmapModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
//        player = (EasyVideoPlayer) findViewById(R.id.player);
//        player.hideControls();
//        player.setAutoPlay(true);
//       // player.disableControls();
//        player.setCallback(this);
//        player.setSource(Uri.parse(getIntent().getStringExtra(ConstantUtils.INTENT_BUNDLE_IMAGE_VIDEO)));

    }
    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
//        player.pause();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }




//    @Override
//    public void onStarted(EasyVideoPlayer player) {
//
//    }
//
//    @Override
//    public void onPaused(EasyVideoPlayer player) {
//
//    }
//
//    @Override
//    public void onPreparing(EasyVideoPlayer player) {
//
//    }
//
//    @Override
//    public void onPrepared(EasyVideoPlayer player) {
//
//    }
//
//    @Override
//    public void onBuffering(int percent) {
//
//    }
//
//    @Override
//    public void onError(EasyVideoPlayer player, Exception e) {
//
//    }
//
//    @Override
//    public void onCompletion(EasyVideoPlayer player) {
//
//    }
//
//    @Override
//    public void onRetry(EasyVideoPlayer player, Uri source) {
//
//    }
//
//    @Override
//    public void onSubmit(EasyVideoPlayer player, Uri source) {
//
//    }
}
