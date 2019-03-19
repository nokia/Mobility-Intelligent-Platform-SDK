package mip.belllabs.moveinsaclay.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.RatingCompat;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import mip.belllabs.moveinsaclay.R;

import static com.google.android.gms.internal.zzahn.runOnUiThread;
import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.geometryType;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.step;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapTools {
    public static void addIsochroneDataToMap(MapboxMap mapboxMap, GeoJsonSource isochroneSource){
        mapboxMap.addSource(isochroneSource);
        FillLayer isochroneFillLayer = new FillLayer("orsay_isochrone_fileLayer", "orsay_isochrone");
        isochroneFillLayer.setFilter(Expression.eq(geometryType(), literal("Polygon")));
        isochroneFillLayer.withProperties(
                fillColor(step((get("value")), rgb(0,0,0),
                        stop(600, rgb(0,10,255)),
                        stop(1200, rgb(0,10,155)),
                        stop(1800, rgb(0,10,55)))),
                fillOpacity(get("opacity"))
        );
        mapboxMap.addLayer(isochroneFillLayer);
    }

    public static void addSourceOnMapBoxMap(Context context, FeatureCollection featureCollection, MapboxMap mapboxMap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Source geoJsonSource = new GeoJsonSource("cordonates", featureCollection,
                        new GeoJsonOptions()
                                .withCluster(true)
                                .withClusterMaxZoom(20)
                                .withClusterRadius(50));

                mapboxMap.addSource(geoJsonSource);

                // Use the cordonates  source to create three layers: One layer for each cluster category.
                // Each point range gets a different fill color.
                int[][] layers = new int[][] {
                        new int[] {200, ContextCompat.getColor(context, R.color.red)},
                        new int[] {150, ContextCompat.getColor(context, R.color.orangeColor)},
                        new int[] {0, ContextCompat.getColor(context, R.color.nokiaBlueColor)}
                };

                //Creating a marker layer for single data points
                SymbolLayer unclustered = new SymbolLayer("unclustered-points", "cordonates");

                unclustered.setProperties(
                        iconImage("cross-icon-id"),
                        iconSize(
                                division(
                                        get("mag"), literal(4.0f)
                                )
                        ),
                        iconColor(
                                interpolate(exponential(1), get("mag"),
                                        stop(2.0, ContextCompat.getColor(context, R.color.greenColor)),
                                        stop(4.5,ContextCompat.getColor(context, R.color.nokiaBlueColor)),
                                        stop(7.0, ContextCompat.getColor(context, R.color.red))
                                )
                        )
                );
                mapboxMap.addLayer(unclustered);

                for (int i = 0; i < layers.length; i++) {
                    //Add clusters' circles
                    CircleLayer circles = new CircleLayer("cluster-" + i, "cordonates");
                    circles.setProperties(
                            circleColor(layers[i][1]),
                            circleOpacity(0.9f),
                            circleStrokeColor(Color.WHITE),
                            circleStrokeWidth(2f),
                            circleRadius(25f)
                    );

                    Expression pointCount = toNumber(get("point_count"));

                    // Add a filter to the cluster layer that hides the circles based on "point_count"
                    circles.setFilter(
                            i == 0
                                    ? all(has("point_count"),
                                    gte(pointCount, literal(layers[i][0]))
                            ) : all(has("point_count"),
                                    gt(pointCount, literal(layers[i][0])),
                                    lt(pointCount, literal(layers[i - 1][0]))
                            )
                    );
                    mapboxMap.addLayer(circles);
                }

                //Add the count labels
                SymbolLayer count = new SymbolLayer("count", "cordonates");
                count.setProperties(
                        textField(Expression.toString(get("point_count"))),
                        textSize(12f),
                        textColor(Color.WHITE),
                        textIgnorePlacement(true),
                        textAllowOverlap(true)
                );
                mapboxMap.addLayer(count);
            }
        });
    }
    public static void addZoneOnMapBoxMap(Context context,FeatureCollection featureCollection, MapboxMap mapboxMap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Source geoJsonSource = new GeoJsonSource("zones", featureCollection);
                //mapboxMap.setStyle(Style.LIGHT);
                mapboxMap.addSource(geoJsonSource);
                // Use the cordonates  source to create three layers: One layer for each cluster category.
                // Each point range gets a different fill color.
                //Creating a marker layer for single data points
                //SymbolLayer unclustered = new SymbolLayer("unclustered-points", "cordonates");
                    //Add clusters' circles
                    CircleLayer circles = new CircleLayer("circleId", "zones");
                    circles.setProperties(
                            circleColor(Color.RED),
                            circleOpacity(0.25f),
                            circleStrokeColor(Color.WHITE),
                            circleStrokeWidth(1f),
                            circleRadius(25f)
                    );
                    mapboxMap.addLayer(circles);
                }

        });
    }
}
