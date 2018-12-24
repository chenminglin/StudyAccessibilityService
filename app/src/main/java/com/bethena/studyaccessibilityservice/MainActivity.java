package com.bethena.studyaccessibilityservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    final String TAG = getClass().getSimpleName();

    PackageManager pm;

    RecyclerView mListView;

    SwipeRefreshLayout mRefreshLayout;
    AppAdapter mAdapter;

    List<ProcessInfo> mDatas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(this);
        mListView = findViewById(R.id.recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        mAdapter = new AppAdapter(mDatas);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_root:
                        mDatas.get(position).isChecked = !mDatas.get(position).isChecked;
                        mAdapter.notifyItemChanged(position);
                        break;
                }
            }
        });

        mListView.setAdapter(mAdapter);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initData();

//        pm.getApplicationLabel()

//        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        mActivityManager.getRunningTasks();
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
//
//        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcessInfos){
//            Log.d(TAG, "runningAppProcessInfo = " + runningAppProcessInfo.processName);
//        }
//
//        List<ActivityManager.RunningServiceInfo> runningServiceInfos =  mActivityManager.getRunningServices(Integer.MAX_VALUE);
//        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos){
//            Log.d(TAG, "runningServiceInfo = " + runningServiceInfo.process);
//        }

//        final int PROCESS_STATE_TOP = 2;
//        ActivityManager.RunningAppProcessInfo currentInfo = null;
//        Field field = null;
//        try {
//            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
//        } catch (Exception ignored) {
//        }
//        ActivityManager am = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo app : appList) {
//            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
//                    && app.importanceReasonCode == ActivityManager.RunningAppProcessInfo.REASON_UNKNOWN) {
//                Integer state = null;
//                try {
//                    state = field.getInt(app);
//                } catch (Exception e) {
//                }
//                if (state != null && state == PROCESS_STATE_TOP) {
//                    currentInfo = app;
//                    break;
//                }
//            }
//        }

    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDatas.clear();
                PackageManager pm = getPackageManager();
//        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES & PackageManager.GET_META_DATA & 0x00200000);
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                int i = 0;
                for (PackageInfo packageInfo : packageInfos) {
                    if (((packageInfo.applicationInfo.flags & PackageManager.GET_ACTIVITIES) == 0)
                            && ((packageInfo.applicationInfo.flags & PackageManager.GET_META_DATA) == 0)
                            && ((packageInfo.applicationInfo.flags & 0x00200000) == 0)) {
                        Log.d(TAG, "packageInfo.packageName = " + packageInfo.packageName);
                        Log.d(TAG, "applicationInfo.name = " + packageInfo.applicationInfo.loadLabel(pm));

                        ProcessInfo info = new ProcessInfo();
                        info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                        info.isChecked = true;
                        info.packageName = packageInfo.packageName;
                        info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                        mDatas.add(info);
                        i++;
                    }
                }
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
    public void onRefresh() {
        initData();
    }



    // 查询所有正在运行的应用程序信息： 包括他们所在的进程id和进程名
    // 这儿我直接获取了系统里安装的所有应用程序，然后根据报名pkgname过滤获取所有真正运行的应用程序
    private List<ProcessInfo> queryAllRunningAppInfo() {
        pm = this.getPackageManager();

        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序

        // 保存所有正在运行的包名 以及它所在的进程信息
        Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<>();

        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();


        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            int pid = appProcess.pid; // pid
            String processName = appProcess.processName; // 进程名
            Log.i(TAG, "processName: " + processName + "  pid: " + pid);

            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包

            // 输出所有应用程序的包名
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                Log.i(TAG, "packageName " + pkgName + " at index " + i + " in process " + pid);
                // 加入至map对象里
                pgkProcessAppMap.put(pkgName, appProcess);
            }
        }
        // 保存所有正在运行的应用程序信息
        List<ProcessInfo> runningAppInfos = new ArrayList<ProcessInfo>(); // 保存过滤查到的AppInfo

        for (ApplicationInfo app : listAppcations) {
            // 如果该包名存在 则构造一个RunningAppInfo对象
            if (pgkProcessAppMap.containsKey(app.packageName)) {
                // 获得该packageName的 pid 和 processName
                int pid = pgkProcessAppMap.get(app.packageName).pid;
                String processName = pgkProcessAppMap.get(app.packageName).processName;
                runningAppInfos.add(getAppInfo(app, pid, processName));
            }
        }

        return runningAppInfos;

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

//    class GetRunnigProcessTask extends AsyncTask<Void,>
}
