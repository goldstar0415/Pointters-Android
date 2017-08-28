package com.pointters.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pointters.R;
import com.pointters.model.FulfillmentDetails;
import com.pointters.utils.AppUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class FulfillmentActivity extends AppCompatActivity implements View.OnClickListener{

    private final Integer android_image[] = {R.drawable.thumb_small_one,R.drawable.thumb_small_two,R.drawable.thumb_small_one,R.drawable.thumb_small_two,
            R.drawable.thumb_small_one,R.drawable.thumb_small_two,R.drawable.thumb_small_one};

    private GoogleMap mMap;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulfillment);
        AppUtils.setToolBarWithBothIcon(FulfillmentActivity.this, getResources().getString(R.string.fulfillment), R.drawable.back_icon_grey, R.drawable.more_icon_horizontal);

        recyclerView = (RecyclerView) findViewById(R.id.mDeliveredFilesRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        ArrayList fulfillmentList = prepareData();
        ImageAdapter adapter = new ImageAdapter(FulfillmentActivity.this, fulfillmentList);
        recyclerView.setAdapter(adapter);

        MapFragment mapFragment =(MapFragment) getFragmentManager().findFragmentById(R.id.mapId);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                // Add a marker in Agicent and move the camera
                LatLng TutorialsPoint = new LatLng(28.6125, 77.3773);
                mMap.addMarker(new MarkerOptions().position(TutorialsPoint));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
            }
        });

    }
        private ArrayList prepareData(){

            ArrayList arrayList_fulfillment = new ArrayList<>();
            for(int i=0;i<android_image.length;i++){
                FulfillmentDetails fulfillmentDetails = new FulfillmentDetails();
                fulfillmentDetails.setFulfillment_img(android_image[i]);
                arrayList_fulfillment.add(fulfillmentDetails);
            }
            return arrayList_fulfillment;
        }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_lft_img:

                onBackPressed();
                break;
            case R.id.toolbar_right_img:

                break;

        }
    }

    //===============Adapter==================================================================

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private ArrayList<FulfillmentDetails> android_versions;
        private Context context;

        public ImageAdapter(Context context,ArrayList<FulfillmentDetails> android_versions) {
            this.context = context;
            this.android_versions = android_versions;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fulfillment_details_list,
                    viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.img_rv.setImageResource(android_versions.get(i).getFulfillment_img());
        }

        @Override
        public int getItemCount() {
            return android_versions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView img_rv;
            public ViewHolder(View view) {
                super(view);
                img_rv = (ImageView)view.findViewById(R.id.mUserDpOfFulfillment);

            }
        }
    }
    }

