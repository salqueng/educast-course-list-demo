package com.example.hoon.educastcourselistdemo.scenes.main;

import android.content.Context;
import android.os.Handler;

import com.android.volley.Request;
import com.example.hoon.educastcourselistdemo.models.Course;
import com.example.hoon.educastcourselistdemo.network.QueryResult;
import com.example.hoon.educastcourselistdemo.network.RestManager;
import com.example.hoon.educastcourselistdemo.network.RestRequest;
import com.example.hoon.educastcourselistdemo.network.RestResponse;
import com.example.hoon.educastcourselistdemo.network.URLBuilder;
import com.example.hoon.educastcourselistdemo.network.VolleyManager;
import com.example.hoon.educastcourselistdemo.utils.reflect.MultipleParameterizedType;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hoon on 2016. 8. 27..
 */
interface MainInteractorInput {
    void fetchCourses(final MainModel.CourseList.Request request);
}
interface MainInteractorOutput {
    void presentCourses(final MainModel.CourseList.Response response);
    void presentError(MainModel.CourseList.ErrorResponse response);
}

public class MainInteractor implements MainInteractorInput {

    private Context mContext;
    private Handler mHandler;
    private RestManager mRestManager;
    private VolleyManager mVolleyManager;

    public MainInteractor(Context context) {
        mContext = context;
        mHandler = new Handler();
        mRestManager = RestManager.get(context);
        //mRestManager.setHeader("Authorization", "Bearer wisxpeyInPxplL2YtLy7RM2sZmL1ot");
        mVolleyManager = VolleyManager.get(context);
    }

    private MainInteractorOutput mOutput;

    public void setOutput(MainInteractorOutput output) {
        mOutput = output;
    }

    public MainInteractorOutput getOutput() {
        return mOutput;
    }

    public void setContext(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }

    @Override
    public void fetchCourses(final MainModel.CourseList.Request request) {
        final RestResponse.Listener<QueryResult<Course>> respListener = new RestResponse.Listener<QueryResult<Course>>() {
            @Override
            public void onResponse(final RestResponse<QueryResult<Course>> response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            MainModel.CourseList.Response resp = new MainModel.CourseList.Response();
                            resp.result = response.getResult();
                            mOutput.presentCourses(resp);
                        } else {
                            // send response with null result.
                            mOutput.presentError(new MainModel.CourseList.ErrorResponse("Error has occured. Please Try again"));
                        }
                    }
                });
            }
        };

        Type requestType = TypeToken.get(Course.class).getType();
        Type responseType = new MultipleParameterizedType(QueryResult.class, Course.class);
        final RestRequest<Type, QueryResult<Course>> restRequest = new RestRequest<>(
                Request.Method.GET, buildFetchUrl(request), null, requestType, responseType, new HashMap<String, String>(), respListener
        );
        mVolleyManager.getRequestQueue().add(restRequest);
    }

    private String buildFetchUrl(MainModel.CourseList.Request request){
        return URLBuilder.get().build(String.format(
                "search/query?page=%d&fetch_num=%d&target=course",
                request.page,
                request.fetchNum
        ));
    }

    private class OutputNotSetException extends Exception {
        public OutputNotSetException() {}

        public OutputNotSetException(String message) {
            super(message);
        }

        public OutputNotSetException(String message, Throwable cause) {
            super(message, cause);
        }

        public OutputNotSetException(Throwable cause) {
            super(cause);
        }

        public OutputNotSetException(Context context, int resId) {
            super(context.getResources().getString(resId));
        }

        public OutputNotSetException(Context context, int resId, Exception e) {
            super(context.getResources().getString(resId), e);
        }
    }
}
