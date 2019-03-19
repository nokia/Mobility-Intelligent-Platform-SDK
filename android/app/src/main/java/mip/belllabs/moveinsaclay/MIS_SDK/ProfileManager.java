package mip.belllabs.moveinsaclay.MIS_SDK;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.services.dynamodbv2.model.SSEType;
import com.amazonaws.util.IOUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import org.json.JSONObject;
import java.io.InputStream;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.*;
import java.io.*;

import mip.belllabs.moveinsaclay.R;
import mip.belllabs.moveinsaclay.Utils.MapTools;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idv3dzax3wy3.MobilityProfilMobileHubClient;

import static android.graphics.Color.rgb;
import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.geometryType;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class ProfileManager {
    private static double distance;
    private static double speed;
    private static String transportMode = "";

    public static  void doInvokeProfilAPI(final String type, final TextView resultTextView,final TextView costTextView,final TextView carbonTextView, final MobilityProfilMobileHubClient apiClient) {

        // Create components of api request
        final String method = "POST";

        final String path = "/profil";
        JSONObject body=new JSONObject();
        try {
            body.put("type",type);
        }catch (Exception e){}
        final String content = body.toString();

        Log.d("ProfilManagerAPI", "doInvokeMotionAPI: "+content);
        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");

        final Map headers = new HashMap<>();

        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .withParameters(parameters);
        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length()))
                    .withBody(content);
        }

        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("ProfilManagerAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());
                    final ApiResponse response = apiClient.execute(request);
                    final InputStream responseContentStream = response.getContent();
                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        DecimalFormat df = new DecimalFormat("0");
                        DecimalFormat df_carbone = new DecimalFormat("0");

                        if(type.equalsIgnoreCase("speed")) {
                            speed=Double.parseDouble(responseData);
                            resultTextView.setText(df.format(speed) + " Km/h");
                        }
                        if (type.equalsIgnoreCase("transport")){
                            transportMode = responseData;
                            switch (responseData){
                                case "car": {
                                    resultTextView.setText("Voiture");
                                    costTextView.setText(df.format(distance*0.22)+" € incluant "+df.format(distance*0.09) + " € Carburant");
                                    carbonTextView.setText(df_carbone.format(distance*0.135/7.2)+" Arbres");
                                } break;
                                case "automotive":{
                                    resultTextView.setText("Automotive (Voiture/Bus/Train)");
                                    costTextView.setText("75,20 € Navigo + frais voiture");
                                    carbonTextView.setText(df_carbone.format(distance*0.09/7.2)+" Arbres");
                                } break;
                                case "walking":{
                                    resultTextView.setText("Marche");
                                    costTextView.setText("75,20 € Navigo + frais voiture");
                                    carbonTextView.setText(df_carbone.format(0)+" Arbres");
                                } break;
                                case "train": {
                                    resultTextView.setText("Train/Metro");
                                    costTextView.setText("75,20 € Navigo");
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                case "bus": {
                                    resultTextView.setText("Bus");
                                    costTextView.setText("75,20 € Navigo");
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                case "train/bus": {
                                    resultTextView.setText("TC Metro/Bus");
                                    costTextView.setText("75,20 € Navigo");
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                default : {
                                    resultTextView.setText(responseData);
                                    costTextView.setText("75,20 € Navigo + frais voiture");
                                    carbonTextView.setText(df_carbone.format(distance*0.09/7.2)+" Arbres");
                                }
                            }
                        }
                        if(type.equalsIgnoreCase("distance")){
                            distance = Double.parseDouble(responseData);
                            distance=distance*0.001;
                            resultTextView.setText(df.format(distance)+" Km Parcourus avec MoveInSaclay");
                            switch (transportMode){
                                case "car": {
                                    carbonTextView.setText(df_carbone.format(distance*0.135/7.2)+" Arbres");
                                } break;
                                case "automotive":{
                                    carbonTextView.setText(df_carbone.format(distance*0.09/7.2)+" Arbres");
                                } break;
                                case "walking":{
                                    carbonTextView.setText(df_carbone.format(0)+" Arbres");
                                } break;
                                case "train": {
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                case "bus": {
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                case "train/bus": {
                                    carbonTextView.setText(df_carbone.format(distance*0.045/7.2)+" Arbres");
                                } break;
                                default : {
                                    carbonTextView.setText(df_carbone.format(distance*0.09/7.2)+" Arbres");
                                }
                            }
                        }
                        if (type.equalsIgnoreCase("duree")){
                            resultTextView.setText(df.format(Double.parseDouble(responseData))+"h de mobilité");
                        }
                        Log.d("ProfileManager", "Response : " + responseData);
                    }
                    Log.d("ProfilManagerAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("ProfileManagerAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }
    public  static  void doInvokeProfilAPI_distance(JSONObject body, final MobilityProfilMobileHubClient apiClient){
        // Create components of api request
        final String method = "POST";
        final String path = "/profil";
        //JSONObject body=new JSONObject();
        final String content = body.toString();
        Log.d("ProfileManagerAPI", "doInvokeProfilAPI_distance: "+content);
        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");
        final Map headers = new HashMap<>();
        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .withParameters(parameters);
        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length()))
                    .withBody(content);
        }
        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("ProfileManager",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        Log.d("ProfileManager", "Response : " + responseData);
                    }
                    Log.d("ProfileManager", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("ProfileManager", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }
    public  static  void manageEndOfTrip(double lat_start,double lng_start, double lat_stop,double lng_stop, double distance, double duration, String start_date,final MobilityProfilMobileHubClient apiClient) {
        JSONObject body = new JSONObject();
        String current_date = MobilityDataManager.doCurrentGetDate();
        try {
            body.put("type", "trip");
            body.put("lastdate", "" + current_date);
            body.put("firstdate", "" + start_date);
            body.put("distance", "" + distance);
            body.put("duration", "" + duration);
            body.put("latitude_start", "" + lat_start);
            body.put("longitude_start", "" + lng_start);
            body.put("latitude_stop", "" + lat_stop);
            body.put("longitude_stop", "" + lng_stop);

        } catch (Exception e) {
        }
        Log.d("endOfTrip", body.toString());
        ProfileManager.doInvokeProfilAPI_distance(body, apiClient);
    }
    public  static void doInvokeProfilAPI_record(Context context, MapboxMap mapboxMap, final MobilityProfilMobileHubClient apiClient){

        // fetch all user records and Viz in a Map View
        List<Feature> markerCoordinates = new ArrayList<>();
        final String method = "POST";

        final String path = "/profil";
        JSONObject body=new JSONObject();
        try {
            body.put("type","record");
        }catch (Exception e){}
        final String content = body.toString();

        Log.d("ProfilManagerAPI_record", "doInvokeMotionAPI_record: "+content);
        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");

        final Map headers = new HashMap<>();

        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .withParameters(parameters);
        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length()))
                    .withBody(content);
        }

        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("ProfilManagerAPI_record",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {


                        final String responseData = IOUtils.toString(responseContentStream);
                        String[] lines=responseData.split(";");
                        int s=lines.length;
                        for(int i=0;i<lines.length;i++){
                            String line=lines[i];
                            double lat=Double.parseDouble(line.split(",")[0]);
                            double lng=Double.parseDouble(line.split(",")[1]);
                            markerCoordinates.add(Feature.fromGeometry(
                                    Point.fromLngLat(lng,lat)
                            ));

                        }
                        if(!markerCoordinates.isEmpty()){
                            FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
                            MapTools.addSourceOnMapBoxMap(context,featureCollection,mapboxMap);
                        }

                        //System.out.println("Responce " +responseData);

                        //DecimalFormat df = new DecimalFormat("0.00");
                        Log.d("ProfileManager", "Response : " + responseData);
                        Log.d("ProfileManager", "Response : " + responseData);
                    }
                    Log.d("ProfilManagerAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("ProfileManagerAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();

    }
}
