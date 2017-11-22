package com.pointters.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.pointters.R;
import com.pointters.adapter.SearchHintAdapter;
import com.pointters.listener.OnEditTextChangeListener;
import com.pointters.listener.OnSearchItemClickListener;
import com.pointters.model.SearchHint;
import com.pointters.utils.AndroidUtils;
import com.pointters.utils.ConstantUtils;
import com.pointters.utils.MyTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vishalsharma on 1/8/17.
 */

public class SearchServiceActivity extends AppCompatActivity implements View.OnTouchListener, OnEditTextChangeListener, View.OnClickListener, OnSearchItemClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private final int REQUEST_CHECK_SETTINGS = 1000;
    private final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 2000;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location = null;
    private ImageView imgSearch;
    private ImageView imgLocation;
    private EditText edtSearchHere;
    private EditText edtLocation;
    private ImageView imgCrossSearch;
    private ImageView imgCrossLocation;
    private RecyclerView recyclerViewSearchHints;
    private String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private String TYPE_AUTOCOMPLETE = "/autocomplete";
    private String OUT_JSON = "/json";
    private ArrayList<String> listSuggestedPlaces = new ArrayList<>();
    private ArrayList<String> filterListSuggestedPlaces = new ArrayList<>();
    private ArrayList<String> popularCategories = new ArrayList<>();
    private ArrayList<String> recentSearches = new ArrayList<>();
    private ArrayList<String> keywords = new ArrayList<>();
    private SearchHintAdapter searchHintAdapter;
    private ArrayList<SearchHint> locationSearchHint = new ArrayList<>();
    private String currentLocation="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_service);

        initViews();

        setSearchBarActive();

        setTouchListener();

        setEditTextListener();

        setOnClickListener();
        locationSearchHint.add(new SearchHint("Locations", filterListSuggestedPlaces));


        recyclerViewSearchHints.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    AndroidUtils.hideKeyBoard(SearchServiceActivity.this);

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        popularCategories.add("Photographer");
        popularCategories.add("Entertainer");
        popularCategories.add("Wedding Planner");

        recentSearches.add("Dog Walker");
        recentSearches.add("Entertainer");
        recentSearches.add("Wedding Planner");

        keywords.add("Dog Walk");
        keywords.add("Dog Care");
        keywords.add("Dog Cares");

        googleApiClient = new GoogleApiClient.Builder(SearchServiceActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
      //  turnOnLocation();

      /*  if(currentLocation.isEmpty()) {

            turnOnLocation();
        }*/

    }


    private void setOnClickListener() {

        imgCrossSearch.setOnClickListener(this);
        imgCrossLocation.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
      //  findViewById(R.id.txt_apply).setOnClickListener(this);

    }

    private void setEditTextListener() {

        //Custom Edit text change listener with returning id of edit text
        edtSearchHere.addTextChangedListener(new MyTextWatcher(edtSearchHere, this));
        edtLocation.addTextChangedListener(new MyTextWatcher(edtLocation, this));
    }

    private void setTouchListener() {

        edtSearchHere.setOnTouchListener(this);
        edtLocation.setOnTouchListener(this);

    }

    private void setSearchBarActive() {

        imgSearch.setSelected(true);
        imgLocation.setSelected(false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchHints.setLayoutManager(linearLayoutManager);

        ArrayList<SearchHint> searchHints = new ArrayList<>();
        searchHints.add(new SearchHint("Popular Categories", popularCategories));
        searchHints.add(new SearchHint("Recent Searches", recentSearches));
        searchHints.add(new SearchHint("Keywords", keywords));


/*        searchHints.add(new SearchHint("Popular Categories", new ArrayList<String>"Photographer", "Entertainer", "Wedding Planner"}));
        searchHints.add(new SearchHint("Recent Searches", new String[]{"Dog Walker", "Entertainer", "Wedding Planner"}));
        searchHints.add(new SearchHint("Keywords", new String[]{"Dog Walk", "Dog Care", "Dog Cares"}));*/

        searchHintAdapter = new SearchHintAdapter(searchHints);

        searchHintAdapter.setOnSearchItemClickListener(this);
        recyclerViewSearchHints.setAdapter(searchHintAdapter);

        edtSearchHere.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"));
        edtLocation.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf"));
    }

    private void setLocationBarActive() {

        imgLocation.setSelected(true);
        imgSearch.setSelected(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchHints.setLayoutManager(linearLayoutManager);
        // ArrayList<SearchHint> searchHints = new ArrayList<>();
        //  searchHints.add(new SearchHint("Locations", listSuggestedPlaces));
        //searchHints.add(new SearchHint("Locations", new String[]{"Chicago", "Chicago Suburb", "Chicago Downtown"}));

        searchHintAdapter = new SearchHintAdapter(locationSearchHint);

        searchHintAdapter.setOnSearchItemClickListener(this);
        recyclerViewSearchHints.setAdapter(searchHintAdapter);

        edtLocation.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"));
        edtSearchHere.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf"));

    }

    private void initViews() {

        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgLocation = (ImageView) findViewById(R.id.img_location_search);
        edtSearchHere = (EditText) findViewById(R.id.edt_search_here);
        edtLocation = (EditText) findViewById(R.id.edt_enter_location);
        imgCrossSearch = (ImageView) findViewById(R.id.img_cross_search_here);
        imgCrossLocation = (ImageView) findViewById(R.id.img_cross_location);
        recyclerViewSearchHints = (RecyclerView) findViewById(R.id.recycler_view_search_hints);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
            case R.id.edt_search_here:
                if (!imgSearch.isSelected())
                    setSearchBarActive();
                break;

            case R.id.edt_enter_location:
                if(!currentLocation.isEmpty() && !imgLocation.isSelected()) {
                 //   if (!imgLocation.isSelected())
                        setLocationBarActive();
                }else {
                    turnOnLocation();
                }

                break;
        }


        return false;
    }

    @Override
    public void onTextChange(String text, View view) {

        switch (view.getId()) {

            case R.id.edt_search_here:
                if (text.length() > 0)
                    imgCrossSearch.setVisibility(View.VISIBLE);
                else
                    imgCrossSearch.setVisibility(View.GONE);
                break;

            case R.id.edt_enter_location:
                new prepareListData().execute(text.toString());

                if (text.length() > 0)

                    imgCrossLocation.setVisibility(View.VISIBLE);
                else
                    imgCrossLocation.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_cross_search_here:
                edtSearchHere.setText("");
                break;

            case R.id.img_cross_location:
                filterListSuggestedPlaces.clear();
                if (currentLocation.isEmpty())
                    turnOnLocation();
                else
                    filterListSuggestedPlaces.add(currentLocation);
                searchHintAdapter.notifyDataSetChanged();
                edtLocation.setText("");
                break;

            case R.id.btn_back:
                onBackPressed();
                break;

      /*      case R.id.txt_apply:
                Intent intent = new Intent(new Intent(this, SearchResultsActivity.class));
                intent.putExtra("searchedValue", String.valueOf(edtSearchHere.getText() + ", " + edtLocation.getText().toString()));
                startActivity(intent);
                break;*/
        }

    }

    @Override
    public void searchItemClicked(String hint) {

        AndroidUtils.hideKeyBoard(this);

        if (imgSearch.isSelected()) {
            edtSearchHere.setText(hint);
            if(!edtLocation.getText().toString().isEmpty())
            {
                Intent intent = new Intent(new Intent(this, SearchResultsActivity.class));
                intent.putExtra("searchedValue", String.valueOf(edtSearchHere.getText() + ", " + edtLocation.getText().toString()));
                startActivity(intent);
            }
        } else {
            edtLocation.setText(hint);
         //   edtLocation.removeTextChangedListener(new MyTextWatcher(edtLocation,this));
          //  edtLocation.addTextChangedListener(null);
            if(!edtSearchHere.getText().toString().isEmpty())
            {
                Intent intent = new Intent(new Intent(this, SearchResultsActivity.class));
                intent.putExtra("searchedValue", String.valueOf(edtSearchHere.getText() + ", " + edtLocation.getText().toString()));
                startActivity(intent);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void turnOnLocation() {



        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        requestCurrentLocation();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // LocationRequestModel settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SearchServiceActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // LocationRequestModel settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private void requestCurrentLocation() {

        if (ContextCompat.checkSelfPermission(SearchServiceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SearchServiceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {


        if (ContextCompat.checkSelfPermission(SearchServiceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        if (location == null) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        } else {
            filterListSuggestedPlaces.clear();
            currentLocation = AndroidUtils.getLocationName(SearchServiceActivity.this, location);
            if (!currentLocation.isEmpty()) {
                filterListSuggestedPlaces.add(0, currentLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {

                if (grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentLocation();
                    }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(SearchServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Show permission explanation dialog...
                        }else{
                            Toast.makeText(SearchServiceActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }



            }
            break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        filterListSuggestedPlaces.clear();

        currentLocation = AndroidUtils.getLocationName(SearchServiceActivity.this, location);
        if (!currentLocation.isEmpty()) {
            filterListSuggestedPlaces.add(0, currentLocation);
        }

        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case RESULT_OK:
                        requestCurrentLocation();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private class prepareListData extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {


            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + ConstantUtils.API_KEY_FOR_GOOGLE_PLACES);

                sb.append("&input=" + URLEncoder.encode(params[0].trim(), "utf8"));
                //sb.append(RESULT_TYPES);

                URL url = new URL(sb.toString());

                System.out.println("URL: " + url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");


                filterListSuggestedPlaces.clear();
                if (currentLocation.isEmpty())
                    turnOnLocation();
                else
                    filterListSuggestedPlaces.add(currentLocation);
                // Extract the Place descriptions from the results
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    filterListSuggestedPlaces.add(predsJsonArray.getJSONObject(i).getString("description"));

                }


                if(filterListSuggestedPlaces.size()>1 && filterListSuggestedPlaces.contains(currentLocation))
                {
                    filterListSuggestedPlaces.remove(0);
                }
            } catch (JSONException e) {
            }

            return filterListSuggestedPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<String> filterListSuggestedPlaces) {
            super.onPostExecute(filterListSuggestedPlaces);
            recyclerViewSearchHints.setAdapter(null);
            recyclerViewSearchHints.setAdapter(searchHintAdapter);
            listSuggestedPlaces.clear();
            listSuggestedPlaces.addAll(filterListSuggestedPlaces);
            filterListSuggestedPlaces.clear();

            if (listSuggestedPlaces.size() > 2) {
                filterListSuggestedPlaces.add(listSuggestedPlaces.get(0).toString().trim());
                filterListSuggestedPlaces.add(listSuggestedPlaces.get(1).toString().trim());
                filterListSuggestedPlaces.add(listSuggestedPlaces.get(2).toString().trim());
                searchHintAdapter.notifyDataSetChanged();
            } else if (listSuggestedPlaces.size() == 2) {
                filterListSuggestedPlaces.add(listSuggestedPlaces.get(0).toString().trim());
                filterListSuggestedPlaces.add(listSuggestedPlaces.get(1).toString().trim());
            } else if (listSuggestedPlaces.size() == 1) {
                filterListSuggestedPlaces.clear();
                filterListSuggestedPlaces.add(currentLocation);
                searchHintAdapter.notifyDataSetChanged();
            }


        }
    }
}
