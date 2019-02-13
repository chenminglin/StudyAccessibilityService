package com.bethena.studyaccessibilityservice;

import android.app.Application;
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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.permission.FloatWindowManager;
import com.bethena.studyaccessibilityservice.service.CleaningProcessWindowService;
import com.bethena.studyaccessibilityservice.ui.CleaningProcessView;
import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.bethena.studyaccessibilityservice.Constants.KEY_PARAM1;

public class CleaningProcessActivity extends AppCompatActivity {


    final String TAG = getClass().getSimpleName();

    CleaningProcessActivity.AccessibilityBroadcastReceiver mReceiver;

    ArrayList<ProcessTransInfo> mAppPkgs = new ArrayList<>();

    boolean isServiceStart;

    PackageManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        pm = getPackageManager();
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        initReceiver();
        if (getIntent().getExtras() != null) {
            mAppPkgs = getIntent().getParcelableArrayListExtra(Constants.KEY_PARAM1);
            isServiceStart = true;
            startNextAppSetting(true);
        }

//        startService(new Intent(this, CleaningProcessWindowService.class));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        mReceiver = new CleaningProcessActivity.AccessibilityBroadcastReceiver();
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

            if (mAppPkgs.size() == 1) {
                transInfo.isLastOne = true;
            }

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


            int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY;

            int flagLieBao = Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_CLEAR_TASK;

            Log.d(TAG, "startNextAppSetting   flag = " + flag);
            Log.d(TAG, "startNextAppSetting   flagLieBao = " + flagLieBao);

            intentSetting.setFlags(flag);
            startActivity(intentSetting);

//            Intent intentActivity = new Intent(this, CleaningProcessActivity.class);
//            intentActivity.putExtra(Constants.KEY_PARAM1, transInfo);
//            startActivity(intentActivity);


        } else {

//            Intent intentService = new Intent(MainActivity.this, CleanProcessService.class);
//            intentService.putExtra(Constants.KEY_PARAM2, false);
//            startService(intentService);

            Intent intentService = new Intent(Constants.ACTION_TO_ACC_DOIT);
            intentService.putExtra(Constants.KEY_PARAM2, false);
            sendBroadcast(intentService);

//            dismissFloatWindow();
            SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, false);

            Intent intentFinish = new Intent(Constants.ACTION_RECEIVER_ACC_FINISH);
            sendBroadcast(intentFinish);
            isServiceStart = false;


            Intent intent = new Intent(CleaningProcessActivity.this, Main2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
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
//                    finish();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ONE:
                    String packageName = intent.getStringExtra(KEY_PARAM1);
                    removePkgAndStartNext(packageName);
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ERROR:
//                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_error, Toast.LENGTH_LONG).show();
//                    finish();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND:
//                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_button_not_fount, Toast.LENGTH_LONG).show();
//                    finish();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND:
//                    dismissFloatWindow();
                    Toast.makeText(context, R.string.accessibility_view_not_fount, Toast.LENGTH_LONG).show();
//                    finish();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER:
//                    dismissFloatWindow();
                    startNextAppSetting(true);
//                    finish();
//                    Toast.makeText(context, R.string.accessibility_intercepter, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE:
                    startNextAppSetting(true);
                    break;

                case Constants.ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH:
                    String packageName2 = intent.getStringExtra(KEY_PARAM1);
                    removePkgAndStartNext(packageName2);
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_LAST_ONE:
                    startActivity(new Intent(CleaningProcessActivity.this, MainActivity.class));
                    break;
            }

        }
    }


}
