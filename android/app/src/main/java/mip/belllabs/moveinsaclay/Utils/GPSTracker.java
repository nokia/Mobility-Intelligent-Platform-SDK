package mip.belllabs.moveinsaclay.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;

public final class GPSTracker implements LocationListener {

    private final Context mContext;
    public boolean isGPSEnabled = false;
    public boolean isMock = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    double speed = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1 * 1000;
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        startLocationUpdate();
    }

    public void startLocationUpdate() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //Permission check
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    public Location getLocation() {
        return location;
    }
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }
    public double getSpeed() {
        if (location != null) {
            speed = location.getSpeed();
        }

        // return speed
        return speed;
    }
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Activation GPS");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Nous aurions besoin de votre position GPS pour le partage de vos données de mobilité et la navigation.");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        Log.d("isMock", String.valueOf(isMock));

        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed =location.getSpeed();
        canGetLocation= !isMock;
    }

    @Override
    public void onProviderDisabled(String provider) {
        canGetLocation= false;
        Log.d("GPS onProviderDisabled", provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        canGetLocation = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("GPS onStatusChanged", provider);
    }

}
