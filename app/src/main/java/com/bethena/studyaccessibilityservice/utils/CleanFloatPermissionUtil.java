package com.bethena.studyaccessibilityservice.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;


import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 悬浮窗权限适配类
 *
 * @author zy on 2018/7/30. 16:22
 */

public class CleanFloatPermissionUtil {
    /** vivo总结  6.0并且os版本是2.5及以下以下使用type_toast 6.0及以上使用type_phone 进行适配
     *  注:vivo重写了权限模块,获取悬浮窗权限永远是开的,但是重写过后的权限是默认关的,因此默认需要适配的vivo第一次权限是关的,后面权限是开的*/
    // vivo x5l         系统:4.4.4   os:2.0           能用toast 绕过去
    // vivo y31A        系统:5.0.2   os:2.5           能用toast 绕过去
    // vivo X7          系统:5.1.1   os:3.0           不能用toast 绕过去
    // vivo y55         系统:6.0.1   os:2.6           不能用toast 绕过去
    // vivo y66         系统:6.0.1   os:3.0           不能用toast 绕过去
    /** oppo总结 6.0以下(r9m 除外)使用type_toast 6.0及以上使用type_phone 进行适配 */
    // oppo a53         系统:5.1.1   os:colorOS2.1    能用toast 绕过去
    // oppo a33m        系统:5.1.1   os:colorOS2.1    能用toast 绕过去
    // opp0 a37         系统:5.1.1   os:colorOS3.0    能用toast 绕过去
    // opp0 r9m         系统:5.1     os:colorOS3.0    不能用toast 绕过去
    // opp0 r9s         系统:6.0     os:colorOS3.0    不能用toast 绕过去
    /** 小米总结 V8以下使用type_toast V8及以上全系使用type_phone 进行适配 小米的系统挺给力啊 */
    // Redmi 2          系统:5.1.1   os:8.2           不能用toast 绕过去
    // MI 4             系统:6.0.1   os:9.5           不能用toast 绕过去
    // Redmi 4A         系统:6.0.1   os:8.2           不能用toast 绕过去
    // Redmi Note 5A    系统:7.1.2   os:9.6           系统版本大于等于7.1,不能用toast
    // MIX 2s           系统:8.0     os:9.6           系统版本大于等于7.1,不能用toast
    /** 华为总结 7.0及以下使用type_toast 7.1及以上使用type_phone 进行适配 华为的渣系统*/
    // 荣耀 4X          系统:4.4.4   os:3.0           能用toast 绕过去
    // AL00             系统:5.1.1   os:3.1           能用toast 绕过去
    // AL10             系统:7.0     os:5.0.1         能用toast 绕过去
    // 华为 mate9       系统:8.0.0   os:8.0.0         系统版本大于等于7.1,不能用toast
    /**
     * 三星总结 7.0及以下使用type_toast 7.1及以上type_phone 都能出来 三星的渣系统
     */
    // s7               系统:6.0.1   os:3.0           能用toast 绕过去
    // s7               系统:7.0     os:3.1           能用toast 绕过去
    // s7               系统:8.0     os:3.1           使用type_phone 不需要权限
    // s8               系统:8.0     os:3.1           使用type_phone 不需要权限
    private Context mContext;


    /**
     * 文轰说5s都没走progress60(抄热点的)提示网络较差,试试刷新
     */
    public void doHandlerMsg(Message msg) {
    }

//    /**
//     * 根据一些条件判断是否要check
//     */
//    public boolean checkModeWithCondition(Context context, final String comefrom) {
//        mContext = context;
//        //如果曾经是有权限的(权限丢失的情况下是不走的),如果开关是关闭的
//        if (PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_VIVO_FLOAT_PERMISSION_SPECIAL, false)
//                || !PrefsCleanUtil.getInstance().getBoolean(Constants.FLOAT_SETTING
//                , PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_XFK_SWITCH, CleanSwitch.OPEN_FLOAT_KEY == 1))) {
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---checkModeThreeDays --72-- 曾经是有权限的(权限丢失的情况下是不走的)或者开关是关闭的");
//            return false;
//        }
//        return checkMode(mContext, comefrom);
//    }

