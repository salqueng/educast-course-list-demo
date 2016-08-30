package com.example.hoon.educastcourselistdemo.network;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by hoon on 15. 1. 9..
 */
public class RestRequestFuture<T> implements Future<RestResponse<T>>, RestResponse.Listener<T> {
    private Request<?> mRequest;
    private VolleyError mException;
    private RestResponse<T> mResult;
    private boolean mResultReceived = false;

    public static <E> RestRequestFuture<E> newFuture() {
        return new RestRequestFuture<>();
    }

    private RestRequestFuture() {

    }

    public void setRequest(Request<?> request) {
        mRequest = request;
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mRequest == null) {
            return false;
        }

        if (!isDone()) {
            mRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCancelled() {
        return mRequest != null && mRequest.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    @Override
    public RestResponse<T> get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public RestResponse<T> get(long timeout, @NonNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MICROSECONDS.convert(timeout, unit));
    }

    private synchronized RestResponse<T> doGet(Long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return mResult;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }

        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }

    @Override
    public synchronized void onResponse(RestResponse<T> response) {
        mResultReceived = true;
        mResult = response;
        notifyAll();
    }
}
