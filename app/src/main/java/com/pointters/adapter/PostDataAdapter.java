package com.pointters.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.model.GeoJsonModel;
import com.pointters.model.Media;
import com.pointters.model.PostData;
import com.pointters.model.TagData;
import com.pointters.model.TagServiceSellerModel;
import com.pointters.utils.AutoSpanGridLayoutManager;
import com.pointters.utils.AutoSpannable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkc on 3/8/18.
 */

public class PostDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    private RecyclerView.LayoutParams layoutParams;
    private List<Media> postDatas = new ArrayList<>();
    private TagServiceSellerModel tagData;

    private int mItemHeight = 0;
    boolean isTagdata = false;
    RecyclerView parentView;

    public  PostDataAdapter(Context ctx){
        this.context = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        parentView = (RecyclerView) parent;
        switch (viewType){
            case 0:{
                final View view = LayoutInflater.from(context).inflate(R.layout.adapter_post_update_tag1, parent, false);
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        final ViewGroup.LayoutParams lp = view.getLayoutParams();
                        if (lp instanceof  StaggeredGridLayoutManager.LayoutParams){
                            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams)lp;
                            sglp.setFullSpan(false);
                            DisplayMetrics metrics = new DisplayMetrics();
                            metrics = context.getResources().getDisplayMetrics();
                            int width = parent.getMeasuredWidth();//.widthPixels;
                            sglp.width = (int) (width / 2f);
                            view.setLayoutParams(sglp);
                            final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager)((RecyclerView)parent).getLayoutManager();
                            lm.invalidateSpanAssignments();
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
                MediaViewHolder holder = new MediaViewHolder(view);
//                if (holder.rootView.getLayoutParams().height != mItemHeight){
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    metrics = context.getResources().getDisplayMetrics();
//                    int width = metrics.widthPixels;
//
//                    layoutParams = new RecyclerView.LayoutParams((int) (width / 2.2f), mItemHeight);
//                    holder.rootView.setLayoutParams(layoutParams);
//                }

                return holder;
            }
            case 1:{
                final View view = LayoutInflater.from(context).inflate(R.layout.adapter_service_tag, parent, false);
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        final ViewGroup.LayoutParams lp = view.getLayoutParams();
                        if (lp instanceof  StaggeredGridLayoutManager.LayoutParams){
                            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams)lp;
                            sglp.setFullSpan(true);
                            view.setLayoutParams(sglp);
                            final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager)((RecyclerView)parent).getLayoutManager();
                            lm.invalidateSpanAssignments();
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
//                layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
                final TagViewHolder holder = new TagViewHolder(view);
                return holder;
            }
            default:
                break;
        }

        return null;
    }
    public void setItemHeight(int height){
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ((MediaViewHolder) holder).bindTo(position);
        }else{
            ((TagViewHolder) holder).bindTo(position);
        }
    }

    @Override
    public int getItemViewType(int position){
        if (isTagdata) {
            if (position == (getItemCount() - 1)) {
                return 1;
            }else{
                return 0;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return postDatas.size() + (isTagdata ? 1 : 0);
//        return (isTagdata ? 1 : 0);
    }

    public List<Media> getPostDatas() {
        return postDatas;
    }

    public void setPostDatas(List<Media> postDatas) {
        this.postDatas = postDatas;
        notifyDataSetChanged();
    }

    public TagServiceSellerModel getTagData() {
        return tagData;
    }

    public void setTagData(TagServiceSellerModel tagData) {
        this.isTagdata = true;
        this.tagData = tagData;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootView;
        ImageView postImage;
        ImageButton deleteButton;
        public MediaViewHolder(View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
            postImage = (ImageView) itemView.findViewById(R.id.post_image);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);

        }

        public void bindTo(int position){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.photo_placeholder)
                    .showImageForEmptyUri(R.drawable.photo_placeholder)
                    .showImageOnFail(R.drawable.photo_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
            Media media = postDatas.get(position);
//            if (media.getMediaType().equals("image")) {
                ImageLoader.getInstance().displayImage(media.getFileName(), postImage, options);
//            }

        }
    }
    public class TagViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imgTagService;
        private TextView txtTagName;

        public TagViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imgTagService = (ImageView) itemView.findViewById(R.id.img_tag_service);
            txtTagName = (TextView) itemView.findViewById(R.id.txt_tag_name);
        }
        public void bindTo(int position){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.photo_placeholder)
                    .showImageForEmptyUri(R.drawable.photo_placeholder)
                    .showImageOnFail(R.drawable.photo_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            if (tagData != null ) {
                if (tagData.getService() != null) {
                    if (tagData.getType() != null && tagData.getType().equals("user")) {
                        imgTagService.setVisibility(View.GONE);
//                    holder.imgTagUser.setVisibility(View.VISIBLE);

                        if (tagData.getService().getMedia().size() > 0 && !tagData.getService().getMedia().isEmpty()) {

                            String strPic = tagData.getService().getMedia().get(0).getFileName();
                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                            }
//                        ImageLoader.getInstance().displayImage(strPic, holder.imgTagUser, options);
                        }

                        txtTagName.setText(tagData.getService().getSeller().getFirstName() + " " + tagData.getService().getSeller().getLastName());
                    }
                    else {
//                    holder.imgTagUser.setVisibility(View.GONE);
                        imgTagService.setVisibility(View.VISIBLE);

                        if (tagData.getService().getMedia().size() > 0) {
                            String strPic = tagData.getService().getMedia().get(0).getFileName();
                            if (!strPic.contains("https://s3.amazonaws.com")) {
//                            strPic = "https://s3.amazonaws.com" + strPic;
                            }
                            ImageLoader.getInstance().displayImage(strPic, imgTagService, options);
                        }

                        if (tagData.getService().getDescription() != null) {
                            txtTagName.setText(tagData.getService().getDescription());
                        } else {
                            txtTagName.setText("");
                        }
                    }

                    if (tagData.getService().getLocation() != null) {
                        String strCity = "", strState = "", strPos="", strKm="NA";
                        if (tagData.getService().getLocation().getCity() != null && !tagData.getService().getLocation().getCity().equals(""))
                            strCity = tagData.getService().getLocation().getCity();
                        if (tagData.getService().getLocation().getState() != null && !tagData.getService().getLocation().getState().equals(""))
                            strState = tagData.getService().getLocation().getState();
                        if (tagData.getService().getLocation().getGeoJson() != null) {
                            GeoJsonModel geoJson = tagData.getService().getLocation().getGeoJson();
                            if (geoJson.getCoordinates() != null && geoJson.getCoordinates().size() > 0) {
                                Location servicePos = new Location("");
                                servicePos.setLatitude(geoJson.getCoordinates().get(1));
                                servicePos.setLongitude(geoJson.getCoordinates().get(0));

                                Location userPos = new Location("");
//                                userPos.setLatitude(userLat);
//                                userPos.setLongitude(userLng);

                                strKm = String.format("%.02f", userPos.distanceTo(servicePos)/1000) + "km";
                            }
                        }

                        if (strCity.equals("")) {
                            if (!strState.equals("")) {
                                strPos = strKm + "@" + strState;
                            } else {
                                strPos = strKm;
                            }
                        } else {
                            strPos = strKm + "@" + strCity + ", " + strState;
                        }

//                    holder.txtTagPos.setText(strPos);
                    }
                }
            }
        }
    }
}
