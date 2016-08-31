package com.example.hoon.educastcourselistdemo.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by hoon on 15. 1. 12..
 */
public class VolleyManager {
    private static final String REQUEST_CACHE_DIR = "volley-request";
    private static final String IMAGE_CACHE_DIR = "volley-image";

    private static VolleyManager mInstance;

    private RequestQueue mRequestQueue;
    private RequestQueue mImageRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;

    private VolleyManager(Context ctx) {
        mContext = ctx;
        mRequestQueue = Volley.newRequestQueue(mContext, new OkHttpStack());

        mImageRequestQueue = new RequestQueue(new DiskBasedCache(
                new File(mContext.getCacheDir(), IMAGE_CACHE_DIR), 32 * 1024 * 1024
        ), new BasicNetwork(new OkHttpStack()));

        mImageRequestQueue.start();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 16;

        mImageLoader = new LowPriorityImageLoader(
                this.mImageRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache =
                    new LruCache<String, Bitmap>(cacheSize) {
                        @Override
                        protected int sizeOf(String key, Bitmap value) {
                            // KB size (not bytes!)
                            return value.getByteCount() / 1024;
                        }
                    };

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        }
        );

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

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}


