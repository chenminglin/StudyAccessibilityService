package com.bethena.studyaccessibilityservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;
import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.bethena.studyaccessibilityservice.permission.FloatWindowManager;
import com.bethena.studyaccessibilityservice.permission.VivoFloatPermissionStatus;
import com.bethena.studyaccessibilityservice.permission.autostart.AutoStartPermissionUtils;
import com.bethena.studyaccessibilityservice.service.CleanProcessService;
import com.bethena.studyaccessibilityservice.service.MyIntentService;
import com.bethena.studyaccessibilityservice.utils.AppUtil;
import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.bethena.studyaccessibilityservice.Constants.KEY_PARAM1;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String TAG = getClass().getSimpleName();
    final int EVER_TREATH_HANDLE_APP_NUM = 40;
    PackageManager pm;
    RecyclerView mListView;
    SwipeRefreshLayout mRefreshLayout;
    AppAdapter mAdapter;
    List<ProcessInfo> mDatas = new ArrayList<>();
    ArrayList<ProcessTransInfo> mAppPkgs = new ArrayList<>();
    ArrayList<String> ignoreAppPackage = new ArrayList<>();
    ArrayList<UserTrajectory> trajectories = new ArrayList<>();
    AccessibilityBroadcastReceiver mReceiver;
    SeekBar seekBar;
    MainBroadcastReceiver mMainReceiver;
    int threadCount = 0;
    volatile int threadFinishCount = 0;

    void initIgnore() {
//        ignoreAppPackage.add("com.oasisfeng.greenify");
//        ignoreAppPackage.add("me.piebridge.brevent");
//        ignoreAppPackage.add("com.tencent.mm");
        String desktopPackage = AppUtil.getLauncherPackageName(MainActivity.this);
        String myPackageName = getPackageName();
        ignoreAppPackage.add(desktopPackage);
        ignoreAppPackage.add(myPackageName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIgnore();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);

        mRefreshLayout = findViewById(R.id.refresh_layout);
//        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
//        for (PackageInfo pack : packageInfos) {
//            ProviderInfo[] providers = pack.providers;
//            Log.w(TAG, "pack.packageName =  " + pack.packageName);
////            if ("com.vivo.browser".equals(pack.packageName)) {
//            if (providers != null) {
//                for (ProviderInfo provider : providers) {
//                    Log.w("Example", "provider: " + provider.authority);
//
//
//                }
//            }
////            }
//
//        }

        mRefreshLayout.setOnRefreshListener(this);
        mListView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mListView.setLayoutManager(layoutManager);
        mAdapter = new AppAdapter(mDatas);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                switch (view.getId()) {
                    case R.id.item_root:
                        mDatas.get(position).isChecked = !mDatas.get(position).isChecked;
                        mListView.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                        break;
                }
            }
        });

        mListView.setAdapter(mAdapter);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isOpen = AppUtil.isAccessibilitySettingsOn(getApplicationContext(), CleanProcessService.class);

                if (!isOpen) {

                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    int flag = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | 0x00800000;
                    Log.d(TAG, "flag = " + flag);
                    intent.setFlags(flag);
                    startActivity(intent);
                    String appname = getResources().getString(R.string.app_name);
                    String toastString = getResources().getString(R.string.accessibility_to_open_permission, appname);
                    Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();


                } else {
                    mAppPkgs.clear();

                    for (ProcessInfo info : mDatas) {
                        if (info.isChecked) {
                            ProcessTransInfo transInfo = new ProcessTransInfo();
                            transInfo.packageInfo = info.packageInfo;
                            transInfo.packageName = info.packageName;
                            mAppPkgs.add(transInfo);
                        }

                    }

                    if (mAppPkgs.size() > 0) {
                        SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, true);
//                        Intent intent = new Intent(MainActivity.this, CleanProcessMainService.class);
                        Intent intent = new Intent(MainActivity.this, CleaningProcessActivity.class);
                        intent.putExtra(Constants.KEY_PARAM1, mAppPkgs);
//                        startService(intent);
                        startActivity(intent);
                    }

                }
            }
        });

