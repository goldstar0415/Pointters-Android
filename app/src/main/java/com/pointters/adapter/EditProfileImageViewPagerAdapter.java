package com.pointters.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.AsyncResponse;
import com.pointters.listener.OnRecycleItemClickListener;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.listener.OnRecyclerViewItemClickListener;
import com.pointters.model.Media;
import com.pointters.utils.AndroidUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


public class EditProfileImageViewPagerAdapter extends PagerAdapter {

    private boolean isEdit = false;
    private Context context;
    private List<Media> bgFiles;
    private LayoutInflater layoutInflater;
    private JZVideoPlayerStandard videoView;
    private ImageView imageView;
    private ImageButton deleteButton;
    OnRecyclerViewButtonClickListener listener;
    OnRecyclerViewItemClickListener listener1;

    public void setEdit(boolean b) {
        this.isEdit = b;
    }
    public EditProfileImageViewPagerAdapter(Context context, List<Media> bgFiles) {
        this.context = context;
        this.bgFiles = bgFiles;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListener1(OnRecyclerViewItemClickListener listener1){
        this.listener1 = listener1;
    }
    public void setListener(OnRecyclerViewButtonClickListener listener){
        this.listener = listener;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.adapter_edit_profile_image, container, false);
        videoView = (JZVideoPlayerStandard) view.findViewById(R.id.video_view);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
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
                videoView.setUp(media.getFileName(), JZVideoPlayer.SCREEN_WINDOW_LIST, "");
                AndroidUtils.MyAsyncTask asyncTask =new AndroidUtils.MyAsyncTask();
                asyncTask.delegate = new AsyncResponse() {
                    @Override
                    public void processFinish(Bitmap output) {
                        videoView.thumbImageView.setImageBitmap(output);

                    }
                };
                asyncTask.execute(media.getFileName());

         /*   videoView.hideControls();
            videoView.setAutoPlay(true);
            videoView.disableControls();
            videoView.setLoop(true);
            videoView.setSource(Uri.parse(media.getFileName()));*/
            }
        }
        if (!isEdit) {
            deleteButton.setVisibility(View.GONE);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(v, position);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener1 != null) {
                    listener1.onItemClick(position);
                }
            }
        });

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
