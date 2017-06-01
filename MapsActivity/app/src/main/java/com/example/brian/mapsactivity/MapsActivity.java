package com.example.brian.mapsactivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000*15;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5.0f;
    private Location myLocation = null;
    private LatLng userLocation = null;
    private static final float MY_LOCATION_ZOOM_FACTORY = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();


        // Add a marker in Sydney and move the camera
        LatLng birthPlace = new LatLng(32.7157, -117.1611);
        //LatLng currentPos = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(birthPlace).title("Born Here!!!!"));
        //mMap.addMarker(new MarkerOptions().position(currentPos).title("Current Pos!!!!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birthPlace));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MapsApp", "Permission check 1 failed");
            Log.d("MapsApp", Integer.toString(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MapsApp", "Failed Permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        mMap.setMyLocationEnabled(true);

    }

    public void getLocation() {
        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //get GPS status
            isGPSEnabled  = locationManager.isProviderEnabled(GPS_PROVIDER);
            if(isGPSEnabled) Log.d("MyMaps", "getLocation: GPS is enabled");
            //get Network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled) Log.d("MyMaps", "getLocation: Network is enabled");

            if(!isGPSEnabled && !isNetworkEnabled)
            {
                Log.d("MyMaps", "getLocation: No Provider is enabled!!");
            }else{
                this.canGetLocation = true;

                if(isGPSEnabled){
                    Log.d("MyMap", "getLocation : GPS Enabled - requesting location update");
                    Log.d("MyMaps", "getLocation: GPS Enabled - requesting location update");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    locationManager.requestLocationUpdates(GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGps);
                    Log.d("MyMap", "getLocation : Network GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
                if(isNetworkEnabled){
                    Log.d("MyMap", "getLocation : Network Enabled - requesting location update");
                    locationManager.requestLocationUpdates(NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    Log.d("MyMap", "getLocation : Network GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }

        } catch(Exception e){
            Log.d("MyMaps", "Caught an exception in my getLocation");
            e.printStackTrace();
        }


    }



    public void changeMapType(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    public void dropMarker(String provider) {

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
            if (myLocation == null){
                Log.d("MyMaps", "dropMarker : myLocation = null");
            }
            else{
                userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOCATION_ZOOM_FACTORY);
                Log.d("MyMaps", "dropMarker : userLocation, update camera");

                Circle circle = mMap.addCircle(new CircleOptions().center(userLocation).
                        radius(1.0).
                        strokeColor(Color.RED).
                        strokeWidth(2.0f).
                        fillColor(Color.TRANSPARENT));

                mMap.animateCamera(update);
            }


        }
    }

    public void trackMe(){

        getLocation();
    }


    LocationListener locationListenerGps = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            //output message in Log.d or Toast
            Log.d("MyMaps", "gps onLocationChanged: idk");

            //drop marker on map (create method called dropMarker)
            dropMarker(GPS_PROVIDER);

            //disable network updates (see LocataionManager api to remove updates (there's a method))
            locationManager.removeUpdates(locationListenerGps);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // set up switch statement on status
            // case: LocationProvider.AVAILABLE ---> output Log.d message and/or toast
            // case: LocationProvider.OUT_OF_SERVICE ---> request updates from NETWORK_PROVIDER
            // case: LocationProvider.TEMPORARILY_UNAVAILABLE ---> request updates from NETWORK_PROVIDER
            // case: default (should not happen)  ---> request updates from NETWORK_PROVIDER
            switch(status){
                case LocationProvider.AVAILABLE:
                    Log.d("MyMaps", "switch: location provider available");
                case LocationProvider.OUT_OF_SERVICE:
                    getLocation();
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    getLocation();
                default:
                    getLocation();

            }
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            //output message in Log.d or Toast

            //drop marker on map (create method called dropMarker)

            //relaunch request for network location updates (another call to requestLocationUpdates)

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            //output message in Log.d or toast
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };









}




