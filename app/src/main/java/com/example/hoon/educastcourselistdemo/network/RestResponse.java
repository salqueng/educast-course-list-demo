package com.example.hoon.educastcourselistdemo.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Created by hoon on 15. 1. 8..
 */
public class RestResponse<T> {
    private static final String LOG_TAG = "RestResponse";
    private NetworkResponse mResponse;
    private VolleyError mError;
    private String mErrorBody;
    private T mResult;

    public RestResponse(VolleyError error, NetworkResponse response) {
        // Error Logging;
        if (response != null && response.data != null) {
            mErrorBody = new String(response.data);
        } else {
            mErrorBody = "";
        }
        mError = error;
        mResponse = response;

        Log.e(LOG_TAG, "Error - " + error.toString(), error);
        Log.e(LOG_TAG, "Response as string - \n" + mErrorBody);
    }

    public RestResponse(T result, NetworkResponse response) {
        mResult = result;
        mResponse = response;
    }

    public String getErrorBody() {
        return mErrorBody;
    }

    public VolleyError getError() {
        return mError;
    }

    public NetworkResponse getResponse() {
        return mResponse;
    }

    public T getResult() {
        return mResult;
    }

    public boolean isError() {
        return mError != null;
    }

    public boolean isSuccess() {
        return mError == null;
    }

    public interface Listener<T> {
        void onResponse(RestResponse<T> response);
    }
}
