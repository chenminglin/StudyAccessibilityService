package com.bethena.studyaccessibilityservice;

import android.app.Application;
import android.content.SharedPreferences;

import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;
import com.facebook.stetho.Stetho;

public class App extends Application {

    private static App app;


    public static App getInstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        Stetho.initializeWithDefaults(this);

        SharedPreferencesUtil.init(this,"sp_name");
    }
}
