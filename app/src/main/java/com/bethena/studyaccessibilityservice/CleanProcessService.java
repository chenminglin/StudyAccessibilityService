package com.bethena.studyaccessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.bethena.studyaccessibilityservice.service.BaseAccessibilityService;

public class CleanProcessService extends BaseAccessibilityService {

    final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG,"onInterrupt");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG,"onAccessibilityEvent");


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
