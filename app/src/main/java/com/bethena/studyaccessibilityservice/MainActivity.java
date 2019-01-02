package com.bethena.studyaccessibilityservice;

import android.app.ActivityManager;
import android.app.Application;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;
import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
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
                    String toastString = getResources().getString(R.string.accessibility_to_open_permission,appname);
                    Toast.makeText(getApplicationContext(),toastString,Toast.LENGTH_LONG).show();
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

                        startNextAppSetting(true);
                    }

                }
            }
        });


        EventBus.getDefault().register(this);
        initData();
        initReceiver();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserTrajectory trajectory) {
        trajectories.add(trajectory);
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


                int i = 0;
                for (PackageInfo packageInfo : packageInfos) {
                    String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    if ("搜狗输入法小米版".equals(appName) || "360手机助手".equals(appName) || "清理大师".equals(appName)) {
                        Log.d(TAG, "packageInfo.appName = " + packageInfo.applicationInfo.loadLabel(pm).toString()
                                + "，packageInfo.applicationInfo.flags = " + packageInfo.applicationInfo.flags);
                    }


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


    @Override
    protected void onResume() {
        super.onResume();

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
                intent.putParcelableArrayListExtra(Constants.KEY_PARAM1, trajectories);
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


    WindowManager windowManager;

    TextView textView;

    private void startNextAppSetting(boolean isNewTask) {
        Log.d(TAG, "startNextAppSetting   mAppPkgs.size()" + mAppPkgs.size());
        if (mAppPkgs.size() > 0) {
            ProcessTransInfo transInfo = mAppPkgs.get(0);

            Intent intentService = new Intent(MainActivity.this, CleanProcessService.class);
            intentService.putExtra(KEY_PARAM1, mAppPkgs.get(0));
            intentService.putExtra(Constants.KEY_PARAM2, true);
            startService(intentService);

            Intent intentSetting = new Intent();
            intentSetting.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", transInfo.packageName, null);
            intentSetting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                    | Intent.FLAG_RECEIVER_REPLACE_PENDING);

            intentSetting.setData(uri);
            startActivity(intentSetting);

//            Intent intentActivity = new Intent(this, CleaningProcessActivity.class);
//            intentActivity.putExtra(Constants.KEY_PARAM1, transInfo);
//            startActivity(intentActivity);

//            if (windowManager == null) {
//                windowManager = (WindowManager) getApplicationContext().getSystemService(Application.WINDOW_SERVICE);
//            }
//
//            if (textView == null) {
//                textView = new TextView(getApplicationContext());
//
//                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//
//                layoutParams.type = 2002;
//                layoutParams.format = 1;
//                layoutParams.flags = 56;
//                layoutParams.width = -2;
//                layoutParams.height = -2;
//
//                textView.setText("djfkasdjfakdjfkajflasdjldjf");
//
//                windowManager.addView(textView,layoutParams);
//            }




        } else {

            Intent intentService = new Intent(MainActivity.this, CleanProcessService.class);
            intentService.putExtra(Constants.KEY_PARAM2, false);
            startService(intentService);

            SharedPreferencesUtil.putBoolean(Constants.KEY_IS_START_CLEAN, false);
            initData();
        }
    }



    public class AccessibilityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("AccessibilityReceiver", "-------------" + intent.getAction());
            switch (intent.getAction()) {
                case Constants.ACTION_RECEIVER_ACC_FINISH:
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ONE:
                    String packageName = intent.getStringExtra(Constants.KEY_PARAM1);
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
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_ERROR:
                    Toast.makeText(context, R.string.accessibility_error, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_BUTTON_NOT_FOUND:
                    Toast.makeText(context, R.string.accessibility_button_not_fount, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_VIEW_NOT_FOUND:
                    Toast.makeText(context, R.string.accessibility_view_not_fount, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_INTERCEPTER:
                    Toast.makeText(context, R.string.accessibility_intercepter, Toast.LENGTH_LONG).show();
                    break;
                case Constants.ACTION_RECEIVER_ACC_CLEAN_NEXT_IF_HAVE:
                    startNextAppSetting(true);
                    break;
                case Constants.ACTION_RECEIVER_ACC_RECORD_ACTIVITY:
                    UserTrajectory userTrajectory = intent.getParcelableExtra(Constants.KEY_PARAM1);
                    trajectories.add(userTrajectory);
                    break;
                case Constants.ACTION_RECEIVER_ACC_PROCESS_HAVE_FINISH:
                    startNextAppSetting(true);
                    break;
            }

        }
    }
}