    /**
     * 检查是否需要弹出权限提示
     */
//    public boolean checkMode(Context context, String comefrom) {
//        mContext = context;
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanAboutActivity---CleanFloatPermissionUtil --getModel-- " + CleanAppApplication.phoneModel);
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanAboutActivity---CleanFloatPermissionUtil --悬浮窗权限-- " + AppUtil.getAppOps(CleanAppApplication.getInstance()));
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanAboutActivity---CleanFloatPermissionUtil --当前厂商-- " + phoneManufacturer);
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanAboutActivity---CleanFloatPermissionUtil --当前Android版本-- " + Build.VERSION.SDK_INT);
//        if (isAdaptation()) {
//            //已经适配好了能用toast的就不需要检查权限
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---checkMode --87-- 据查这个手机不需要悬浮窗权限就可以出现悬浮窗");
//            return false;
//        }
//        boolean hasPermission = checkPermission(true);
//        if (!hasPermission) {
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---checkMode --92-- 么有权限");
//            showDialog(comefrom);
//            if ("finishActivity".equals(comefrom)) {
//                UMengAgent.onEvent(CleanAppApplication.getInstance(), UMengAgent.CLEAN_JSFINISH_SUSPENSIONWINDOW_POINT_SHOW);
//            }
//            return true;
//        } else {
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---checkMode --98-- 已经有权限了");
//            return false;
//        }
//    }

    private void showDialog(final String comefrom) {
//        DialogOneBtn dialogOneBtn = new DialogOneBtn(mContext, new DialogOneBtn.DialogListener() {
//            @Override
//            public void doClick() {
//                jump2System();
//                if ("finishActivity".equals(comefrom)) {
//                    UMengAgent.onEvent(CleanAppApplication.getInstance(), UMengAgent.CLEAN_JSFINISH_SUSPENSIONWINDOW_POINT_CLICK);
//                }
//                if (mContext instanceof CleanFinishDoneNewsListActivity) {
//                    ((CleanFinishDoneNewsListActivity) mContext).hasClickPermission();
//                }
//            }
//
//            @Override
//            public void doDismiss() {
//            }
//        });
//        dialogOneBtn.setDialogTitle("提升加速效果!");
//        if ("settingActivity".equals(comefrom)) {
//            dialogOneBtn.setDialogContent("允许\"清理大师\"悬浮窗权限,可提升加速效果高达30%!");
//        } else {
//            dialogOneBtn.setDialogContent("允许\"清理大师\"悬浮窗权限,可提升加速效果高达30%! (可通过\"我的-设置\"管理)");
//        }
//        dialogOneBtn.setDialogBtnText("立即授权");
//        dialogOneBtn.setDialogBtnTextColor(0xFF32bd7b);
//        dialogOneBtn.setCanceledOnTouchOutside(false);
//        dialogOneBtn.show();
    }

    public static void jump2System(Context context,String phoneManufacturer) {
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---jump2System --153-- ");
        final Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            switch (phoneManufacturer) {
                case "Xiaomi":
                case "HUAWEI":
                case "OPPO":
                case "vivo":
                    jumpPermissionPage(context,phoneManufacturer);
                    break;
                default:
                    // 在此根据用户手机当前版本跳转系统设置界面
                    if (Build.VERSION.SDK_INT >= 9) {
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                    }
                    context.startActivity(intent);
                    break;
            }
        } catch (Exception e) {//抛出异常就直接打开设置页面
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "jump2System-166-", e);
            intent.setAction(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
        //之前是小米系列会直接黑屏,现在vivo更新了系统过后也会黑,因此这里注释掉这个页面 2018年11月6日 09:13:44 zuoyuan
        //文红说去掉
//        if (mContext!=null){
//            Intent intent1=new Intent(mContext,CleanNotifyPermissionNotifyActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent1.putExtra("title","找到[清理大师]开启悬浮窗");
//            mContext.startActivity(intent1);
//        }
    }

