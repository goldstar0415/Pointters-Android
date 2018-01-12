package com.pointters.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.utils.ConstantUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by mac on 1/7/18.
 */

public class PhotoViewerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        String photoUrl = getIntent().getStringExtra(ConstantUtils.CHAT_TAP_PHOTO);

        btnBack = (ImageView) findViewById(R.id.toolbar_lft_img);
        btnBack.setOnClickListener(this);

        imageView = (PhotoView) findViewById(R.id.photo_view);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        if (!photoUrl.equals("")) {
            ImageLoader.getInstance().displayImage(photoUrl, imageView, options);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
