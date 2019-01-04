package com.bethena.studyaccessibilityservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;
import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.bethena.studyaccessibilityservice.permission.FloatWindowManager;
import com.bethena.studyaccessibilityservice.service.CleanProcessMainService;
import com.bethena.studyaccessibilityservice.service.CleanProcessService;
import com.bethena.studyaccessibilityservice.utils.AppUtil;
import com.bethena.studyaccessibilityservice.utils.SharedPreferencesUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.bethena.studyaccessibilityservice.Constants.KEY_PARAM1;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String TAG = getClass().getSimpleName();

    PackageManager pm;

    RecyclerView mListView;

    SwipeRefreshLayout mRefreshLayout;
    AppAdapter mAdapter;

    List<ProcessInfo> mDatas = new ArrayList<>();

    ArrayList<ProcessTransInfo> mAppPkgs = new ArrayList<>();

    ArrayList<String> ignoreAppPackage = new ArrayList<>();

    ArrayList<UserTrajectory> trajectories = new ArrayList<>();

    AccessibilityBroadcastReceiver mReceiver;


    void initIgnore() {
        ignoreAppPackage.add("com.oasisfeng.greenify");
        ignoreAppPackage.add("me.piebridge.brevent");
        ignoreAppPackage.add("com.tencent.mm");
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

        mRefreshLayout.setOnRefreshListener(this);
        mListView = findViewById(R.id.recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
    }

    private void initData() {
        mRefreshLayout.setRefreshing(true);
        SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ProcessInfo> datasTemp = new ArrayList<>();
                PackageManager pm = getPackageManager();
//        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES & PackageManager.GET_META_DATA & 0x00200000);
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

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
                for (PackageInfo packageInfo : packageInfos) {
                    String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
//                    if ("搜狗输入法小米版".equals(appName) || "360手机助手".equals(appName) || "清理大师".equals(appName)) {
//                        Log.d(TAG, "packageInfo.appName = " + packageInfo.applicationInfo.loadLabel(pm).toString()
//                                + "，packageInfo.applicationInfo.flags = " + packageInfo.applicationInfo.flags);
//                    }


                    if (((packageInfo.applicationInfo.flags & PackageManager.GET_ACTIVITIES) == 0)
                            && ((packageInfo.applicationInfo.flags & PackageManager.GET_META_DATA) == 0)
                            && ((packageInfo.applicationInfo.flags & 0x00200000) == 0)) {


//                        Log.d(TAG, "packageInfo.packageName = " + packageInfo.packageName);
//                        Log.d(TAG, "applicationInfo.name = " + packageInfo.applicationInfo.loadLabel(pm));

                        ProcessInfo info = new ProcessInfo();
                        info.packageInfo = packageInfo;
                        info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();


                        info.packageName = packageInfo.packageName;
                        if (ignoreAppPackage.contains(info.packageName)) {
                            info.isChecked = false;
                        } else {
                            info.isChecked = true;
                        }

                        info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                        datasTemp.add(info);

                        i++;
                    }
                }

                mDatas.clear();
                mDatas.addAll(datasTemp);
                Log.d(TAG, "i = " + i);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(false);
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        }).start();


    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

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


    public void clickButtonFloat(View view) {
//        CleanFloatPermissionUtil.jump2System(this, AppUtil.getPhoneModel());
//        requestSettingCanDrawOverlays();

        boolean checkPermission = FloatWindowManager.getInstance().checkPermission(this);

        if (!checkPermission) {
            FloatWindowManager.getInstance().applyPermission(this);
        } else {

        }
    }

    private void requestSettingCanDrawOverlays() {

    }
}
