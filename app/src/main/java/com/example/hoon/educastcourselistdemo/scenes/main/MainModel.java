package com.example.hoon.educastcourselistdemo.scenes.main;

import com.example.hoon.educastcourselistdemo.models.Course;
import com.example.hoon.educastcourselistdemo.models.Paginator;
import com.example.hoon.educastcourselistdemo.network.QueryResult;
import com.example.hoon.educastcourselistdemo.network.RestResponse;

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
            public static class DisplayedCourse {
                public int pk;
                public String thumbnail;
                public String name;
                public String channelName;
            }
            public List<DisplayedCourse> displayedCourses;
        }

    }
}
