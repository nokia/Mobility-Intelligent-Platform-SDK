package mip.belllabs.moveinsaclay;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;

import java.util.Calendar;
import java.util.List;

import mip.belllabs.moveinsaclay.MIS_SDK.AuthManager;
import mip.belllabs.moveinsaclay.Utils.GPSTracker;
import mip.belllabs.moveinsaclay.Utils.NotificationReceiver;

import static mip.belllabs.moveinsaclay.Utils.Constants.ONBOARDING_PREFERENCES;

public class OnboardingActivity extends AppCompatActivity  implements  AdapterView.OnItemSelectedListener, PermissionsListener {

    private Button agree, plus;
    private Context mContext;
    private Spinner spinner;
    String[] user_category={"#TeamEPAPS", "#TeamCPS", "#TeamSQY", "#TeamVGP", "#TeamUPSaclay", "#TeamVedecom", "#TeamNokia", "#TeamTransdev", "#TeamEDF", "#TeamOuiShare", "#Resident", "#Etudiant", "#Salarié"};
    String UserTag = "#TeamEPAPS";
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        mContext = this;
        agree = (Button) findViewById(R.id.agree);
        plus = (Button) findViewById(R.id.ensavoir);
        spinner=(Spinner)findViewById(R.id.user_category_spinner);
        settings = getSharedPreferences(ONBOARDING_PREFERENCES, MODE_PRIVATE);
        prefEditor = settings.edit();
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog.show(mContext, "MoveInSaclay", "Chargement en cours, veuillez patienter...");
                enableLocationComponent();
                prefEditor.putBoolean("Onboarding_21",true);
                prefEditor.commit();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(OnboardingActivity.this, PortailActivity.class);
                startActivity(myIntent);
            }
        });
        manageUserTagSpinner();
        boolean onboard = settings.getBoolean("Onboarding_21", false);
        if (onboard) {
            ProgressDialog.show(this, "MoveInSaclay", "Chargement en cours, veuillez patienter...");
            enableLocationComponent();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            AuthManager.init_Auth(mContext,OnboardingActivity.this);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    public void manageUserTagSpinner(){
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,user_category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String userTag = settings.getString("UserTag", null);
        if (userTag != null) {
            int spinnerPosition = adapter.getPosition(userTag);
            spinner.setSelection(spinnerPosition);
        } else {
            prefEditor.putString("UserTag",UserTag);
            prefEditor.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
        UserTag=user_category[position];
        prefEditor.putString("UserTag",UserTag);
        prefEditor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.permission_rationale, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, R.string.permission_rationale, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void addReminder() {
        addReminderAt(8);
        addReminderAt(17);
    }

    public void addReminderAt(Integer hour){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        Intent intent1 = new Intent(OnboardingActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(OnboardingActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) OnboardingActivity.this.getSystemService(OnboardingActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
    }
}
