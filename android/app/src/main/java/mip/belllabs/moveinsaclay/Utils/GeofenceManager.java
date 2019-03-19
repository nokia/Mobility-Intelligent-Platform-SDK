package mip.belllabs.moveinsaclay.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import mip.belllabs.moveinsaclay.services.GeofenceTransitionsIntentService;

public class GeofenceManager {
    static Context mContext;
    static GeofencingClient mGeofencingClient;
    static List<Geofence> mGeofenceList;
    static PendingIntent mGeofencePendingIntent;

    public GeofenceManager(Context context) {
        mContext = context;
    }


    public void startGeofence() {
        System.out.println("Starting Geofencing");
        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
    }

    public static void setupGeofencing(String GEOFENCE_ID, double GEOFENCE_LAT, double GEOFENCE_LONG, float GEOFENCE_RADIUS){

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(GEOFENCE_ID)

                .setCircularRegion(
                        GEOFENCE_LAT,
                        GEOFENCE_LONG,
                        GEOFENCE_RADIUS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |Geofence.GEOFENCE_TRANSITION_DWELL
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(2000)
                .build());

        System.out.println(mGeofenceList);
        System.out.println("GeoFence created");
        addGeofences();


    }

    private static void addGeofences() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {System.out.println("NO PERMISSION");}

        else {

            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            System.out.println("DONE");
                        }
                    })
                    .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            System.out.println("NOT DONE");
                            System.out.println(e);
                        }
                    });


        }
    }

    private static GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        System.out.println("Geofencing Request OK");

        return builder.build();
    }
    private static PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent( mContext, GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        System.out.println("Geofencing Intent OK");
        return mGeofencePendingIntent;
    }
}
