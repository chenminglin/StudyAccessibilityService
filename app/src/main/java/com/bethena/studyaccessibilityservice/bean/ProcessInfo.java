package com.bethena.studyaccessibilityservice.bean;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ProcessInfo implements Serializable {

    public String appName;
    public String packageName;
    public Drawable appIcon;
    public boolean isChecked;

    public long size;

    public PackageInfo packageInfo;


    @Override
    public String toString() {
        return "ProcessInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appIcon=" + appIcon +
                '}';
    }
}
