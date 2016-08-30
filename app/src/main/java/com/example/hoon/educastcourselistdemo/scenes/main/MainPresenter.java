package com.example.hoon.educastcourselistdemo.scenes.main;

/**
 * Created by hoon on 2016. 8. 27..
 */
interface MainPresenterInput {
    void presentCourses(MainModel.CourseList.Response response);
}

interface MainPresenterOutput {
    void displayCourses(MainModel.CourseList.ViewModel viewModel);
}

public class MainPresenter implements MainPresenterInput {
    private MainPresenterOutput mOutput;
    public void setOutput(MainPresenterOutput output) {
        mOutput = output;
    }

    @Override
    public void presentCourses(MainModel.CourseList.Response response) {
    }
}
