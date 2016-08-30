package com.example.hoon.educastcourselistdemo.scenes.main;

/**
 * Created by hoon on 2016. 8. 27..
 */
interface MainInteractorInput {
    void fetchCourses(final MainModel.CourseList.Request request);
}
interface MainInteractorOutput {
    void presentCourses(final MainModel.CourseList.Response response);
}

public class MainInteractor implements MainInteractorInput {

    private MainInteractorOutput mOutput;

    public void setOutput(MainInteractorOutput output) {
        mOutput = output;
    }

    public MainInteractorOutput getOutput() {
        return mOutput;
    }

    @Override
    public void fetchCourses(final MainModel.CourseList.Request request) {

    }
}
