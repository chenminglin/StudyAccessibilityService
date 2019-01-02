package com.bethena.studyaccessibilityservice.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class AppUtil {
    public final static String TAG = AppUtil.class.getSimpleName();

    public static boolean isAccessibilitySettingsOn(Context context, Class clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + clazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    /**
     * 获取正在运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
     */
    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }


    public static long getMemorySizebyPid(final Context context, final int pid) {
        final Debug.MemoryInfo[] processMemoryInfo =
                ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getProcessMemoryInfo(new int[]{pid});

        if (processMemoryInfo != null) {
            if (processMemoryInfo.length > 0) {
                return processMemoryInfo[0].getTotalPss() * 1024;
            }
        }
        return 0;
    }

    /**
     * 启动应用
     *
     * @return
     */
    public static boolean startApk(Context context, String packname) {
//        if (object == null) {
//            return false;
//        }
//        DownloadTaskInfo info = changeInfo(object);
//        if (info == null || info.getPackageName() == null) {
//            return false;
//        }
//        String packname = info.getPackageName();
//        Context context = CleanAppApplication.getInstance();
        PackageManager pm = context.getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packname, 0);
            resolveIntent.setPackage(pi.packageName);
        } catch (Exception e) {
        }
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        if (apps.size() == 0) {
            try {
                android.util.Log.e("=ff=", "6");
                pi = pm.getPackageInfo(packname, 0);
                resolveIntent.setPackage(pi.packageName);
                apps = pm.queryIntentActivities(resolveIntent, 0);
            } catch (Exception e1) {
            }
        }
        if (apps.size() != 0) {
            ResolveInfo ri = apps.iterator()
                    .next();
            if (ri != null) {
                try {
                    String className = ri.activityInfo.name;
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(packname, className);
                    intent.setComponent(cn);
                    context.startActivity(intent);

//                    info.setType(3);// 打开统计
//                    HttpClientController.statisticsRequest(info, "3");
                    return true;
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    /**
     * @description 获取手机型号的方法
     * @author HaganWu
     * @data 2017/3/3-15:41
     */
    public static String getPhoneModel() {
        String model = null;
        try {
            model = Build.MODEL;
        } catch (Exception e) {
            model = "";
        }
        return model;
    }


    /**
     * 获取手机生产商
     *
     * @return
     * @author Mr.Silence
     * @data 2016-5-5-上午11:22:16
     */
    public static String getAndroidDeviceProduct() {
        String product = "";
        try {
            product = android.os.Build.MANUFACTURER;
        } catch (Exception e) {
            product = "";
        }
        if (TextUtils.isEmpty(product)) {
            product = "";
        }
        return product;
    }

}
