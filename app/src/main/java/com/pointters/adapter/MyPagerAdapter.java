package com.pointters.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.Media;

import java.util.List;

/**
 * Created by prashantkumar on 19/9/17.
 */

public class MyPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Media> mediaList;

    public MyPagerAdapter(Context context, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        VideoView videoView = (VideoView) view.findViewById(R.id.video_view);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_img_video_placeholder)
                .showImageForEmptyUri(R.drawable.upload_img_video_placeholder)
                .showImageOnFail(R.drawable.upload_img_video_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        if (mediaList.size() > 0) {
            if (mediaList.get(position).getMediaType().equals(context.getResources().getString(R.string.image))) {

                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mediaList.get(position).getFileName(), imageView,options);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(view);

            } else if (mediaList.get(position).getMediaType().equals(context.getResources().getString(R.string.video))) {

                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                videoView.setVideoURI(Uri.parse(mediaList.get(position).getFileName()));
                videoView.setZOrderOnTop(true);
                videoView.start();
            }
        }


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


}
