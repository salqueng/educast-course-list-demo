package com.example.hoon.educastcourselistdemo.scenes.main;

import com.example.hoon.educastcourselistdemo.models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoon on 2016. 8. 27..
 */
interface MainPresenterInput {
    void presentCourses(MainModel.CourseList.Response response);
    void presentError(MainModel.CourseList.ErrorResponse response);
}

interface MainPresenterOutput {
    void displayCourses(MainModel.CourseList.SuccessViewModel successViewModel);
}

public class MainPresenter implements MainPresenterInput {
    private MainPresenterOutput mOutput;
    public void setOutput(MainPresenterOutput output) {
        mOutput = output;
    }

    @Override
    public void presentCourses(MainModel.CourseList.Response response) {
        // check null
        if(response.result != null){
            List<MainModel.CourseList.SuccessViewModel.DisplayedCourse> courses = new ArrayList<>();
            for(Course course : response.result.getResults()){
                MainModel.CourseList.SuccessViewModel.DisplayedCourse displayedCourse = new MainModel.CourseList.SuccessViewModel.DisplayedCourse();
                displayedCourse.pk = course.pk;
                displayedCourse.thumbnail = course.image.medium;
                displayedCourse.name = course.name;
                displayedCourse.channelName = course.channel.name;
                courses.add(displayedCourse);
            }

            MainModel.CourseList.SuccessViewModel successViewModel = new MainModel.CourseList.SuccessViewModel();
            successViewModel.displayedCourses = courses;
            mOutput.displayCourses(successViewModel);
        }
    }

    @Override
    public void presentError(MainModel.CourseList.ErrorResponse response) {

    }
}
