package com.agx.mqtt.helper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;


import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.ui.MainService.getActivity;
import static com.agx.mqtt.ui.MainService.getStarter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.List;

public class GPSLocator {
    private static final String TAG = "GPSLocator";
    private Button b;
    private TextView t;
    private static LocationManager locationManager;
    private static LocationListener listener;
    private static Location mLocation;

    public GPSLocator() {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            listener = new LocationListener() {


                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mLocation = location;
                    Log.d(TAG, "onLocationChanged: "+"New Location available!");
                    String topic = getUniqueDeviceID()+"/location";
                    String message = "location update";
                    HashMap<String,String > extras = new HashMap<>();

                    extras.put("type","location");
                    extras.put("longitude", String.valueOf(location.getLongitude()));
                    extras.put("latitude", String.valueOf(location.getLatitude()));

                    getStarter().publish(topic,message,extras);
                }

                @Override
                public void onLocationChanged(@NonNull List<Location> locations) {
                    LocationListener.super.onLocationChanged(locations);
                }

                @Override
                public void onFlushComplete(int requestCode) {
                    LocationListener.super.onFlushComplete(requestCode);
                }

                @Override
                public void onProviderEnabled(String s) {
                    //
                }

                @Override
                public void onProviderDisabled(String s) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(i);
                }
            };
            configure_button();

    }

    public static void configure_button() {
        // first check for permissions
        Log.d(TAG, "configure_button: ");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
        } else {
            // permission has been granted
            Log.d(TAG, "configure_button: ");
            try{
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    private static void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                ACCESS_COARSE_LOCATION)) {

//            snackOnTop("Location permission is needed because ...");
            Log.d(TAG, "request_permission: \"Location permission is needed because ...\"");
        } else {
            // permission has not been granted yet. Request it directly.
            getActivity().requestPermissions(new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
    }

    public static Location getLocation() {
        return mLocation;
    }
}
