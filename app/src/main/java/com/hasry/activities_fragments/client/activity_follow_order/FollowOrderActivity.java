package com.hasry.activities_fragments.client.activity_follow_order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.esotericsoftware.kryo.NotNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;
import com.hasry.R;
import com.hasry.activities_fragments.activity_map.MapActivity;
import com.hasry.databinding.ActivityFollowOrderBinding;
import com.hasry.databinding.ActivityMapBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.DriverLocationUpdate;
import com.hasry.models.OrderModel;
import com.hasry.models.PlaceDirectionModel;
import com.hasry.models.PlaceGeocodeData;
import com.hasry.models.PlaceMapDetailsData;
import com.hasry.models.SelectedLocationModel;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowOrderActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, Listeners.BackListener {
    private ActivityFollowOrderBinding binding;
    private String lang;
    private double lat = 0.0, lng = 0.0;
    private String address = "";
    private GoogleMap mMap;
    private Marker marker,marker_driver;
    private float zoom = 15.0f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String fineLocPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 1225;
    private OrderModel orderModel;
    private Preferences preferences;
    private UserModel userModel;
    private ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_order);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null){
            orderModel = (OrderModel) intent.getSerializableExtra("data");
        }
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        updateUI();
        CheckPermission();
        dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        EventBus.getDefault().register(this);
    }

    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(this,fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{fineLocPerm}, loc_req);
        } else {

            initGoogleApi();
        }
    }
    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private void updateUI() {

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {

            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);



        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToDriverUpdate(DriverLocationUpdate model){
        getDirection(String.valueOf(lat),String.valueOf(lng),String.valueOf(model.getLatitude()),String.valueOf(model.getLongitude()));

    }

    private void AddMarker(double lat, double lng,double driver_lat,double driver_lng,String driver_name, String client_name) {

        mMap.clear();

        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setBackground(null);
        View view = LayoutInflater.from(this).inflate(R.layout.map_client_icon, null);
        iconGenerator.setContentView(view);
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(client_name).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(lat, lng));
        builder.include(new LatLng(Double.parseDouble(orderModel.getDriver().getLatitude()),Double.parseDouble(orderModel.getDriver().getLongitude())));



        IconGenerator iconGenerator2 = new IconGenerator(this);
        iconGenerator2.setBackground(null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.map_driver_icon, null);
        iconGenerator2.setContentView(view2);
        marker_driver = mMap.addMarker(new MarkerOptions().position(new LatLng(driver_lat, driver_lng)).title(driver_name).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator2.makeIcon())).anchor(iconGenerator2.getAnchorU(), iconGenerator.getAnchorV()));



        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));




    }

    private void getDirection(String client_lat,String client_lng,String driver_lat,String driver_lng) {

        dialog.show();
        String origin = "", dest = "";
        origin = client_lat + "," + client_lng;
        dest = driver_lat + "," + driver_lng;

        Api.getService(Tags.googleDirectionBase_url)
                .getDirection(origin, dest, "rail", getString(R.string.map_api_key))
                .enqueue(new Callback<PlaceDirectionModel>() {
                    @Override
                    public void onResponse(Call<PlaceDirectionModel> call, Response<PlaceDirectionModel> response) {
                        if (response.body() != null && response.body().getRoutes().size() > 0) {

                            drawRoute(PolyUtil.decode(response.body().getRoutes().get(0).getOverview_polyline().getPoints()));

                        } else {
                            dialog.dismiss();
                            Toast.makeText(FollowOrderActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDirectionModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(FollowOrderActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void drawRoute(List<LatLng> latLngList) {
        dialog.dismiss();
        AddMarker(latLngList.get(0).latitude,latLngList.get(0).longitude,latLngList.get(latLngList.size()-1).latitude,latLngList.get(latLngList.size()-1).longitude,orderModel.getDriver().getName(),userModel.getData().getName());
        PolylineOptions options = new PolylineOptions();
        options.geodesic(true);
        options.color(ContextCompat.getColor(this, R.color.colorPrimary));
        options.width(8.0f);
        options.addAll(latLngList);
        mMap.addPolyline(options);


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(FollowOrderActivity.this,100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdate()
    {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        getDirection(String.valueOf(lat),String.valueOf(lng),String.valueOf(orderModel.getDriver().getLatitude()),String.valueOf(orderModel.getDriver().getLongitude()));
        if (googleApiClient!=null)
        {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient!=null)
        {
            if (locationCallback!=null)
            {
                LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
                googleApiClient.disconnect();
                googleApiClient = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == loc_req)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initGoogleApi();
            }else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100&&resultCode== Activity.RESULT_OK)
        {

            startLocationUpdate();
        }

    }

    @Override
    public void back() {

        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        finish();
    }
}
