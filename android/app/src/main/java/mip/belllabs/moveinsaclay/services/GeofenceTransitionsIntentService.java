package mip.belllabs.moveinsaclay.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import mip.belllabs.moveinsaclay.MIS_SDK.WalletManager;
import mip.belllabs.moveinsaclay.MainActivity;
import mip.belllabs.moveinsaclay.R;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idozwrau7p77.WalletManagerMobileHubClient;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private WalletManagerMobileHubClient walletApiClient;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
        walletApiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance().getCredentialsProvider())
                .build(WalletManagerMobileHubClient.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        System.out.println("Geofencing Detection in Progress...");
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String geofenceTransitionString[] = {"", "ENTER", "EXIT" , "", "DWELL"};
        if (triggeringGeofences != null && !triggeringGeofences.isEmpty()) {
            Geofence currentGeofence = triggeringGeofences.get(0);
            System.out.println("Transition in " + currentGeofence.getRequestId() + " AREA Detected: " + geofenceTransitionString[geofenceTransition]);
            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                System.out.println("Current Geofence: " + currentGeofence);
                String transportMode = MainActivity.getZoneTransportMode();
                System.out.println("Transport Mode is:  " + transportMode);
                WalletManager.onEnterNudgeArea(currentGeofence.getRequestId(), geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude(), transportMode, "userid", System.currentTimeMillis(), walletApiClient);
            } else {
                // Log the error.
                //Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                //      geofenceTransition));
            }
        }
    }
}
