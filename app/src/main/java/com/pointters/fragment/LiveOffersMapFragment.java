package com.pointters.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pointters.R;
import com.pointters.activity.LocationSearchActivity;
import com.pointters.listener.MapWrapperLayout;
import com.pointters.listener.OnInfoWindowElemTouchListener;
import com.pointters.model.LiveOfferModel;
import com.pointters.utils.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by jkc on 3/8/18.
 */

@SuppressLint("ValidFragment")
public class LiveOffersMapFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    public ArrayList<LiveOfferModel> serviceArrayList = new ArrayList<>();

    MapView mapView;
    GoogleMap googleMap;

    ArrayList<LatLng> markerPoints;

    private LinearLayout spinnerCategories;
    private LinearLayout llEditButton, llNewRequestButton, llSeeRequestButton;
    private RelativeLayout editView;
    private CardView locationButton;

    private ViewGroup infoWindow;
    private ImageView imgUser;
    private TextView txtServiceDesc, txtPrice, txtUserName;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private MapWrapperLayout mapWrapperLayout;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_live_offers_map, container, false);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mapWrapperLayout = (MapWrapperLayout)view.findViewById(R.id.map_relative_layout);

        mapView = (MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getContext());
        }catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;

                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        googleMap.setMyLocationEnabled(true);
                    }
                }
                markerPoints = new ArrayList<LatLng>();
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                addMakers(serviceArrayList);
                mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));

                // We want to reuse the info window for all the markers,
                // so let's create only one class member instance
                infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.map_info_window, null);
                txtUserName = (TextView)infoWindow.findViewById(R.id.txt_username);
                txtPrice = (TextView)infoWindow.findViewById(R.id.txt_price);
                txtServiceDesc = (TextView)infoWindow.findViewById(R.id.txt_service_desc);
                imgUser = (RoundedImageView)infoWindow.findViewById(R.id.img_user);

                // Setting custom OnTouchListener which deals with the pressed state
                // so it shows up
                infoButtonListener = new OnInfoWindowElemTouchListener(infoWindow,
                        null, //btn_default_normal_holo_light
                        null) //btn_default_pressed_holo_light
                {
                    @Override
                    protected void onClickConfirmed(View v, Marker marker) {
                        // Here we can perform some action triggered after clicking the button
                        Toast.makeText(getActivity(), marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
                    }
                };
                infoWindow.setOnTouchListener(infoButtonListener);


                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public View getInfoContents(Marker marker) {
                        // Setting up the infoWindow with current's marker info
                        int position = Integer.valueOf(marker.getSnippet());
                        LiveOfferModel model = serviceArrayList.get(position);
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.photo_placeholder)
                                .showImageForEmptyUri(R.drawable.photo_placeholder)
                                .showImageOnFail(R.drawable.photo_placeholder)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .build();
                        if (model == null) {
                            if (model.getMedia() != null) {
                                if (model.getMedia().getFileName() != null && !model.getMedia().getFileName().isEmpty()) {
                                    String strPic = model.getMedia().getFileName();
                                    if (!strPic.contains("https://s3.amazonaws.com")) {
//                                        strPic = "https://s3.amazonaws.com" + strPic;
                                    }
                                    ImageLoader.getInstance().displayImage(strPic, imgUser, options);
                                }

                            }
                            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                                txtServiceDesc.setText(model.getDescription());
                            } else {
                                txtServiceDesc.setText("NA");
                            }

                            if (model.getSeller() != null && !model.getSeller().getFirstName().isEmpty() && !model.getSeller().getLastName().isEmpty()) {

                                txtUserName.setText(String.valueOf(position + 1) + ". " + model.getSeller().getFirstName() + " " + model.getSeller().getLastName());
                            } else {
                                txtUserName.setText(String.valueOf(position + 1) + ".");
                            }

                            if (model.getPrices() != null) {
                                String currency = "";
                                currency = model.getPrices().getCurrencySymbol() != null ? model.getPrices().getCurrencySymbol() : "";
                                txtPrice.setText(currency + "" + model.getPrices().getPrice());
                            } else {
                                txtPrice.setText("NA");
                            }
                        }
                        infoButtonListener.setMarker(marker);

                        // We must call this to set the current marker and infoWindow references
                        // to the MapWrapperLayout
                        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                        return infoWindow;
                    }
                });

            }
        });
        initViews();

        return view;
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, int num) {

        View customMarkerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_map_pin, null);
//        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.);
//        markerImageView.setImageResource(resId);
        TextView markerText = (TextView) customMarkerView.findViewById(R.id.txt_num);
        markerText.setText(String.valueOf(num));
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void addMakers(ArrayList<LiveOfferModel> models) {

        LatLng sydney = new LatLng(Float.parseFloat("37.422"), Float.parseFloat("-122.084"));
        for (int position = 0; position < models.size(); position++) {
            LiveOfferModel model = models.get(position);
            sydney = new LatLng(Float.parseFloat("37.422"), Float.parseFloat("-122.084"));
            ArrayList<Double> coordinates = model.getLocation().get(0).getGeoJson().getCoordinates();
            if (coordinates.size() > 1) {
                sydney = new LatLng(coordinates.get(1), coordinates.get(0));
            }
            googleMap.addMarker(new MarkerOptions().position(sydney).
                    title("Title").snippet(String.valueOf(position)).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.plus_icon, position + 1))));

        }
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                (cameraPosition ));


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                }
                return;
            }

        }
    }
    private void initViews() {
        llEditButton = (LinearLayout) view.findViewById(R.id.ll_edit);
        llNewRequestButton = (LinearLayout) view.findViewById(R.id.ll_new_request);
        llSeeRequestButton = (LinearLayout) view.findViewById(R.id.ll_see_request);

        llEditButton.setOnClickListener(this);
        llNewRequestButton.setOnClickListener(this);
        llSeeRequestButton.setOnClickListener(this);

        editView = (RelativeLayout) view.findViewById(R.id.rl_overlay);
        editView.setVisibility(View.GONE);
        spinnerCategories = (LinearLayout)view.findViewById(R.id.spinner_categories);
        spinnerCategories.setOnClickListener(this);
        locationButton = (CardView) view.findViewById(R.id.card_location);
        locationButton.setOnClickListener(this);
        editView.setOnClickListener(this);

    }

    public void setServiceArrayList(ArrayList<LiveOfferModel> models) {
        this.serviceArrayList = models;
        addMakers(models);
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.spinner_categories:
                editView.setVisibility(View.VISIBLE);
                break;

            case R.id.ll_new_request:
                editView.setVisibility(View.GONE);
                break;
            case R.id.rl_overlay:
                editView.setVisibility(View.GONE);
                break;
            case R.id.ll_edit:
                editView.setVisibility(View.GONE);
                break;
            case R.id.ll_see_request:
                editView.setVisibility(View.GONE);
                break;
            case R.id.card_location:
                startActivity(new Intent(getActivity(), LocationSearchActivity.class));
                break;
        }

    }
}
