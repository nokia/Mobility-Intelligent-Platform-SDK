package mip.belllabs.moveinsaclay.MIS_SDK;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;


import mip.belllabs.moveinsaclay.MainActivity;
import mip.belllabs.moveinsaclay.R;

public class AuthManager {
    public static void init_Auth (final Context context, final Activity activity) {
        AWSMobileClient.getInstance().initialize(context, new AWSStartupHandler() {
            @Override
            public void onComplete(final AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config=
                        new AuthUIConfiguration.Builder()
                        .userPools(true)
                        .canCancel(false)
                        .logoResId(R.drawable.mis_co)
                        .backgroundColor(Color.parseColor("#132358"))
                        .build();
                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(context, SignInUI.class);
                signin.login(activity, MainActivity.class).authUIConfiguration(config).execute();
            }
        }).execute();
    }
}

