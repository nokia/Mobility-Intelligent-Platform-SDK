package mip.belllabs.moveinsaclay.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;


public class CustomMapview extends MapView {
    public CustomMapview(Context context) {
        super(context);
    }

    public CustomMapview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMapview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomMapview(Context context, MapboxMapOptions options) {
        super(context, options);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        super.onTouchEvent(event);
        return true;
    }


}

