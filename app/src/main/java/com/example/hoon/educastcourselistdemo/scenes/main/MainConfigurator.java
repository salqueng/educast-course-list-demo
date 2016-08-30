package com.example.hoon.educastcourselistdemo.scenes.main;

import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by hoon on 2016. 8. 27..
 */

class RMainInteractor extends MainInteractor implements MainActivityOutput {
}

class RMainPresenter extends MainPresenter implements MainInteractorOutput {
}

class RMainActivity implements MainPresenterOutput {
    MainActivity mActivity;
    RMainActivity(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void displayCourses(MainModel.CourseList.ViewModel viewModel) {
        mActivity.displayCourses(viewModel);
    }
}

public class MainConfigurator {
    private static final String CONFIGURATION_RESTORE_KEY = "CONFIGURATION_RESTORE";
    private static MainConfigurator sInstance;
    private static HashMap<String, WeakReference<RMainInteractor>> sInteractorMap = new HashMap<>();

    protected MainConfigurator() {
    }

    public static MainConfigurator getInstance() {
        if (sInstance == null) {
            sInstance = new MainConfigurator();
        }
        return sInstance;
    }

    public void configure(MainActivity activity) {
        // TODO: Connect
        RMainActivity rActivity = new RMainActivity(activity);
        RMainPresenter presenter = new RMainPresenter();
        RMainInteractor interactor = new RMainInteractor();

        activity.setOutput(interactor);
        presenter.setOutput(rActivity);
        interactor.setOutput(presenter);
    }

    public void reconfigure(MainActivity activity, Bundle inState) {
        String key = inState.getString(CONFIGURATION_RESTORE_KEY);

        if (sInteractorMap.containsKey(key)) {
            RMainInteractor interactor = sInteractorMap.remove(key).get();
            if (interactor != null) {
                RMainActivity rActivity = new RMainActivity(activity);
                RMainPresenter presenter = (RMainPresenter) interactor.getOutput();
                activity.setOutput(interactor);
                presenter.setOutput(rActivity);
                return;
            }
        }

        configure(activity);
    }

    public void saveConfiguration(MainActivity activity, Bundle outState) {
        RMainInteractor interactor = (RMainInteractor) activity.getOutput();
        String key = UUID.randomUUID().toString();
        sInteractorMap.put(key, new WeakReference<>(interactor));
        outState.putString(CONFIGURATION_RESTORE_KEY, key);
    }
}
