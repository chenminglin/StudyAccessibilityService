package com.bethena.studyaccessibilityservice;

import android.app.Application;
import android.content.SharedPreferences;

import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;
import com.facebook.stetho.Stetho;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        SharedPreferencesUtil.init(this,"sp_name");
    }
}