    /**
     * 检查权限,如果当前有权限就记录下曾经取得过权限
     */
//    public boolean checkPermission(boolean special) {
//        //下面适配好的直接返回true
//        if (isAdaptation()) {
//            return true;
//        }
//        boolean hasPermission = AppUtil.getAppOps(CleanAppApplication.getInstance());
//        if (hasPermission) {
//            boolean isRecorded = PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_HAD_FLOAT_PERMISSION, false);
//            if (!isRecorded) {
//                PrefsCleanUtil.getInstance().putBoolean(Constants.CLEAN_HAD_FLOAT_PERMISSION, true);
//            }
//        }
//        if (special) {
//            if (phoneManufacturer.equals("vivo") && PrefsCleanUtil.getInstance().getBoolean(Constants.CLEAN_VIVO_FLOAT_PERMISSION_SPECIAL, true)) {
//                /* vivo的悬浮窗权限用了3套 权限默认是关的.但是获取的是开的,因此特殊处理vivo手机第一次获取权限问题 */
//                PrefsCleanUtil.getInstance().putBoolean(Constants.CLEAN_VIVO_FLOAT_PERMISSION_SPECIAL, false);
//                hasPermission = false;
//            }
//        }
//        return hasPermission;
//    }

    /**
     * 悬浮窗权限适配 toast or phone
     */
//    public void setParams(WindowManager.LayoutParams params) {
//        if (Build.VERSION.SDK_INT >= 25) {
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParams --TYPE_PHONE-- ");
//            params.type = WindowManager.LayoutParams.TYPE_PHONE;
//        } else if (AppUtil.getAppOps(CleanAppApplication.getInstance())) {
//            /* vivo的悬浮窗权限用了3套 权限默认是关的.但是获取的是开的,因此特殊处理vivo手机第一次获取权限问题 */
////            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParms --TYPE_PHONE-- ");
////            params.type = WindowManager.LayoutParams.TYPE_PHONE;
//            /* 产品为了保证悬浮窗出现的概率不会有大的影响,默认使用toast,其实上面注释掉的部分才是适配的结果 */
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParms --TYPE_TOAST-- ");
//            params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        } else if (Build.VERSION.SDK_INT <= 18) {
//            //api19以下没有悬浮窗权限//悬浮窗  api17以下toast 无点击事件
//            params.type = WindowManager.LayoutParams.TYPE_PHONE;
//            Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParms --TYPE_PHONE-- ");
//        } else {
//             /* 产品为了保证悬浮窗出现的概率不会有大的影响,默认使用toast,其实注释掉的部分才是适配的结果 */
////            if (isAdaptation()){
//            params.type = WindowManager.LayoutParams.TYPE_TOAST;
////                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParams --TYPE_TOAST-- " );
////            }else{
////                Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---setParams --TYPE_PHONE-- " );
////                params.type = WindowManager.LayoutParams.TYPE_PHONE;
////            }
//        }
//    }

    public static void jumpPermissionPage(Context context,String phoneManufacturer) {
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---jumpPermissionPage ---- " + phoneManufacturer);
        switch (phoneManufacturer) {
            case "HUAWEI":
                goHuaWeiMainager(context);
                break;
            case "vivo":
                goVivoMainager(context);
                break;
            case "OPPO":
                goOppoMainager(context);
                break;
            case "Coolpad":
                goCoolpadMainager(context);
                break;
            case "Meizu":
                goMeizuMainager(context);
                break;
            case "Xiaomi":
                goXiaoMiMainager(context);
                break;
            case "samsung":
                goSangXinMainager(context);
                break;
            case "Sony":
                goSonyMainager(context);
                break;
            case "LG":
                goLGMainager(context);
                break;
            default:
                goIntentSetting(context);
                break;
        }
    }

