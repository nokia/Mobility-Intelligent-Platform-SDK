package mip.belllabs.sdk_test;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mip.belllabs.moveinsaclay.HubActivity;
import mip.belllabs.moveinsaclay.MIS_SDK.AuthManager;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AuthManager.init_Auth(TestActivity.this, TestActivity.this, HubActivity.class));
    }

}
