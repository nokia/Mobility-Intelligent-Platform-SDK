package mip.belllabs.moveinsaclay;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.AWSIdentityProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import mip.belllabs.moveinsaclay.Utils.MapTools;
import mip.belllabs.moveinsaclay.Utils.SVCMotion;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mip.belllabs.moveinsaclay.MIS_SDK.AuthManager;
import mip.belllabs.moveinsaclay.MIS_SDK.MobilityDataManager;
import mip.belllabs.moveinsaclay.MIS_SDK.ProfileManager;
import mip.belllabs.moveinsaclay.MIS_SDK.SurveyManager;
import mip.belllabs.moveinsaclay.MIS_SDK.WalletManager;
import mip.belllabs.moveinsaclay.Utils.Constants;
import mip.belllabs.moveinsaclay.Utils.GPSTracker;
import mip.belllabs.moveinsaclay.Utils.GeofenceManager;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idpog7i433nj.MotionMobileHubClient;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idozwrau7p77.WalletManagerMobileHubClient;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.id1m748ba2ph.MobilityProfilMobileHubClient;
import mip.belllabs.moveinsaclay.services.BackgroundActivityService;
import mip.belllabs.moveinsaclay.services.GeofenceTransitionsIntentService;
import mip.belllabs.moveinsaclay.services.LocationMonitoringService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static mip.belllabs.moveinsaclay.Utils.Constants.ONBOARDING_PREFERENCES;
import static mip.belllabs.moveinsaclay.Utils.MapTools.addIsochroneDataToMap;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener{
    private static String zoneTransportMode="";
    private MapView mapView;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView gpsStateTV, speedTV, timeStampTV, transportModeTV, distanceTV, incitationTV;
    private TextView costTextView, carbonTextView, distanceTextView, speedTextView, transportModeTextView, dureeTextView;
    private TextView tokenTextView;
    private Switch onDataSharing;
    private  Switch automotive_voiture,automotive_Bus,automotive_train;
    private boolean mMotionAvailble = false;
    private boolean dataSharing = false;
    private String mActivity = " ";
    private  String transportMode="";
    private  String modeAccuracy="";
    private boolean mAlreadyStartedService = false;

    private Double current_lat, current_lng, waypointLat,waypointLng,lat_start, lng_start, last_speed=0.0, current_speed = 0.0;
    private long current_timestamp = 0, start_timestamp = 0 , last_timestamp = 0;
    private double distance = 0, delta_distance = 0, tokenDistance = 0, newToken = 0;
    private String start_date;
    DecimalFormat df = new DecimalFormat("0.0000");
    DecimalFormat df1 = new DecimalFormat("0.00");
    private MotionMobileHubClient motionApiClient;
    private WalletManagerMobileHubClient tokenApiClient;
    private  MobilityProfilMobileHubClient mobilityProfilApiClient;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;
    private GPSTracker trackerGPS;

    private Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    private WebView mWebView;
    private ViewFlipper mViewFlipper;
    private Button surveyButton;
    private MapboxMap mapboxMap;
    private  MapboxMap incitationZoneMapboxMap;
    private  MapView incitationMapview;
    private Location nudge_location, current_location;

    private SharedPreferences settings;
    private GeofenceManager mGeofenceManager;
    private SVCMotion SVClf;

    SharedPreferences.Editor prefEditor;

    private Boolean isTrain=false,isBus=false, isCar =false;
    Float val1, val2, val3;
    List<LatLng> localCoordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSetting();
        Mapbox.getInstance(this, getString(R.string.mapbox_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapViewId);
        incitationMapview=(MapView) findViewById(R.id.incitation_map_view_id) ;
        mapView.onCreate(savedInstanceState);
        incitationMapview.onCreate(savedInstanceState);
        mGeofenceManager = new GeofenceManager(this);
        mGeofenceManager.startGeofence();
        setupUIelements();
        addListeners();
        setupApis();
        initMapzone(incitationMapview);
        initSurvey(surveyButton);
        setupSensorManager();
        SurveyManager.loadSurvey(mWebView);
        ProfileManager.doInvokeProfilAPI("distance", distanceTextView, costTextView, carbonTextView,mobilityProfilApiClient);
        ProfileManager.doInvokeProfilAPI("speed", speedTextView, costTextView, carbonTextView,mobilityProfilApiClient);
        ProfileManager.doInvokeProfilAPI("transport", transportModeTextView, costTextView, carbonTextView, mobilityProfilApiClient);
        ProfileManager.doInvokeProfilAPI("duree", dureeTextView, costTextView, carbonTextView, mobilityProfilApiClient);
        WalletManager.doInvokeWalletAPI(tokenTextView, tokenApiClient);
        startActivityTracking();
        checkForUpdates();
        // Estimators:
        try {
            SVClf = new SVCMotion(loadGeoJsonFromAsset("data.json"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setupUIelements(){
        mViewFlipper =(ViewFlipper)findViewById(R.id.view_filpper_id);
        surveyButton=(Button)findViewById(R.id.survey_button_id);
        /*gpsStateTV = (TextView) findViewById(R.id.idGpsState);
        speedTV = (TextView) findViewById(R.id.idSpeedState);
        timeStampTV =(TextView)findViewById(R.id.timeStampId);*/


        //incitationTV=(TextView)findViewById(R.id.id_incitation);
        transportModeTV=(TextView)findViewById(R.id.transportyModeId);
        onDataSharing =(Switch)findViewById(R.id.dataSharingSwitchId);
        //distanceTV=(TextView)findViewById(R.id.distanceId);
        costTextView =(TextView)findViewById(R.id.id_cost_of_mobility_per_day) ;
        carbonTextView =(TextView)findViewById(R.id.id_co2_level);
        distanceTextView =(TextView)findViewById(R.id.id_cost_of_mobility_message);
        tokenTextView = (TextView) findViewById(R.id.tokenId);
        transportModeTextView = (TextView) findViewById(R.id.idMostUserTransportMode);
        dureeTextView = (TextView) findViewById(R.id.idDuree);
        speedTextView = (TextView) findViewById(R.id.idAvarageSpeedPerDay);
        mWebView = (WebView) findViewById(R.id.idWebView);
        automotive_voiture=(Switch)findViewById(R.id.automotive_voiture_id);
        automotive_train=(Switch)findViewById(R.id.automotive_train_id);
        automotive_Bus=(Switch)findViewById(R.id.automotive_Bus_id);
    }
    public void setupSetting(){
        settings = getSharedPreferences(ONBOARDING_PREFERENCES, MODE_PRIVATE);
        prefEditor = settings.edit();
        nudge_location = new Location("");
        nudge_location.setLatitude(48.669064);
        nudge_location.setLongitude(2.234261);
    }
    public void setupApis(){
        motionApiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance().getCredentialsProvider())
                .build(MotionMobileHubClient.class);
        tokenApiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance().getCredentialsProvider())
                .build(WalletManagerMobileHubClient.class);
        mobilityProfilApiClient=new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance().getCredentialsProvider())
                .build(MobilityProfilMobileHubClient.class);
    }
    public void setupSensorManager(){
        trackerGPS = new GPSTracker(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new SensorEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (dataSharing == false) {
                    /*gpsStateTV.setText("off");
                    speedTV.setText("off");
                    timeStampTV.setText("off");*/
                    transportModeTV.setText("off");
                    /*distanceTV.setText("off");
                    incitationTV.setText("off");*/
                    distance=0;
                    tokenDistance = 0;
                } else {
                    if (event.values.length >= 3) {
                        val1 = event.values[0];
                        val2 = event.values[1];
                        val3 = event.values[2];
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            if (trackerGPS.canGetLocation) {
                                if (firstsharing && trackerGPS.getLatitude() != 0 && trackerGPS.getLongitude() != 0) {
                                    waypointLat = trackerGPS.getLatitude();
                                    waypointLng = trackerGPS.getLongitude();
                                    lat_start = waypointLat;
                                    lng_start = waypointLng;
                                    start_date = MobilityDataManager.doCurrentGetDate();
                                    start_timestamp = System.currentTimeMillis();
                                    firstsharing = false;
                                }
                                current_timestamp = System.currentTimeMillis();
                                if (mMotionAvailble && current_timestamp - last_timestamp >= 5000 && waypointLat != null && waypointLng != null) {
                                    current_location = trackerGPS.getLocation();
                                    current_lat = trackerGPS.getLatitude();
                                    current_lng = trackerGPS.getLongitude();
                                    last_speed = trackerGPS.getSpeed() * 3.6;
                                    delta_distance = MobilityDataManager.doGetDistance(waypointLat, waypointLng, current_lat, current_lng);
                                    distance = distance + delta_distance;
                                    tokenDistance = tokenDistance + delta_distance;
                                    if (tokenDistance >= 10000) {
                                        long duration = (current_timestamp - start_timestamp) / 1000;
                                        ProfileManager.manageEndOfTrip(lat_start, lng_start, current_lat, current_lng, tokenDistance * 1000, duration, start_date, mobilityProfilApiClient);
                                        ProfileManager.doInvokeProfilAPI("distance", distanceTextView, costTextView, carbonTextView, mobilityProfilApiClient);
                                        ProfileManager.doInvokeProfilAPI("speed", speedTextView, costTextView, carbonTextView,mobilityProfilApiClient);
                                        ProfileManager.doInvokeProfilAPI("transport", transportModeTextView, costTextView, carbonTextView, mobilityProfilApiClient);
                                        ProfileManager.doInvokeProfilAPI("duree", dureeTextView, costTextView, carbonTextView, mobilityProfilApiClient);
                                        ProfileManager.doInvokeProfilAPI_record(getApplicationContext(), mapboxMap, mobilityProfilApiClient);
                                        WalletManager.doInvokeWalletAPI(tokenTextView, tokenApiClient);
                                        tokenDistance = 0;
                                    }

                                    waypointLat = current_lat;
                                    waypointLng = current_lng;
                                    last_timestamp = System.currentTimeMillis();
                                    current_speed = last_speed;

                                    if (current_speed == 0) {
                                        mActivity = getString(R.string.activity_still);
                                    }

                                    if (current_speed > 3 && mActivity.contains(getString(R.string.activity_still))) {
                                        mActivity = getString(R.string.activity_walking);
                                    }
                                    if (current_speed > 10 && mActivity.contains(getString(R.string.activity_walking))) {
                                        mActivity = getString(R.string.activity_in_vehicle);
                                    }
                                    if (mActivity.contains(getString(R.string.activity_in_vehicle))) {
                                        modeAccuracy = "moving";
                                        if (isBus && !isTrain && !isCar) {
                                            transportMode = "bus";
                                        } else if (isTrain && !isBus && !isCar) {
                                            transportMode = "train";
                                        } else if (isCar && !isTrain && !isBus) {
                                            transportMode = "car";
                                        } else if (isTrain && isBus && !isCar) {
                                            transportMode = "train/bus";
                                        } else if (!isTrain && isBus && isCar) {
                                            transportMode = "bus/car";
                                        } else if (isTrain && !isBus && isCar) {
                                            transportMode = "train/car";
                                        } else {
                                            double[] my_array = {0., current_lat,  current_lng,  current_speed};
                                            int prediction = SVClf.predict(my_array);
                                            transportMode = SVClf.labels[prediction];
                                            Log.d("Prediction SVC", transportMode);
                                        }
                                    } else {
                                        transportMode = mActivity;
                                        modeAccuracy = mActivity;
                                    }
                                    //distanceTV.setText(df1.format(distance) + " KM");
                                    //gpsStateTV.setText(df.format(current_lat) + " , " + df.format(current_lng));
                                    //speedTV.setText(df1.format(current_speed) + " Km/h");
                                    String curentDate=MobilityDataManager.doCurrentGetUIDate();
                                    //timeStampTV.setText(curentDate);
                                    transportModeTV.setText(transportMode);
                                    zoneTransportMode = transportMode;
                                    Double accelXY = Math.sqrt(Math.pow(val1,2)+Math.pow(val2,2));

                                    String usertag = settings.getString("UserTag", null);
                                    JSONObject body = new JSONObject();
                                    try {
                                        body.put("itemId", UUID.randomUUID().toString());
                                        body.put("baseMotionMode", mActivity);
                                        body.put("latitude", "" + current_lat);
                                        body.put("longitude", "" + current_lng);
                                        body.put("speed", "" + current_speed);
                                        body.put("timestamp", "" + System.currentTimeMillis());
                                        body.put("xyAcceleration", "" + accelXY);
                                        body.put("yAcceleration", "" + val2);
                                        body.put("zAcceleration", "" + val3);
                                        body.put("transportMotionMode", transportMode);
                                        body.put("modeAccuracy", modeAccuracy);
                                        body.put("deviceType", "1");
                                        body.put("usertag", usertag);
                                    } catch (Exception ex) {
                                    }
                                    Log.d("Motion", body.toString());
                                    if (!mActivity.contains(getString(R.string.activity_still))) {

                                        // Add current GPS position to the map
                                        localCoordinates.add(new LatLng(current_lat, current_lng));
                                        mapboxMap.addPolyline(new PolylineOptions()
                                                .addAll(localCoordinates)
                                                .color(Color.parseColor("#3bb2d0"))
                                                .width(2));
                                        MobilityDataManager.doInvokeMotionAPI(body, motionApiClient);
                                    }
                                }

                            } else {
                                System.out.println("Unable to get Location");
                            }
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        CheckGooglePlayServices();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));


    }
    public void addListeners(){
        //Transport Mode Listeners Init
        automotive_voiture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCarChecked) {
                if(isCarChecked){
                    isCar =true;
                    automotive_voiture.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 20, 255, 0)));
                    automotive_voiture.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 20, 153, 0)));
                }else {
                    isCar =false;
                    automotive_voiture.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 236, 236, 236)));
                    automotive_voiture.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 0, 0, 0)));
                }
                prefEditor.putBoolean("isCar",isCar);
                prefEditor.commit();
            }
        });
        automotive_train.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isTrainChecked) {
                if(isTrainChecked){
                    isTrain=true;
                    automotive_train.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 20, 255, 0)));
                    automotive_train.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 20, 153, 0)));
                }else{
                    isTrain=false;
                    automotive_train.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 236, 236, 236)));
                    automotive_train.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 0, 0, 0)));
                }
                prefEditor.putBoolean("isTrain",isTrain);
                prefEditor.commit();
            }
        });
        automotive_Bus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isBusChecked) {
                if(isBusChecked){
                    isBus=true;
                    automotive_Bus.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 20, 255, 0)));
                    automotive_Bus.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 20, 153, 0)));
                }else {
                    isBus=false;
                    automotive_Bus.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 236, 236, 236)));
                    automotive_Bus.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 0, 0, 0)));
                }
                prefEditor.putBoolean("isBus",isBus);
                prefEditor.commit();
            }
        });
        initTransportMode(automotive_voiture, "isCar", isCar);
        initTransportMode(automotive_Bus, "isBus", isBus);
        initTransportMode(automotive_train, "isTrain", isTrain);
        Log.d("isCar",isCar.toString());
        Log.d("isBus",isBus.toString());
        Log.d("isTrain",isTrain.toString());

        //Data Sharing Listener
        onDataSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                onDataSharingChecked (isChecked);
                prefEditor.putBoolean("onDataSharing",isChecked);
                prefEditor.commit();
            }

        });


    }

    public void initSurvey (Button button) {
        Boolean isServeyDone = settings.getBoolean("onSurveyDone_23", false);
        if(!isServeyDone){
            button.setText("1 Nouvelle Enquete Disponible ");
            SurveyManager.loadSurvey(mWebView);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double lat=-1.0;
                    double lng=-1.0;
                    if(current_lat!=null&&current_lng!=null) {
                        lat = current_lat;
                        lng = current_lng;
                    }
                    WalletManager.onSurveyReveled(lat,lng, "", System.currentTimeMillis(), tokenApiClient);
                    prefEditor.putBoolean("onSurveyDone_23",true);
                    prefEditor.commit();
                    mViewFlipper.showNext();
                }
            });
        }else{
            button.setText("0 Enquete Disponible ");
        }
    }
    public void onStoptDataSharing () {
            double lat=-1.0;
            double lng=-1.0;
            if(current_lat!=null&&current_lng!=null) {
                lat = current_lat;
                lng = current_lng;
            }
            WalletManager.onStopDataSharing(lat, lng, "", System.currentTimeMillis(), tokenApiClient);
    }

    public void initDataSharing () {
        Boolean isChecked = settings.getBoolean("onDataSharing", false);
        onDataSharingChecked (isChecked);
    }

    public void initTransportMode(Switch mSwitch, String id, Boolean state){
        state = settings.getBoolean(id, false);
        if (state) {
            mSwitch.setChecked(true);
            mSwitch.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 20, 255, 0)));
            mSwitch.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 20, 153, 0)));
        }
    }

    public boolean firstsharing = false;
    public void onDataSharingChecked (boolean isChecked) {
        if (isChecked) {
            dataSharing = true;
            onDataSharing.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 20, 255, 0)));
            onDataSharing.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 20, 153, 0)));
            /*gpsStateTV.setText("En attente");
            speedTV.setText("En attente");
            timeStampTV.setText("En attente");*/
            transportModeTV.setText("En attente");
            /*distanceTV.setText("En attente");
            incitationTV.setText("En attente");*/
            trackerGPS.startLocationUpdate();
            firstsharing = true;
            /*Toast.makeText(getApplicationContext(),
                        "Partage de données activé", Toast.LENGTH_LONG).show();*/
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            dataSharing=false;
            onDataSharing.setThumbTintList(ColorStateList.valueOf(Color.argb(255, 236, 236, 236)));
            onDataSharing.setTrackTintList(ColorStateList.valueOf(Color.argb(255, 0, 0, 0)));
            /*gpsStateTV.setText("off");
            speedTV.setText("off");
            timeStampTV.setText("off");*/
            transportModeTV.setText("off");
            /*distanceTV.setText("off");
            incitationTV.setText("off");*/
            trackerGPS.stopUsingGPS();
            mSensorManager.unregisterListener(mSensorListener);
            if (lat_start != null && lng_start != null && distance > 0) {
                long duration = (System.currentTimeMillis() - start_timestamp) / 1000;
                Toast.makeText(getApplicationContext(),
                        "Partage de données désactivé", Toast.LENGTH_LONG).show();
                ProfileManager.manageEndOfTrip(lat_start, lng_start, current_lat, current_lng, distance * 1000, duration, start_date, mobilityProfilApiClient);
            }
            onStoptDataSharing();
            tokenDistance = 0;
            distance = 0;
        }

        onDataSharing.setChecked(isChecked);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Etes-vous sûr de vouloir eteindre l'application ? Le partage de vos données sera désactivé.")
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        incitationMapview.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        incitationMapview.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        incitationMapview.onStart();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, LocationMonitoringService.class));
        stopActivityTracking();
        mAlreadyStartedService = false;
        super.onDestroy();
        mapView.onDestroy();
        incitationMapview.onDestroy();
        Log.d("Activity","onDestroy");
        unregisterManagers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        incitationMapview.onStop();
    }

    /*
        Permissions
    */
    private void CheckGooglePlayServices() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {
            CheckInternet(null);

        } else {
            Toast.makeText(getApplicationContext(), "no_google_playservice_available", Toast.LENGTH_LONG).show();
        }
    }
    private Boolean CheckInternet(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }

        if (dialog != null) {
            dialog.dismiss();
        }

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            trackerGPS.startLocationUpdate();
            mapView.getMapAsync(this);
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Internet Connection");
        builder.setMessage("No Internet");
        String positiveText = "Refresh";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Block the Application Execution until user grants the permissions
                        if (CheckInternet(dialog)) {
                            //Now make sure about location permission.
                            if (checkPermissions()) {
                                trackerGPS.startLocationUpdate();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                trackerGPS.startLocationUpdate();
                mapView.getMapAsync(this);
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /*private void startMotionMonitoring() {
        if (!mAlreadyStartedService ) {
            Log.d("Main","Location Started");
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);
            mAlreadyStartedService = true;
        }
    }*/

    private void startActivityTracking() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundActivityService.class);
        startService(intent1);
        initDataSharing ();
    }
    private void stopActivityTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundActivityService.class);
        stopService(intent);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                break;
            }

            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_walking);
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = getString(R.string.activity_walking);
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.d(TAG, "User activity: " + label + ", Confidence: " + confidence);

        if (confidence > Constants.CONFIDENCE) {
            System.out.println(label);
            mActivity = label;
            System.out.println("Confidence: " + confidence);
            mMotionAvailble = true;
        }
    }


    /*
        Maps & Navigation
    */
    public  void initMapzone(MapView mapView){
        //private final Handler mHandler = new Handler(Looper.getMainLooper())
        Context context=this.getApplicationContext();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                incitationZoneMapboxMap=mapboxMap;
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .zoom(9)
                        .build());
                WalletManager.doInvokeWalletAPI_Rules(context,incitationZoneMapboxMap,tokenApiClient);

            }
        });

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap=mapboxMap;

        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(this);
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);

        this.mapboxMap.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(48.66,2.24))
                .zoom(15)
                .bearing(0)
                .tilt(50)
                .build());
        Location currentGPS = trackerGPS.getLocation();

        if (! trackerGPS.canGetLocation) {
            trackerGPS.showSettingsAlert();
        }

        try {
            addClusteredGeoJsonSource();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.mapboxMap.addOnMapClickListener(this);
    }



    @Override
    public void onMapClick(@NonNull LatLng point){
        if (destinationMarker != null) {
            mapboxMap.removeMarker(destinationMarker);
        }
        destinationCoord = point;
        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                .position(destinationCoord)
        );
        trackerGPS.getLocation();
        if (trackerGPS.canGetLocation) {
            current_lat = trackerGPS.getLatitude();
            current_lng = trackerGPS.getLongitude();

            originCoord = new LatLng(current_lat, current_lng);
            destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
            originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
            getRoute(originPosition, destinationPosition);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Activation de votre GPS necessaire !", Toast.LENGTH_LONG).show();
        }
    }

    private void addClusteredGeoJsonSource() throws MalformedURLException {
        ProfileManager.doInvokeProfilAPI_record(this, mapboxMap, mobilityProfilApiClient);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                        //goDirection.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    private String loadGeoJsonFromAsset(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /*
        HockeyApp Updates & Crash
    */
    private void checkForCrashes() {
        CrashManager.register(this);
    }
    private void checkForUpdates() {
        UpdateManager.register(this);
    }
    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    public static String getZoneTransportMode() {
        return zoneTransportMode;
    }

}