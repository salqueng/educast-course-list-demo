package com.example.hoon.educastcourselistdemo;

/**
 * Created by hoon on 2016. 8. 28..
 */
public class Application extends android.app.Application {
    private static Application app;

    public static Application getApplication() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
