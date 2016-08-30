package com.example.hoon.educastcourselistdemo.scenes.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hoon.educastcourselistdemo.R;

interface MainActivityInput {
    void displayCourses(final MainModel.CourseList.ViewModel viewModel);
}
interface MainActivityOutput {
    void fetchCourses(final MainModel.CourseList.Request request);
}

public class MainActivity extends AppCompatActivity implements MainActivityInput {
    MainActivityOutput mOutput;
    public void setOutput(MainActivityOutput output) {
        mOutput = output;
    }
    public MainActivityOutput getOutput() {
        return mOutput;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            MainConfigurator.getInstance().configure(this);
        } else {
            MainConfigurator.getInstance().reconfigure(this, savedInstanceState);
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        MainConfigurator.getInstance().saveConfiguration(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void displayCourses(final MainModel.CourseList.ViewModel viewModel) {

    }
}
