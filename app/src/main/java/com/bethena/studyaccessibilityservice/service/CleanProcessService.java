package com.bethena.studyaccessibilityservice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.bethena.studyaccessibilityservice.Constants;
import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.bethena.studyaccessibilityservice.utils.AppUtil;
import com.bethena.studyaccessibilityservice.utils.CleanFloatPermissionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class CleanProcessService extends BaseAccessibilityService {

    final String TAG = getClass().getSimpleName();

    ArrayList<String> appSettingPkgNames = new ArrayList<>();

    ArrayList<String> appSettingViews = new ArrayList<>();

    ArrayList<String> stopButtonTexts = new ArrayList<>();

    ArrayList<String> stopButtonIds = new ArrayList<>();

    ArrayList<String> dialogViews = new ArrayList<>();

    ArrayList<String> dialogOkButtonTexts = new ArrayList<>();

    ArrayList<ProcessTransInfo> mAppPkgs;

    ProcessTransInfo mCurrentAppPkg;

    boolean isStartClean;

    ToAccessibilityBroadcastReceiver mReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        initTexts();
        initReceiver();

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        if (nodeInfo != null) {
            Log.d(TAG, "onCreate nodeInfo = " + nodeInfo.getClassName());

        }

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected ..... ");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        if (nodeInfo != null) {
            Log.d(TAG, "onServiceConnected nodeInfo = " + nodeInfo.getClassName());
            //服务开启了，走这里返回
            performBackClick();

        }
    }

    private void initTexts() {
        appSettingPkgNames.add("com.android.settings");
        appSettingPkgNames.add("com.miui.securitycenter");

        appSettingViews.add("com.android.settings.applications.InstalledAppDetailsTop");
        appSettingViews.add("com.miui.appmanager.ApplicationsDetailsActivity");

        stopButtonTexts.add("强行停止");
        stopButtonTexts.add("强制停止");
        stopButtonTexts.add("结束运行");
        stopButtonTexts.add("结束运行");
        stopButtonTexts.add("结束运行");
        stopButtonTexts.add("结束运行");
        stopButtonTexts.add("结束运行");

        stopButtonIds.add("com.android.settings:id/force_stop_button");
        stopButtonIds.add("android:id/button1");

        dialogViews.add("android.app.AlertDialog");
        dialogViews.add("miui.app.AlertDialog");

        dialogOkButtonTexts.add("确定");
        dialogOkButtonTexts.add("强制停止");
        dialogOkButtonTexts.add("强行停止");


    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        mReceiver = new ToAccessibilityBroadcastReceiver();
        filter.addAction(Constants.ACTION_TO_ACC_DOIT);

        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand startId = " + startId);


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");


        AccessibilityNodeInfo source = event.getSource();


        if (source != null) {
//            Log.d(TAG, "source.getClassName().toString()----" + source.getClassName().toString());
            Log.d(TAG, "event.getPackageName----" + event.getPackageName());
            Log.d(TAG, "event.getClassName----" + event.getClassName());
            Log.d(TAG, "isStartClean----" + isStartClean);
        } else {
            Log.d(TAG, "source null ----");
        }


        if (isStartClean) {
            UserTrajectory trajectory = new UserTrajectory(event.getPackageName().toString(), event.getClass().toString());
            EventBus.getDefault().post(trajectory);

            Intent recordIntent = new Intent(Constants.ACTION_RECEIVER_ACC_RECORD_ACTIVITY);
            recordIntent.putExtra(Constants.KEY_PARAM1, trajectory);
            sendBroadcast(recordIntent);
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    && appSettingPkgNames.contains(event.getPackageName())) {

                CharSequence className = event.getClassName();

                if (className.equals("android.widget.FrameLayout")) {
                    Log.e(TAG, "这个不知道怎么来的。。。。");

                    return;
                }

                if (appSettingViews.contains(className)) {
                    AccessibilityNodeInfo info = null;

                    for (String id : stopButtonIds) {
                        AccessibilityNodeInfo inf = findViewByID(id);
                        if (inf != null) {
                            Log.e(TAG, "根据 id 找到 按钮 " + inf.getClassName());
                        }
                    }

                    for (String text : stopButtonTexts) {
                        info = findViewByText(text);
                        if (info != null) {
                            break;
                        }
                    }
                    if (info != null) {

                        if (info.isEnabled()) {
                            performViewClick(info);
                        } else {//进程已经结束
                            performBackClick();
                            sendBroadcast(new Intent(Constants.ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH));

                        }
                    } else {
                        Log.e(TAG, "找不到 '停止' 按钮");
                        performBackClick();
//                        performBackClick();

                        sendBroadcast(new Intent(Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND));
                        isStartClean = false;
                    }
                } else if (dialogViews.contains(className)) {
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
//                        startNextAppSetting(false);
                        Intent intent = new Intent(Constants.ACTION_RECEIVER_ACC_CLEAN_ONE);
                        String packageName = mCurrentAppPkg.packageName;
                        intent.putExtra(Constants.KEY_PARAM1, packageName);
                        sendBroadcast(intent);
                    } else {
                        performBackClick();
                        performBackClick();
//                        startNextAppSetting(false);
                        Log.e(TAG, "找不到 弹出窗的 '停止' 按钮");
                        sendBroadcast(new Intent(Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND));
                        isStartClean = false;
                    }

                } else {
                    performBackClick();
                    isStartClean = false;
                    Log.d(TAG, "应用详情界面找不到");
                    sendBroadcast(new Intent(Constants.ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND));
                }
            } else if (event.getPackageName().equals(getPackageName())) {
                Log.w(TAG, "出现了本应用页面");
                sendBroadcast(new Intent(Constants.ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE));
            } else {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER);
                sendBroadcast(intent);
                isStartClean = false;
                Log.w(TAG, "强行停止 任务中断");
            }
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
    }


    class ToAccessibilityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive ---- intent action = " + intent.getAction());

            switch (intent.getAction()) {
                case Constants.ACTION_TO_ACC_DOIT:
                    mCurrentAppPkg = intent.getParcelableExtra(Constants.KEY_PARAM1);
                    Log.d(TAG, "onStartCommand mCurrentAppPkg = " + mCurrentAppPkg);
                    isStartClean = intent.getBooleanExtra(Constants.KEY_PARAM2, false);

                    break;
            }


        }
    }
}
