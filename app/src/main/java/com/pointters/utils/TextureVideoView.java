package com.pointters.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by prashantkumar on 17/10/17.
 */

public class TextureVideoView extends TextureView implements TextureView.SurfaceTextureListener {

    private MediaPlayer mMediaPlayer;

    public TextureVideoView(Context context) {
        super(context);
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        this.setSurfaceTextureListener(this);
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void setVideoPath(final String path) throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
    }

    public void setVideoPath(FileDescriptor fd, long offset, long length) throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(fd, offset, length);
        mMediaPlayer.prepare();
    }

    public void setVideoURI(Context context, Uri uri) throws IOException {
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(context, uri);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        mMediaPlayer.setOnErrorListener(onErrorListener);
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void stopPlayback() {
        mMediaPlayer.stop();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public void release() {
        mMediaPlayer.release();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {

        mMediaPlayer.setSurface(new Surface(surface));
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}