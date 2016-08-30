package com.example.hoon.educastcourselistdemo.scenes.main;

import com.example.hoon.educastcourselistdemo.models.Course;
import com.example.hoon.educastcourselistdemo.models.Paginator;

import java.util.List;

/**
 * Created by hoon on 2016. 8. 27..
 */
public class MainModel {
    public class CourseList {
        public class Request {
            public int fetchNum = 8;
            public int page;
        }
        public class Response {
            public Paginator<Course> paginator;
        }
        public class ViewModel {
            public class DisplayedCourse {
                public int pk;
                public String title;
                public String channelName;
            }
            public List<DisplayedCourse> displayedCourses;
        }

    }
}
