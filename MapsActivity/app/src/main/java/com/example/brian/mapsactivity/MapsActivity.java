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
    private boolean isTracked = false;

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

        //mMap.setMyLocationEnabled(true);

    }

    public void getLocation() {

        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get gps status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled == true) {
                Log.d("MyMaps", "getLocation: GPS is enabled");
            }

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled == true) {
                Log.d("MyMaps", "getLocation: Network is enabled");
            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: No Provider is enabled");
            } else {
                canGetLocation = true;
                if (isGPSEnabled == true) {
                    Log.d("MyMaps", "getLocation: GPS enabled & requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.d("MyMaps", "Permissions granted");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGps);
                    Log.d("MyMaps", "getLocation: GPS update request is happening");
                    Toast.makeText(this, "Currently Using GPS", Toast.LENGTH_SHORT).show();
                }
                if (isNetworkEnabled == true) {
                    Log.d("MyMaps", "getLocation: Network enabled & requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    Log.d("MyMaps", "getLocation: Network update request is happening");
                    Toast.makeText(this, "Currently Using Network", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            Log.d("MyMaps", "Caught an exception in getLocation");
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
    /*
    public void search(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.searchField);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Search Results"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    } */


    public void dropMarker(String provider) {

        LatLng userLocation = null;

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
        }

        if (myLocation == null) {
            //display a log d message and/or toast
            Log.d("MyMaps", "dropMarker: myLocation is null");

        } else {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //display log d message and/or toast of coordinates
            Toast.makeText(MapsActivity.this, "" + myLocation.getLatitude() + ", " +myLocation.getLongitude(), Toast.LENGTH_SHORT).show();

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOCATION_ZOOM_FACTORY);

            //add a shape for a marker (don't use standard teardrop marker)
            Circle myCircle = mMap.addCircle(new CircleOptions().center(userLocation).radius(1).strokeColor(Color.MAGENTA).strokeWidth(2).fillColor(Color.MAGENTA));

            mMap.animateCamera(update);
        }

    }

    public void trackMe(View view) {
        isTracked = true;
        if (isTracked == true) {
            getLocation();
            isTracked = false;
        }
        if (isTracked == false) {
            return;
        }
    }


    LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output a message in log.D and toast
            Log.d("MyMaps", "GPS Location has changed");
            Toast.makeText(MapsActivity.this, "GPS Location has changed", Toast.LENGTH_SHORT).show();

            //drop a marker on the map (create a method called drop a marker)
            dropMarker(LocationManager.GPS_PROVIDER);

            // disable network updates (see locationManager API to remove updates)
            locationManager.removeUpdates(locationListenerNetwork);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //setup a switch statement on status
            //case: where location provider is available (output a Log.D or toast)
            //case: location LocationProvider.OUT_OF_SERvIce-> request updates from network provider
            //case: locationProvider.TEMPORARILY_UNAVAILABLE --> request updates from network provider

            switch (status) {
                case LocationProvider.AVAILABLE:

                    Log.d("MyMaps", "LocationProvider is available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:

                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);

                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;
                default:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    break;

            }


        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //output a message in log.D and toast
            Log.d("MyMaps", "Network Location has changed");
            Toast.makeText(MapsActivity.this, "Network Location has changed", Toast.LENGTH_SHORT).show();

            //drop a marker on the map (create a method called drop a marker)
            dropMarker(LocationManager.NETWORK_PROVIDER);

            //relaunch request for network location updates

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output a log.d and/or toast
            Log.d("MyMaps", "Network onStatusChanged called");
            Toast.makeText(MapsActivity.this, "Network onStatusChanged called", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };









}