    private static void goLGMainager(Context context) {
        try {
            Intent intent = new Intent(context.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goIntentSetting(context);
        }
    }

    private static void goSonyMainager(Context context) {
        try {
            Intent intent = new Intent(context.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goIntentSetting(context);
        }
    }

    private static void goHuaWeiMainager(Context context) {
        //华为mate9 的权限页面(最佳,需要自定义权限,自定义权限无效,需要华为系统层) :com.huawei.systemmanager/com.huawei.permissionmanager.ui.SingleAppActivity
        //华为mate9 的权限页面(最佳,需要自定义权限,自定义权限无效,需要华为系统层) :com.android.packageinstaller/com.android.packageinstaller.permission.ui.ManagePermissionsActivity
        //华为mate9 的权限页面 :com.huawei.systemmanager/com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity

        try {
            Intent intent = new Intent(context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            /* 悬浮窗应用列表页面 */
//            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            ComponentName comp = ComponentName.unflattenFromString("com.huawei.systemmanager/com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-323-", e);
            goIntentSetting(context);
        }
    }

    private static void goXiaoMiMainager(Context context) {
        String rom = getProp(KEY_VERSION_MIUI);
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---goXiaoMiMainager --314-- " + rom);
        Intent intent = new Intent();
        if ("V8".equals(rom) || "V9".equals(rom) || "V10".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } else if ("V6".equals(rom) || "V7".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } else {
            goIntentSetting(context);
        }
    }

    private static void goMeizuMainager(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-352-", e);
            goIntentSetting(context);
        }
    }

    private static void goSangXinMainager(Context context) {
        //三星4.3可以直接跳转
        goIntentSetting(context);
    }

    private static void goIntentSetting(Context context) {
//        Logger.i(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil---goIntentSetting --384-- ");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-369-", e);
        }
    }

    private static void goOppoMainager(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = ComponentName.unflattenFromString("com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-381-", e);
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = ComponentName.unflattenFromString("com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
            } catch (Exception e1) {
//                Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-389-", e1);
                try {
                    AppUtil.startApk(context,"com.coloros.safecenter");
                } catch (Exception e2) {
//                    Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-393-", e2);
                    goIntentSetting(context);
                }
            }

        }
    }

    /**
     * doStartApplicationWithPackageName("com.yulong.android.security:remote")
     * 和Intent open = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
     * startActivity(open);
     * 本质上没有什么区别，通过Intent open...打开比调用doStartApplicationWithPackageName方法更快，也是android本身提供的方法
     */
    private static void goCoolpadMainager(Context context) {
        AppUtil.startApk(context,"com.yulong.android.security:remote");
    }

    private static void goVivoMainager(Context  context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = ComponentName.unflattenFromString("com.vivo.permissionmanager/.activity.SoftPermissionDetailActivity");
            intent.setComponent(comp);
            intent.putExtra("packagename", context.getPackageName());//vivo y66 用的这货  真的是日狗..猜了劳资6个小时
            context.startActivity(intent);
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-421-", e);
            try {
                AppUtil.startApk(context,"com.iqoo.secure");
            } catch (Exception e1) {
//                Logger.iCatch(Logger.TAG, Logger.ZYTAG, "CleanFloatPermissionUtil-426-", e1);
                goIntentSetting(context);
            }
        }
    }

    /**
     * 获取Rom的版本
     */
    public static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    public static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    public static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    public static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    public static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception e) {
//            Logger.iCatch(Logger.TAG, Logger.ZYTAG, "getProp-449-", e);
            return "";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
//                    Logger.iCatch(Logger.TAG, Logger.ZYTAG, "getProp-456-", e);
                }
            }
            if (TextUtils.isEmpty(line)) {
                line = "others";
            }
            return line;
        }
    }

    /**
     * 是否能使用toast绕过权限
     */
    public boolean isAdaptation(String phoneManufacturer) {
        if (Build.VERSION.SDK_INT < 25) {
            //25及之后的版本toast不能用作为悬浮窗
            switch (phoneManufacturer) {
                case "samsung":
                    //三星不存在悬浮窗管理
                    if (Build.VERSION.SDK_INT < 25) {
                        return true;
                    }
                    break;
                case "OPPO":
                    if (Build.VERSION.SDK_INT < 23 && !"OPPO R9m".equals(AppUtil.getPhoneModel())) {
                        //R9m android5.1+os3.0不能绕过权限
                        return true;
                    }
                    break;
                case "vivo":
                    if (Build.VERSION.SDK_INT < 23
                            && (getProp(KEY_VERSION_VIVO).startsWith("2.4")
                            || getProp(KEY_VERSION_VIVO).startsWith("2.3")
                            || getProp(KEY_VERSION_VIVO).startsWith("2.2")
                            || getProp(KEY_VERSION_VIVO).startsWith("2.0")
                            || getProp(KEY_VERSION_VIVO).startsWith("1"))) {
                        return true;
                    }
                    break;
                case "HUAWEI":
                    if (Build.VERSION.SDK_INT < 25) {
                        return true;
                    }
                    break;
                case "Xiaomi":
                    String romVersion = getProp(KEY_VERSION_MIUI);
                    if ("V4".equals(romVersion) || "V5".equals(romVersion) || "V6".equals(romVersion) || "V7".equals(romVersion)) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
