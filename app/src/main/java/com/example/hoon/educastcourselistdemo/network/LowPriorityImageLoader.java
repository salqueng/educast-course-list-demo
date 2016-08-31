package com.example.hoon.educastcourselistdemo.network;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;

/**
 * Created by hoon on 15. 1. 16..
 */
public class LowPriorityImageLoader extends ImageLoader {

    /**
     * Constructs a new ImageLoader.
     *
     * @param queue      The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public LowPriorityImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
    }

    @Override
    protected Request<Bitmap> makeImageRequest(
            String requestUrl, int maxWidth, int maxHeight,ImageView.ScaleType scaleType,
            final String cacheKey) {
        return new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onGetImageSuccess(cacheKey, response);
            }
        }, maxWidth, maxHeight, scaleType, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onGetImageError(cacheKey, error);
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
    }
}
