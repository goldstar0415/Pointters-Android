package com.pointters.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.listener.AsyncResponse;
import com.pointters.listener.OnRecyclerViewButtonClickListener;
import com.pointters.model.Media;
import com.pointters.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private static final String TAG = "Video";
    private Context _context;
    private List<Media> mediaList;
    private DisplayImageOptions options;
    private boolean isFullscreen  = false;
    OnRecyclerViewButtonClickListener listener;

    public MediaAdapter(Context _context, List<Media> mediaList) {
        this._context = _context;
        this.mediaList = mediaList;
    }

    public void setListener(OnRecyclerViewButtonClickListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Media> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }
    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.user_avatar_placeholder)
                .showImageForEmptyUri(R.drawable.user_avatar_placeholder)
                .showImageOnFail(R.drawable.user_avatar_placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        View view = LayoutInflater.from(_context).inflate(R.layout.adapter_add_image_in_crosswall, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Media media = mediaList.get(position);
        if (media.getMediaType().equals("image")) {
            ImageLoader.getInstance().displayImage(media.getFileName(), holder.mediaImage, options);
            holder.mediaVideo.setVisibility(View.INVISIBLE);
            holder.mediaImage.setVisibility(View.VISIBLE);
        }else {
            holder.mediaVideo.setVisibility(View.VISIBLE);
            holder.mediaImage.setImageBitmap(ThumbnailUtils.createVideoThumbnail(media.getFileName(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
            holder.mediaVideo.setUp(media.getFileName(), JZVideoPlayer.SCREEN_WINDOW_LIST, "");
            AndroidUtils.MyAsyncTask asyncTask =new AndroidUtils.MyAsyncTask();
            asyncTask.delegate = new AsyncResponse() {
                @Override
                public void processFinish(Bitmap output) {
                    holder.mediaVideo.thumbImageView.setImageBitmap(output);

                }
            };
            asyncTask.execute(media.getFileName());
        }
        holder.mediaVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                {
                    listener.onButtonClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mediaImage;
        private JZVideoPlayerStandard mediaVideo;
        private ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaImage = (RoundedImageView) itemView.findViewById(R.id.crosswall_images);
            mediaVideo = (JZVideoPlayerStandard) itemView.findViewById(R.id.video_view);
            deleteButton = (ImageButton) itemView.findViewById(R.id.img_cross);
            isFullscreen = false;
        }
    }
}