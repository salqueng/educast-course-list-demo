package com.example.hoon.educastcourselistdemo.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.hoon.educastcourselistdemo.utils.gson.CommonGson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoon on 15. 1. 8..
 */
public class RestRequest<U, T> extends Request<RestResponse<T>> {
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private static final int DEFAULT_TIMEOUT_MS = 15000;


    private static final Gson gson = CommonGson.get();
    private final Map<String, String> mHeaders;
    private final U mObj;
    private final RestResponse.Listener<T> mListener;
    private final Type mRequestType;
    private final Type mResponseType;

    private RestManager mManager;

    public static Gson getGson() {
        return gson;
    }

    public RestRequest(int method, String url, U obj, Type requestType, Type responseType,
                       Map<String, String> headers, RestResponse.Listener<T> listener) {
        super(method, url, null);
        mHeaders = headers;
        mObj = obj;
        mListener = listener;
        mRequestType = requestType;
        mResponseType = responseType;

        // Default Timeout? 10s
        setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
    }

    public void setRestManager(RestManager manager) {
        mManager = manager;
    }

    @Override
    protected Response<RestResponse<T>> parseNetworkResponse(NetworkResponse response) {
        try {
            T obj;
            if (response.data.length == 0) {
                // No Object Case
                obj = null;
            } else {
                String json = new String(
                        response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8")
                );
                obj = gson.fromJson(json, mResponseType);
            }

            // Refinable Check
            if (obj instanceof IRestRefinable) {
                ((IRestRefinable) obj).refine();
            }

            // Notify & Check
            if (mManager != null) {
                int method;
                switch (getMethod()) {
                    case Request.Method.GET:
                        method = RestManager.CHANGE_TYPE_READ;
                        break;
                    case Request.Method.POST:
                        method = RestManager.CHANGE_TYPE_CREATE;
                        break;
                    case Request.Method.PUT:
                        method = RestManager.CHANGE_TYPE_UPDATE;
                        break;
                    case Request.Method.DELETE:
                        method = RestManager.CHANGE_TYPE_DELETE;
                        break;
                    default:
                        method = RestManager.CHANGE_TYPE_READ;
                }

                obj = (T) mManager.updateAndGetManagedObject(obj, method);
            }

            RestResponse<T> restResponse = new RestResponse<>(obj, response);
            return Response.success(
                    restResponse, HttpHeaderParser.parseCacheHeaders(response)
            );
        } catch (Exception e) {
            if (e instanceof UnsupportedEncodingException || e instanceof JsonSyntaxException) {
                VolleyError error = new ParseError(response);
                error.initCause(e);
                return Response.error(error);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mObj == null) {
            return null;
        }
        String jsonObj = gson.toJson(mObj, mRequestType);
        try {
            return jsonObj.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    jsonObj, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return new HashMap<>(mHeaders);
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    protected void deliverResponse(RestResponse<T> response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        RestResponse<T> errorResponse = new RestResponse<>(error, error.networkResponse);
        mListener.onResponse(errorResponse);
    }
}
