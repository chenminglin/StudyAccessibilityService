package com.bethena.studyaccessibilityservice.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class ProcessInfo implements Serializable {

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
