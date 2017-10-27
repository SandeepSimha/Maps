package com.bbva.my.bbvacompasaplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.bbva.my.bbvacompasaplication.model.BbvaModel;
import com.bbva.my.bbvacompasaplication.model.Geometry;
import com.bbva.my.bbvacompasaplication.model.Results;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * @author Sandeep Cherukuri on 10/27/17.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GenericReceiver.Receiver {

    private GoogleMap mMap;
    private GenericReceiver receiver;
    private ResponseReceiver responseReceiver;
    private List<BbvaModel> list;
    private Marker marker;

    Location mLocation;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    int PLACE_PICKER_REQUEST = 1;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        receiver = new GenericReceiver(new Handler());
        //receiver.setReceiver(this);

        /*IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        responseReceiver = new ResponseReceiver();
        registerReceiver(responseReceiver, filter);*/

        connectClient();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //BbvaModel bbvaModel = new BbvaModel();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        Intent pinRequestService = new Intent(Intent.ACTION_SYNC, null, this, ServiceClient.class);
        pinRequestService.putExtra(ServiceClient.PARAM_RECEIVER, receiver);
        startService(pinRequestService);

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            list = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject loc = jsonObject1.getJSONObject("geometry").getJSONObject("location");

                BbvaModel bbvaModel = new BbvaModel();
                bbvaModel.setLat(loc.getDouble("lat"));
                bbvaModel.setLang(loc.getDouble("lng"));
                bbvaModel.setName(jsonObject1.getString("name"));
                bbvaModel.setAddress(jsonObject1.getString("formatted_address"));

                if (list != null && list.size() > 0) {


                    if (mMap != null) {
                        mMap.clear();

                        if (mLocation != null) {
                            /*LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                            if (marker == null) {
                                marker = mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
                            } else {
                                marker.setPosition(sydney);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

                        }
                        for (BbvaModel latLng1 : list) {
                            LatLng latLng2 = new LatLng(latLng1.getLat(), latLng1.getLang());
                            mMap.addMarker(new MarkerOptions().position(latLng2).title(latLng1.getName()));
                        }
                    }

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Toast.makeText(MapsActivity.this, "Info window clicked", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MapsActivity.this, SecondLevelActivity.class);
                            LatLng latLng = marker.getPosition();
                            intent.putExtra("title", marker.getTitle());
                            //intent.putExtra("title", bbvaModel.getAddress());
                            intent.putExtra("latitude", latLng.latitude);
                            intent.putExtra("longitude", latLng.longitude);
                            startActivity(intent);
                        }
                    });


                }

                list.add(bbvaModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        receiver.setReceiver(null);
        super.onPause();
    }

    public void connectClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver.setReceiver(this);
        if (!checkPlayServices()) {
            Toast.makeText(getApplicationContext(), "Please install google play services", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MapsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }


    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case HttpURLConnection.HTTP_OK: {
                Log.d("MainActivity", "onReceive = ok");
                break;
            }
            case HttpURLConnection.HTTP_BAD_GATEWAY: {
                Log.d("MainActivity", "onReceive = bad");
                break;
            }
            default:
                Log.d("MainActivity", "onReceive = default");
                break;
        }

    }

    public static class ResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            //
            Log.d("MainActivity", "onReceive");
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}