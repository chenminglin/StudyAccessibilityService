package com.bethena.studyaccessibilityservice.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

    public String appName;
    public String packageName;
    public Drawable appIcon;
    public boolean isChecked;


    @Override
    public String toString() {
        return "ProcessInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appIcon=" + appIcon +
                '}';
    }
}
