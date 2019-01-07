package com.bethena.studyaccessibilityservice.permission.autostart;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import com.bethena.studyaccessibilityservice.permission.rom.RomUtils;

import java.util.List;

public class AutoStartPermissionUtils {


    /**
     * 判断是否具有可以打开权限界面的可能
     *
     * @param context
     * @return
     */
    public static boolean isEnablePermissioActivity(Context context) {

        Log.d("AutoStartPermission", "Build.MANUFACTURER = " + Build.MANUFACTURER);

        if (RomUtils.checkIsMiuiRom()) {
            if (isIntentAvailabl(context, provideIntentByAction("miui.intent.action.OP_AUTO_START"))) {
                return true;
            } else {
                return false;
            }

        } else if (RomUtils.checkIsMeizuRom()) {
            if (isIntentAvailabl(context,
                    provideIntentByClass("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"))) {
                return true;
            } else {
                return false;
            }
        } else if (RomUtils.checkIsHuaweiRom()) {
            if (isIntentAvailabl(context,
                    provideIntentByAction("huawei.intent.action.HSM_BOOTAPP_MANAGER"))) {
                return true;
            } else {
                return false;
            }
        } else if (RomUtils.checkIs360Rom()) {
            return false;
        } else if (RomUtils.checkIsOppoRom()) {
            if (isIntentAvailabl(context,
                    provideIntentByClass("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity"))) {
                return true;
            } else if (isIntentAvailabl(context,
                    provideIntentByClass("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionTopActivity"))) {
                return true;
            } else if (isIntentAvailabl(context, provideIntentByClass("com.color.safecenter", "com.color.safecenter.permission.PermissionTopActivity"))) {
                return true;
            } else {
                return false;
            }
        } else if (RomUtils.checkIsSamsungRom()) {
            if (isIntentAvailabl(context,
                    provideIntentByClass("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"))) {
                return true;
            } else if (isIntentAvailabl(context,
                    provideIntentByClass("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"))) {
                return true;
            } else if (isIntentAvailabl(context,
                    provideIntentByClass("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"))) {
                return true;
            } else {
                return false;
            }
        } else if (RomUtils.checkIsVivoRom()) {
            if (isIntentAvailabl(context, provideIntentByClass("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"))) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public static void openPermissionActivity(Context context) {
        if (RomUtils.checkIsMiuiRom()) {
            Intent intent = provideIntentByAction("miui.intent.action.OP_AUTO_START");
            if (isIntentAvailabl(context, intent)) {
                context.startActivity(intent);
            }
        } else if (RomUtils.checkIsMeizuRom()) {
            Intent intent = provideIntentByClass("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
            if (isIntentAvailabl(context,
                    intent)) {
                context.startActivity(intent);
            }
        } else if (RomUtils.checkIsHuaweiRom()) {
            Intent intent = provideIntentByAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
            if (isIntentAvailabl(context, intent)) {
                context.startActivity(intent);
            }
        } else if (RomUtils.checkIs360Rom()) {

        } else if (RomUtils.checkIsOppoRom()) {
            Intent intent = provideIntentByClass("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity");
            if (isIntentAvailabl(context, intent)) {
                context.startActivity(intent);
            } else {
                Intent intent1 = provideIntentByClass("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionTopActivity");
                if (isIntentAvailabl(context, intent1)) {
                    context.startActivity(intent1);
                } else {
                    Intent intent2 = provideIntentByClass("com.color.safecenter", "com.color.safecenter.permission.PermissionTopActivity");
                    if (isIntentAvailabl(context, intent2)) {
                        context.startActivity(intent2);
                    }
                }
            }
        } else if (RomUtils.checkIsSamsungRom()) {
            Intent intent = provideIntentByClass("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity");
            if (isIntentAvailabl(context, intent)) {
                context.startActivity(intent);
            } else {
                Intent intent1 = provideIntentByClass("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity");
                if (isIntentAvailabl(context, intent1)) {
                    context.startActivity(intent1);
                } else {
                    Intent intent2 = provideIntentByClass("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity");
                    if (isIntentAvailabl(context, intent2)) {
                        context.startActivity(intent2);
                    }
                }
            }
        } else if (RomUtils.checkIsVivoRom()) {
            Intent intent = provideIntentByClass("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity");
            if (isIntentAvailabl(context, intent)) {
                context.startActivity(intent);
            }
        }
    }


    private static Intent provideIntentByClass(String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        return intent;
    }

    private static Intent provideIntentByAction(String action) {
        Intent intent = new Intent(action);
        return intent;
    }

    private static boolean isIntentAvailabl(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }


}
