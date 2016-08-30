package com.example.hoon.educastcourselistdemo.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hoon on 15. 1. 12..
 */
public class VolleyManager {
    private static final String REQUEST_CACHE_DIR = "volley-request";
    private static final String IMAGE_CACHE_DIR = "volley-image";

    private static VolleyManager mInstance;

    private RequestQueue mRequestQueue;
    private Context mContext;

    private VolleyManager(Context ctx) {
        mContext = ctx;
        mRequestQueue = Volley.newRequestQueue(mContext, new OkHttpStack());

    }

    public static VolleyManager get(Context ctx) {
        if (mInstance == null) {
            mInstance = new VolleyManager(ctx);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
