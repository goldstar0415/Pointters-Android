package com.pointters.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.afollestad.easyvideoplayer.EasyVideoCallback;
//import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.PlayVideoActivity;
import com.pointters.model.Media;

import java.util.ArrayList;


/**
 * Created by aniljha on 21/9/17.
 */

public class AddImagesInCrossallPagerAdapter extends PagerAdapter{//} implements EasyVideoCallback {
    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//    private EasyVideoPlayer player;
    private DisplayImageOptions options;
    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Media> files;


    public AddImagesInCrossallPagerAdapter(Context context, ArrayList<Media> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_add_image_in_crosswall, container, false);
        RoundedImageView imageView = (RoundedImageView) itemView.findViewById(R.id.crosswall_images);
//        player = (EasyVideoPlayer) itemView.findViewById(R.id.player);

        CardView cardView = (CardView) itemView.findViewById(R.id.cardview);

        if (files.get(position).getMediaType().equals(context.getResources().getString(R.string.image))) {
//            player.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(files.get(position).getFileName()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else if (files.get(position).getMediaType().equals(context.getResources().getString(R.string.video))) {
           // context.startActivity(new Intent(context, PlayVideoActivity.class));
            imageView.setVisibility(View.GONE);

//            player.setVisibility(View.VISIBLE);
//
//            player.setCallback(this);
//            player.setSource(Uri.parse(TEST_URL));

        }

        cardView.setMaxCardElevation(0);

        container.addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
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
