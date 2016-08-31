package com.example.hoon.educastcourselistdemo.scenes.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hoon.educastcourselistdemo.models.Course;
import com.example.hoon.educastcourselistdemo.models.Paginator;
import com.example.hoon.educastcourselistdemo.network.QueryResult;
import com.example.hoon.educastcourselistdemo.network.RestResponse;
import com.example.hoon.educastcourselistdemo.utils.gson.CommonGson;

import java.util.List;

/**
 * Created by hoon on 2016. 8. 27..
 */
public class MainModel {
    public static class CourseList {
        public static class Request {
            public int fetchNum = 8;
            public int page;

            public Request() {}

            public Request(int fetchNum, int page) {
                this.fetchNum = fetchNum;
                this.page = page;
            }
        }
        public static class Response {
            public QueryResult<Course> result;

            public Response() {}

            public Response(QueryResult<Course> result) {
                this.result = result;
            }
        }
        public static class ErrorResponse {
            public String message;

            public ErrorResponse(String message) {
                this.message = message;
            }
        }

        public static class SuccessViewModel {
            public static class DisplayedCourse implements Parcelable {
                public int pk;
                public String thumbnail;
                public String name;
                public String channelName;

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public String toString() {
                    return CommonGson.get().toJson(this);
                }

                @Override
                public void writeToParcel(Parcel parcel, int i) {
                    parcel.writeString(toString());
                }

                public static final Parcelable.Creator<DisplayedCourse> CREATOR = new Parcelable.Creator<DisplayedCourse>() {
                    @Override
                    public DisplayedCourse createFromParcel(Parcel parcel) {
                        return CommonGson.get().fromJson(parcel.readString(), DisplayedCourse.class);
                    }

                    @Override
                    public DisplayedCourse[] newArray(int size) {
                        return new DisplayedCourse[size];
                    }
                };
            }
            public List<DisplayedCourse> displayedCourses;
        }

    }
}
