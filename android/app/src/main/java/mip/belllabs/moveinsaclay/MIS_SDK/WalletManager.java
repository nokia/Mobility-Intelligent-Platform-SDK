package mip.belllabs.moveinsaclay.MIS_SDK;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.IOUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONObject;

import java.io.InputStream;
import java.sql.Date;
import java.util.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mip.belllabs.moveinsaclay.Utils.GeofenceManager;
import mip.belllabs.moveinsaclay.Utils.MapTools;
import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idozwrau7p77.WalletManagerMobileHubClient;

public class WalletManager {
    private static double tokenValue;
    static DecimalFormat df = new DecimalFormat("0");

    /*
    * Manage User Wallet
    * */
    public  static  void doInvokeWalletAPI(final TextView token_TV , final WalletManagerMobileHubClient apiClient ){

        // Create components of api request
        final String method = "GET";

        final String path = "/token";

        // final String content = body.toString();

        //Log.d("WalletAPI", "doInvokeWalletAPI: "+content);
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
        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("WalletAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        System.out.println(" response from wallet Api : " + responseData);
                        Double tk=Double.parseDouble(responseData);
                        setNewTokenValue(tk);
                        token_TV.setText(df.format(getCurrentToken()) + " MIPS");
                        Log.d("WalletAPI", "Response : " + responseData);
                    }
                    else {
                        token_TV.setText("0");
                    }
                    Log.d("WalletAPI", response.getStatusCode() + " " + response.getStatusText());
                } catch (final Exception exception) {
                    Log.e("WalletAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }
    public static double getCurrentToken(){
        return  tokenValue;
    }
    public static  void setNewTokenValue(double token){
        // Set new token value and update the wallet UI
        tokenValue=token;
    }

    /*
     * Manage Nudge Based on Distance Trips
     * */
    public  static  void  manageNudgeEndOfTrip(double lat,double lng, double token, final WalletManagerMobileHubClient apiClient) {
        JSONObject body = new JSONObject();
        String currentDate = MobilityDataManager.doCurrentGetDate();
        try {
            body.put("description", "Nudge KM");
            body.put("date", "" + currentDate);
            body.put("transactionid", "" + UUID.randomUUID().toString());
            body.put("numbertoken", "" + token);
            body.put("latitude", "" + lat);
            body.put("longitude", "" + lng);

        } catch (Exception e) {

        }
        doInvokeRewardsAPI(body, apiClient);
        setNewTokenValue(token);
    }
    public  static  void manageNudgeZone(double lat,double lng, final WalletManagerMobileHubClient apiClient, final TextView token_TV){
        JSONObject body = new JSONObject();
        String currentDate = MobilityDataManager.doCurrentGetDate();
        double token = getCurrentToken() + 1.0;
        try {
            body.put("description", "Nudge Zone");
            body.put("date", "" + currentDate);
            body.put("transactionid", "" + UUID.randomUUID().toString());
            body.put("numbertoken", "" + token);
            body.put("latitude", "" + lat);
            body.put("longitude", "" + lng);

        } catch (Exception e) {

        }
        doInvokeRewardsAPI(body, apiClient);
        setNewTokenValue(token);
        token_TV.setText(df.format(token) + " MIPS");
    }
    public  static  void doInvokeRewardsAPI(JSONObject body, final WalletManagerMobileHubClient apiClient){
        // Create components of api request
        final String method = "POST";
        final String path = "/token";
        //JSONObject body=new JSONObject();
        final String content = body.toString();
        Log.d("WalletManagerAPI", "doInvokeRewardAPI: "+content);
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
                    Log.d("WalletManagerAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        DecimalFormat df = new DecimalFormat("0.00");
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                        //token_TV.setText(responseData);
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                    }
                    Log.d("WalletManagerAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("WalletManagerAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();

    }

    /*
    * Manage Nudge Based on Rules and Events
    * */
    public  static  void doInvokeWalletAPI_Rules(Context context,MapboxMap mapboxMap,final WalletManagerMobileHubClient apiClient ){

        // Create components of api request
        final String method = "GET";

        final String path = "/token/rules";

        // final String content = body.toString();

        //Log.d("WalletAPI", "doInvokeWalletAPI: "+content);
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
        final ApiRequest request = localRequest;

        // Make network call on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("WalletAPI_Rules",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);
                    List<Feature> zonesCoordinates = new ArrayList<>();
                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        System.out.println(" response from wallet Api : " + responseData);
                        String[] lines=responseData.split(";");
                        for(int i=0;i<lines.length;i++){
                            String line=lines[i];
                            double lat=Double.parseDouble(line.split(",")[0]);
                            double lng=Double.parseDouble(line.split(",")[1]);
                            int radius=Integer.parseInt(line.split(",")[2]);
                            String id=line.split(",")[3];
                            zonesCoordinates.add(Feature.fromGeometry(
                                    Point.fromLngLat(lng,lat)
                            ));
                            GeofenceManager.setupGeofencing(id, lat, lng, radius);
                        }
                        Log.d("WalletAPI", "Response : " + responseData);

                        if(!zonesCoordinates.isEmpty()){
                            FeatureCollection featureCollection = FeatureCollection.fromFeatures(zonesCoordinates);
                            MapTools.addZoneOnMapBoxMap(context,featureCollection,mapboxMap);
                        }
                    }
                    Log.d("WalletAPI", response.getStatusCode() + " " + response.getStatusText());
                } catch (final Exception exception) {
                    Log.e("WalletAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }
    public  static  void onEnterNudgeArea(String areaId,double areaLat,double areaLng,String transportMode,String userId,double timeStamp, final WalletManagerMobileHubClient apiClient){
        JSONObject body = new JSONObject();
        try {
            body.put("timestamp", timeStamp);
            body.put("eventid", UUID.randomUUID().toString());
            body.put("userid", ""+userId);
            body.put("transportmode", "" + transportMode);
            body.put("arealat", "" + areaLat);
            body.put("arealng", "" + areaLng);
            body.put("areaid", "" + areaId);

        } catch (Exception ex) {

        }
        System.out.println(body);
        WalletManager.doInvokeBehaviorEventAPI(body,apiClient);
    }
    public  static  void onStopDataSharing(double lat,double lng,String userId,double timeStamp, final WalletManagerMobileHubClient apiClient){
        JSONObject body = new JSONObject();
        try {
            body.put("timestamp", timeStamp);
            body.put("eventid", UUID.randomUUID().toString());
            body.put("userid", ""+userId);
            body.put("lat", "" + lat);
            body.put("lng", "" + lng);
        } catch (Exception ex) {

        }
        WalletManager.doInvokeSharingEventAPI(body,apiClient);
    }
    public  static  void onSurveyReveled(double lat,double lng,String userId,double timeStamp, final WalletManagerMobileHubClient apiClient){
        JSONObject body = new JSONObject();
        try {
            body.put("timestamp", timeStamp);
            body.put("eventid", UUID.randomUUID().toString());
            body.put("userid", ""+userId);
            body.put("lat", "" + lat);
            body.put("lng", "" + lng);
        } catch (Exception ex) {

        }
        WalletManager.doInvokeSurveyEventAPI(body,apiClient);
    }
    public  static  void doInvokeSharingEventAPI(JSONObject body, final WalletManagerMobileHubClient apiClient){
        // Create components of api request
        final String method = "POST";
        final String path = "/token/event/datasharing";
        //JSONObject body=new JSONObject();
        final String content = body.toString();
        Log.d("WalletManagerAPI", "oInvokeSharingEventAPI: "+content);
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
                    Log.d("oInvokeSharingEventAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        DecimalFormat df = new DecimalFormat("0.00");
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                        //token_TV.setText(responseData);
                        Log.d("oInvokeSharingEventAPI", "Response : " + responseData);
                    }
                    Log.d("oInvokeSharingEventAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("oInvokeSharingEventAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();

    }
    public  static  void doInvokeSurveyEventAPI(JSONObject body, final WalletManagerMobileHubClient apiClient){
        // Create components of api request
        final String method = "POST";
        final String path = "/token/event/survey";
        //JSONObject body=new JSONObject();
        final String content = body.toString();
        Log.d("WalletManagerAPI", "doInvokeSurveyEventAPI: "+content);
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
                    Log.d("oInvokeSharingEventAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        DecimalFormat df = new DecimalFormat("0.00");
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                        //token_TV.setText(responseData);
                        Log.d("oInvokeSharingEventAPI", "Response : " + responseData);
                    }
                    Log.d("oInvokeSharingEventAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("oInvokeSharingEventAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();

    }
    public  static  void doInvokeBehaviorEventAPI(JSONObject body, final WalletManagerMobileHubClient apiClient){
        // Create components of api request
        final String method = "POST";
        final String path = "/token/event/behavior";
        //JSONObject body=new JSONObject();
        final String content = body.toString();
        Log.d("WalletManagerAPI", "doInvokeBehaviorEventAPI: "+content);
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
                    Log.d("WalletManagerAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        DecimalFormat df = new DecimalFormat("0.00");
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                        //token_TV.setText(responseData);
                        Log.d("WalletManagerAPI", "Response : " + responseData);
                    }
                    Log.d("WalletManagerAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("WalletManagerAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();

    }

}
