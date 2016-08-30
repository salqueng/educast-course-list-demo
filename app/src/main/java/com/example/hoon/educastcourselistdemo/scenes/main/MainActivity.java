package com.example.hoon.educastcourselistdemo.scenes.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.hoon.educastcourselistdemo.R;

interface MainActivityInput {
    void displayCourses(final MainModel.CourseList.SuccessViewModel successViewModel);
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

    private enum STATE {
        IDLE, FETCHING, FETCHED
    }
    private String STATE_KEY = "STATE_KEY";
    private STATE mCurrentState;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            MainConfigurator.getInstance().configure(this);
        } else {
            MainConfigurator.getInstance().reconfigure(this, savedInstanceState);
        }
        setContentView(R.layout.activity_main);
        bindViews();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MainRecyclerViewAdapter(this));
        if(savedInstanceState == null) {
            // first init
            updateState(STATE.IDLE);
        } else {
            updateState(STATE.valueOf(savedInstanceState.getString(STATE_KEY)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_fetch:
                ((MainRecyclerViewAdapter) mRecyclerView.getAdapter()).clear();
                updateState(STATE.FETCHING);
                mOutput.fetchCourses(new MainModel.CourseList.Request(30, 1));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        MainConfigurator.getInstance().saveConfiguration(this, outState);
        outState.putString(STATE_KEY, mCurrentState.name());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void displayCourses(final MainModel.CourseList.SuccessViewModel successViewModel) {
        updateState(STATE.FETCHED);
        ((MainRecyclerViewAdapter) mRecyclerView.getAdapter()).addAll(successViewModel.displayedCourses);
    }

    private void bindViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void updateState(STATE state){
        mCurrentState = state;
        if(mProgressBar != null){
            switch (mCurrentState) {
                case IDLE:
                case FETCHED:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case FETCHING:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