//        final TextView tvCount = findViewById(R.id.clean_count);
//
//        seekBar = findViewById(R.id.seekbar);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tvCount.setText("选择个数：" + progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });


        EventBus.getDefault().register(this);
        pm = getPackageManager();
        initData();
        initReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserTrajectory trajectory) {
        trajectories.add(trajectory);
    }

    private void initReceiver() {
        mReceiver = new AccessibilityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_RECEIVER_ACC_RECORD_ACTIVITY);
        intentFilter.addAction(Constants.ACTION_RECEIVER_ACC_FINISH);
        registerReceiver(mReceiver, intentFilter);


        mMainReceiver = new MainBroadcastReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter.addAction("vivo.intent.action.CHECK_ALERT_WINDOW");
        registerReceiver(mMainReceiver, intentFilter1);
    }

    private void initData() {
        mRefreshLayout.setRefreshing(true);

        threadCount = 0;
        threadFinishCount = 0;
        SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ProcessInfo> datasTemp = new ArrayList<>();

//        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES & PackageManager.GET_META_DATA & 0x00200000);
                final List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

//                List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
//
//                for (ApplicationInfo applicationInfo : apps) {
//                    if (((applicationInfo.flags & PackageManager.GET_ACTIVITIES) == 0)
//                            && ((applicationInfo.flags & PackageManager.GET_META_DATA) == 0)
//                            && ((applicationInfo.flags & 0x00200000) == 0)) {
//                        String label = applicationInfo.loadLabel(pm).toString();
//
//                        Log.d(TAG, "label = " + label);
//                    }
//                }

                int i = 0;

                if (packageInfos.size() % EVER_TREATH_HANDLE_APP_NUM >= 1) {
                    threadCount = packageInfos.size() / EVER_TREATH_HANDLE_APP_NUM + 1;
                } else {
                    threadCount = packageInfos.size() / EVER_TREATH_HANDLE_APP_NUM;
                }
                Log.d(TAG, "threadCount = " + threadCount);

                for (int t = 0; t <= threadCount; t++) {
//                    Log.d(TAG, "t = " + t);
                    final int startN = t;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            Log.d(TAG, "startN = " + startN);

                            int m = EVER_TREATH_HANDLE_APP_NUM * startN;
                            int tempM = m;

                            for (; m < tempM + EVER_TREATH_HANDLE_APP_NUM; m++) {
//                                Log.d(TAG, "m = " + m);
                                if (m >= packageInfos.size()) {
//                                    Log.d(TAG, "m more than size " + m);
                                    continue;
                                }
                                PackageInfo packageInfo = packageInfos.get(m);

                                if (packageInfo == null) {
                                    Log.d(TAG, "package info null");
//                                    Log.d(TAG, "package info null");
                                    continue;
                                }

                                if (((packageInfo.applicationInfo.flags & PackageManager.GET_ACTIVITIES) == 0)
                                        && ((packageInfo.applicationInfo.flags & PackageManager.GET_META_DATA) == 0)
                                        && ((packageInfo.applicationInfo.flags & 0x00200000) == 0)) {

                                    if (ignoreAppPackage.contains(packageInfo.packageName)) {
                                        continue;
                                    }


                                    final ProcessInfo info = new ProcessInfo();
                                    info.packageInfo = packageInfo;
                                    info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();


                                    info.packageName = packageInfo.packageName;
                                    info.size = new File(packageInfo.applicationInfo.publicSourceDir).length();
                                    Log.d(TAG, "info.appName = " + info.appName + ", info.size = " + info.size);
//                                    if (ignoreAppPackage.contains(info.packageName)) {
//                                        info.isChecked = false;
//                                    } else {
                                    info.isChecked = true;
//                                    }

                                    info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                                    datasTemp.add(info);
                                }
                            }
                            threadFinishCount++;
                            Log.d(TAG, "threadFinishCount = " + threadFinishCount);
                            if (threadFinishCount >= threadCount) {
                                synchronized (datasTemp) {
                                    Collections.sort(datasTemp, new Comparator<ProcessInfo>() {
                                        @Override
                                        public int compare(ProcessInfo o1, ProcessInfo o2) {
                                            if (o1 == null) {
                                                Log.e(TAG,"sort error 1");
                                                return 1;
                                            }
                                            if (o2 == null) {
                                                Log.e(TAG,"sort error 2");
                                                return -1;
                                            }
                                            if (o1.size > o2.size) {
                                                return -1;
                                            } else if (o1.size < o2.size) {
                                                return 1;
                                            }
                                            return 0;
                                        }
                                    });


                                    mDatas.clear();
                                    int endIndex = 0;
//                                if (seekBar.getProgress() > datasTemp.size()) {
//                                    endIndex = datasTemp.size();
//                                } else {
//                                    endIndex = seekBar.getProgress();
//                                }
                                    mDatas.addAll(datasTemp);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRefreshLayout.setRefreshing(false);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }).start();
                }

                for (PackageInfo packageInfo : packageInfos) {
//                    String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
//                    if ("搜狗输入法小米版".equals(appName) || "360手机助手".equals(appName) || "清理大师".equals(appName)) {
//                        Log.d(TAG, "packageInfo.appName = " + packageInfo.applicationInfo.loadLabel(pm).toString()
//                                + "，packageInfo.applicationInfo.flags = " + packageInfo.applicationInfo.flags);
//                    }


                }


            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_to_user_trajectory:
                Intent intent = new Intent(MainActivity.this, UserTrajectoryActivity.class);
                intent.putParcelableArrayListExtra(KEY_PARAM1, trajectories);
                startActivity(intent);
                break;
            case R.id.action_all:
                for (ProcessInfo info : mDatas) {
                    info.isChecked = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.action_none:
                for (ProcessInfo info : mDatas) {
                    info.isChecked = false;
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ProcessInfo getAppInfo(ApplicationInfo app, int pid, String processName) {
        ProcessInfo appInfo = new ProcessInfo();
        appInfo.appName = ((String) app.loadLabel(pm));
        appInfo.appIcon = app.loadIcon(pm);
        appInfo.packageName = (app.packageName);

//        appInfo.setPid(pid);
//        appInfo.setProcessName(processName);

        return appInfo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mReceiver);

        Log.d(TAG, "onDestroy");

        unregisterReceiver(mMainReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    public void clickButtonFloat(View view) {
//        (new CleanFloatPermissionUtil()).jump2System(this, AppUtil.getAndroidDeviceProduct());
//        requestSettingCanDrawOverlays();

        boolean checkPermission = FloatWindowManager.getInstance().checkPermission(this);

//        if (!checkPermission) {
        FloatWindowManager.getInstance().applyPermission(this);
        Intent intentService = new Intent(MainActivity.this, MyIntentService.class);
        intentService.setAction(MyIntentService.ACTION);
        startService(intentService);
//        } else {
//
//        }


    }

    public void clickButtonSelfReset(View view) {
        if (AutoStartPermissionUtils.isEnablePermissioActivity(this)) {
            AutoStartPermissionUtils.openPermissionActivity(this);
        }
    }

    int n = 0;

    public void mytest(View view) {
//        n = 0;
//        int flag = Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_TASK_ON_HOME;
//        for (; n < mDatas.size(); n++) {
//            startDtailSettingActivity(mDatas.get(n).packageName, 1000 * n, flag);
//        }
//
//
//        startDelayActivity(MainActivity.class, n * 1000, Intent.FLAG_ACTIVITY_CLEAR_TASK );
//        overridePendingTransition(R.anim.no_anim, R.anim.no_anim);

//        startActivity(new Intent(this, WindowActivity.class));
//        startService(new Intent(this, CleaningProcessWindowService.class));
//        AppUtil.disableNavigationBar(this);

//        int status = VivoFloatPermissionStatus.getFloatPermissionStatus(this);
//        if (status == VivoFloatPermissionStatus.MODE_ALLOWED) {
//            Log.d(TAG, "已获取权限");
//        } else {
//            Log.d(TAG, "未获取权限");
//        }

        VivoFloatPermissionStatus.updateFloatPermissionStatus(this, VivoFloatPermissionStatus.MODE_ALLOWED);

//
//        Uri uri2 = Uri.parse("content://com.iqoo.secure.provider.secureprovider/password");
//        Cursor cursor2 = App.getInstance().getContentResolver().query(uri2, null, null, null, null);
//        if (cursor2 != null) {
//            while (cursor2.moveToNext()) {
//                String[] names = cursor2.getColumnNames();
//
//                StringBuffer stringBuffer = new StringBuffer();
//                for (String name : names) {
//                    String value = cursor2.getString(cursor2.getColumnIndex(name));
//                    stringBuffer.append(name + ":" + value + ",");
//
//                }
//                Log.d(TAG, "stringBuffer = " + stringBuffer.toString());
//            }
//        } else {
//            Log.d(TAG, "cursor2 is null");
//        }


//        sendBroadcast(new Intent("vivo.intent.action.CHECK_ALERT_WINDOW"));
//        Intent intent = new Intent(this,MyIntentService.class);
//        intent.setAction(MyIntentService.ACTION);
//        startService(intent);


//        boolean isAbove = AppUtil.isAboveFunTouchOSV2_5();
//        Log.d(TAG, "isAbove = " + isAbove);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(n*2000);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            MyIntentService2.startActionBaz(MainActivity.this,"aaa","bbb");
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


    }

    private void startDtailSettingActivity(final String packageName, final long delayTime, final int flags) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delayTime);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "aaaa packagename = " + packageName);
                            Intent intentSetting = new Intent();
                            intentSetting.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                            Uri uri = Uri.fromParts("package", packageName, null);
                            intentSetting.setFlags(flags);
                            intentSetting.setData(uri);
                            startActivity(intentSetting);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startDelayActivity(final Class clazz, final long delayTime, final int flags) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delayTime);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "aaaa class = " + clazz);
                            Intent intent = new Intent(MainActivity.this, clazz);
                            intent.setFlags(flags);
                            startActivity(intent);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class AThread extends Thread {

    }

    public class AccessibilityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_RECEIVER_ACC_RECORD_ACTIVITY:
                    UserTrajectory userTrajectory = intent.getParcelableExtra(KEY_PARAM1);
                    trajectories.add(userTrajectory);
                    break;
                case Constants.ACTION_RECEIVER_ACC_FINISH:
                    initData();
                    break;
            }


        }
    }

    class MainBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG,"action : "+intent.getAction());

        }
    }
}
