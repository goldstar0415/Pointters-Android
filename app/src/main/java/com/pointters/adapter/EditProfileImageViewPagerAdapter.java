package com.pointters.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.Media;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class EditProfileImageViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Media> bgFiles;
    private LayoutInflater layoutInflater;
    private VideoView videoView;
    private ImageView imageView;

    public EditProfileImageViewPagerAdapter(Context context, List<Media> bgFiles) {
        this.context = context;
        this.bgFiles = bgFiles;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.adapter_edit_profile_image, container, false);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        final ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_grey)
                .showImageForEmptyUri(R.drawable.placeholder_grey)
                .showImageOnFail(R.drawable.placeholder_grey)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if(bgFiles.size()>0) {
            Media media = bgFiles.get(position);
            if (media.getMediaType().equals(context.getResources().getString(R.string.image))) {
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(media.getFileName(), imageView, options);


            } else if (media.getMediaType().equals(context.getResources().getString(R.string.video))) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                try {
                    videoView.setVideoPath(URLDecoder.decode(media.getFileName(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        progressBar.setVisibility(View.GONE);
                        videoView.start();
                    }
                });


                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        progressBar.setVisibility(View.GONE);
                        return true;
                    }
                });

         /*   videoView.hideControls();
            videoView.setAutoPlay(true);
            videoView.disableControls();
            videoView.setLoop(true);
            videoView.setSource(Uri.parse(media.getFileName()));*/
            }
        }

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return bgFiles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
