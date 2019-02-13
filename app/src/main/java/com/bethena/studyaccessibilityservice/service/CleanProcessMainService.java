package com.bethena.studyaccessibilityservice.service;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.Constants;
import com.bethena.studyaccessibilityservice.MainActivity;
import com.bethena.studyaccessibilityservice.R;
import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.permission.FloatWindowManager;
import com.bethena.studyaccessibilityservice.ui.CleaningProcessView;
import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.bethena.studyaccessibilityservice.Constants.KEY_PARAM1;

public class CleanProcessMainService extends Service {

    final String TAG = getClass().getSimpleName();

    AccessibilityBroadcastReceiver mReceiver;

    CleaningProcessView mCleaningWindow;

    ArrayList<ProcessTransInfo> mAppPkgs = new ArrayList<>();

    boolean isServiceStart;

    WindowManager windowManager;
    PackageManager pm;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = getPackageManager();
        initReceiver();

//        FloatWindowManager.getInstance().applyPermission(this);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + "  onStartCommand");
        if (intent.getExtras() != null) {
            mAppPkgs = intent.getParcelableArrayListExtra(Constants.KEY_PARAM1);
            isServiceStart = true;
            startNextAppSetting(true);
        }



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d(TAG, TAG + "  onDestroy");
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        mReceiver = new AccessibilityBroadcastReceiver();
        filter.addAction(Constants.ACTION_RECEIVER_ACC_FINISH);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_ERROR);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_ONE);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_RECORD_ACTIVITY);
        filter.addAction(Constants.ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH);
        registerReceiver(mReceiver, filter);
    }

    private void startNextAppSetting(boolean isNewTask) {
        Log.d(TAG, "startNextAppSetting   mAppPkgs.size()" + mAppPkgs.size());
        Log.d(TAG, "startNextAppSetting   Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//
//            Log.e(TAG, "startNextAppSetting   e = " + e.getMessage());
//        }
        if (!isServiceStart) {
            return;
        }

        if (mAppPkgs.size() > 0) {
            ProcessTransInfo transInfo = mAppPkgs.get(0);


            Intent intentService = new Intent(Constants.ACTION_TO_ACC_DOIT);
            intentService.putExtra(KEY_PARAM1, mAppPkgs.get(0));
            intentService.putExtra(Constants.KEY_PARAM2, true);
            sendBroadcast(intentService);


            Intent intentSetting = new Intent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                intentSetting.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", transInfo.packageName, null);
                intentSetting.setData(uri);

                List<ResolveInfo> resolveInfo =
                        pm.queryIntentActivities(intentSetting,
                                PackageManager.MATCH_DEFAULT_ONLY);

                Log.d(TAG, "startNextAppSetting   resolveInfo.size() = " + resolveInfo.size());

            }


            int flag = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | Intent.FLAG_ACTIVITY_NO_HISTORY;

            int flagLieBao = Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_CLEAR_TASK;

            Log.d(TAG, "startNextAppSetting   flag = " + flag);
            Log.d(TAG, "startNextAppSetting   flagLieBao = " + flagLieBao);

            intentSetting.setFlags(flag);


            startActivity(intentSetting);

//            Intent intentActivity = new Intent(this, CleaningProcessActivity.class);
//            intentActivity.putExtra(Constants.KEY_PARAM1, transInfo);
//            startActivity(intentActivity);


            if (FloatWindowManager.getInstance().checkPermission(this)) {
                if (windowManager == null) {
                    windowManager = (WindowManager) getApplicationContext().getSystemService(Application.WINDOW_SERVICE);
                }

                if (mCleaningWindow == null) {
                    View cleaningLayout = LayoutInflater.from(this).inflate(R.layout.activity_cleaning_process, null);
                    mCleaningWindow = new CleaningProcessView();
                    mCleaningWindow.vRootView = cleaningLayout;
                    mCleaningWindow.tvAppName = cleaningLayout.findViewById(R.id.tv_appname);
                    mCleaningWindow.iVIconView = cleaningLayout.findViewById(R.id.iv_app_icon);
                    mCleaningWindow.btnCancel = cleaningLayout.findViewById(R.id.btn_cancel);
                    mCleaningWindow.mWindowManager = windowManager;

                    mCleaningWindow.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "service cancel ");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            dismissFloatWindow();
                            mCleaningWindow.btnCancel.setClickable(false);
                            isServiceStart = false;
                            sendBroadcast(new Intent(Constants.ACTION_TO_CANCEL_SERVICE));
                        }
                    });

                    Point size = new Point();
                    windowManager.getDefaultDisplay().getSize(size);
                    int screenWidth = size.x;
                    int screenHeight = size.y;

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                    layoutParams.packageName = getPackageName();
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;


                    int type = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                    }

                    layoutParams.type = type;

                    layoutParams.format = PixelFormat.RGBA_8888;
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

                    layoutParams.x = screenWidth;
                    layoutParams.y = screenHeight;

                    mCleaningWindow.layoutParams = layoutParams;
                }

                if (mCleaningWindow != null && !mCleaningWindow.isShowing) {
                    mCleaningWindow.btnCancel.setClickable(true);
                    windowManager.addView(mCleaningWindow.vRootView, mCleaningWindow.layoutParams);
                    mCleaningWindow.isShowing = true;
                }

                Drawable icon = transInfo.packageInfo.applicationInfo.loadIcon(pm);
                mCleaningWindow.iVIconView.setImageDrawable(icon);
                mCleaningWindow.tvAppName.setText(transInfo.packageInfo.applicationInfo.loadLabel(pm));
            }

        } else {

//            Intent intentService = new Intent(MainActivity.this, CleanProcessService.class);
//            intentService.putExtra(Constants.KEY_PARAM2, false);
//            startService(intentService);

            Intent intentService = new Intent(Constants.ACTION_TO_ACC_DOIT);
            intentService.putExtra(Constants.KEY_PARAM2, false);
            sendBroadcast(intentService);

            dismissFloatWindow();
            SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, false);

            Intent intentFinish = new Intent(Constants.ACTION_RECEIVER_ACC_FINISH);
            sendBroadcast(intentFinish);
            isServiceStart = false;

            stopSelf();
        }
    }

    public void dismissFloatWindow() {
        if (mCleaningWindow != null && mCleaningWindow.isShowing) {
            mCleaningWindow.dismissWindow();
        }
    }

    private void removePkgAndStartNext(String packageName) {
        Log.d("AccessibilityReceiver", "-------------packageName = " + packageName);
        int n = 0;
        for (; n < mAppPkgs.size(); ) {
            ProcessTransInfo transInfo1 = mAppPkgs.get(n);

            Log.d("AccessibilityReceiver", "-------------transInfo1 = " + transInfo1);
            if (transInfo1.packageName.equals(packageName)) {
                mAppPkgs.remove(transInfo1);
                break;
            } else {
                n++;
            }
        }
        startNextAppSetting(true);
    }

    public class AccessibilityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AccessibilityReceiver", "-------------" + intent.getAction());
            switch (intent.getAction()) {
                case Constants.ACTION_RECEIVER_ACC_FINISH:
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ONE:
                    String packageName = intent.getStringExtra(KEY_PARAM1);
                    removePkgAndStartNext(packageName);
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ERROR:
                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_error, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND:
                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_button_not_fount, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND:
                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_view_not_fount, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER:
                    dismissFloatWindow();
                    startNextAppSetting(true);
//                    Toast.makeText(context, R.string.accessibility_intercepter, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE:
                    startNextAppSetting(true);
                    break;

                case Constants.ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH:
                    String packageName2 = intent.getStringExtra(KEY_PARAM1);
                    removePkgAndStartNext(packageName2);
                    break;
            }

        }
    }
}
