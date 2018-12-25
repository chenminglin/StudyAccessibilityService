package com.bethena.studyaccessibilityservice.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.bethena.studyaccessibilityservice.Constants;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.bethena.studyaccessibilityservice.service.BaseAccessibilityService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CleanProcessService extends BaseAccessibilityService {

    final String TAG = getClass().getSimpleName();

    ArrayList<String> appSettingPkgNames = new ArrayList<>();

    ArrayList<String> appSettingViews = new ArrayList<>();

    ArrayList<String> stopButtonTexts = new ArrayList<>();

    ArrayList<String> dialogViews = new ArrayList<>();

    ArrayList<String> dialogOkButtonTexts = new ArrayList<>();


    ArrayList<String> mAppPkgs;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        initTexts();
    }

    private void initTexts() {
        appSettingPkgNames.add("com.android.settings");

        appSettingViews.add("com.android.settings.applications.InstalledAppDetailsTop");

        stopButtonTexts.add("强行停止");
        stopButtonTexts.add("强制停止");

        dialogViews.add("android.app.AlertDialog");

        dialogOkButtonTexts.add("确定");
        dialogOkButtonTexts.add("强制停止");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand startId = " + startId);
        mAppPkgs = intent.getStringArrayListExtra(Constants.KEY_PARAM1);
        for (String pkg : mAppPkgs) {
            Log.d(TAG, "onStartCommand pkg = " + pkg);
        }

        startNextAppSetting();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startNextAppSetting() {
        if (mAppPkgs.size() > 0) {
            String pkgName = mAppPkgs.get(0);
            Intent intentSetting = new Intent();
            intentSetting.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", pkgName, null);
            intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentSetting.setData(uri);
            startActivity(intentSetting);

            mAppPkgs.remove(0);
        }
    }


    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");


        Log.d(TAG, "event.getPackageName----" + event.getPackageName());
        Log.d(TAG, "event.getClassName----" + event.getClassName());

        UserTrajectory trajectory = new UserTrajectory(event.getPackageName().toString(), event.getClass().toString());
        EventBus.getDefault().post(trajectory);

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && appSettingPkgNames.contains(event.getPackageName())) {

            CharSequence className = event.getClassName();

            if (appSettingViews.contains(className)) {
                AccessibilityNodeInfo info = null;
                for (String text : stopButtonTexts) {
                    info = findViewByText(text, true);
                    if (info != null) {
                        break;
                    }
                }
                if (info != null) {
                    if (info.isEnabled()) {
                        performViewClick(info);
                    } else {
                        performBackClick();
                    }
                }
            }
            if (dialogViews.contains(className)) {
                AccessibilityNodeInfo info = null;
                for (String text : dialogOkButtonTexts) {
                    info = findViewByText(text, true);
                    if (info != null) {
                        break;
                    }
                }
                if (info != null) {
                    performViewClick(info);
                    performBackClick();
                    startNextAppSetting();
                } else {
                    performBackClick();
                    performBackClick();
                    startNextAppSetting();
                }

            }
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
