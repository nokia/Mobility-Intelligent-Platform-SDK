package mip.belllabs.moveinsaclay.MIS_SDK;

import android.util.Log;
import android.widget.TextView;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.IOUtils;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import mip.belllabs.moveinsaclay.amazonaws.mobile.api.idpog7i433nj.MotionMobileHubClient;

public class MobilityDataManager {

    public static  void doInvokeMotionAPI(JSONObject body, final MotionMobileHubClient apiClient) {
        // Create components of api request
        final String method = "POST";

        final String path = "/traces";

        final String content = body.toString();

        Log.d("MotionAPI", "doInvokeMotionAPI: "+content);
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
                    Log.d("MotionAPI",
                            "Invoking API w/ Request : " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        Log.d("MotionAPI", "Response : " + responseData);
                        //token_TV.setText(responseData);
                        Log.d("WalletAPI", "Response : " + responseData);
                    }

                    Log.d("MotionAPI", response.getStatusCode() + " " + response.getStatusText());

                } catch (final Exception exception) {
                    Log.e("MotionAPI", exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public static  Double doGetDistance(double lat1 ,double lng1,double lat2,double lng2) {
        double distance;
        if ((lat1 == lat2)&(lng1==lng2)) {
            distance = 0;
        } else {
            //double RT = 6371.00;
            double dlt = lng1 - lng2;
            distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *Math.cos(Math.toRadians(dlt));
            distance = Math.acos(distance);
            distance = Math.toDegrees(distance);
            distance = distance *  60 * 1.1515;
            distance=distance*1.609344;
            if (Double.isNaN(distance)){
                distance=0.0;  // evoid NaN value
            }
        }
        return distance;
    }


    public static  String doCurrentGetDate() {
        String date;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        date = dateFormatter.format(new Date());
        return date;
    }
    public static  String doCurrentGetUIDate() {
        String date;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        date = dateFormatter.format(new Date());
        return date;
    }





}
