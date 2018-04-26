package com.pointters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context _context;
    private List<Media> mediaList;
    private DisplayImageOptions options;

    public MediaAdapter(Context _context, List<Media> mediaList) {
        this._context = _context;
        this.mediaList = mediaList;
    }

    public void setData(ArrayList<Media> mediaList) {
        this.mediaList = mediaList;
//        notifyDataSetChanged();
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
        }
//        holder.txtTitle.setText(titles[position]);
//        holder.txtAmount.setText(currencySymbol + "" + transAmounts.get(position));

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView mediaImage;
        private ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaImage = (RoundedImageView) itemView.findViewById(R.id.crosswall_images);
            deleteButton = (ImageView) itemView.findViewById(R.id.img_cross);
        }
    }
}