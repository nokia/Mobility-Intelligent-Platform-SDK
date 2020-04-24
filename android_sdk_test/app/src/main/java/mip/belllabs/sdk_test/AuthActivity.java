package mip.belllabs.sdk_test;
import android.app.Activity;
import android.os.Bundle;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mip.belllabs.moveinsaclay.HubActivity;
import mip.belllabs.moveinsaclay.MIS_SDK.AuthManager;
import mip.belllabs.moveinsaclay.MIS_SDK.HubManager;

public class AuthActivity extends Activity {
    Boolean auth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FloatingActionButton fab = findViewById(R.id.fab);
        String providerName = "YOUR_AUTH_PROVIDER_URL";
        String idToken_M2I = "YOUR_RECENT_ID_TOKEN";

        fab.setOnClickListener(view -> {
            if (auth){
                HubManager.openActivity(this, HubActivity.class);
            } else {
                AuthManager.init_Auth_Federated(AuthActivity.this, providerName, idToken_M2I);
                auth = true;
            }
        });
    }
}