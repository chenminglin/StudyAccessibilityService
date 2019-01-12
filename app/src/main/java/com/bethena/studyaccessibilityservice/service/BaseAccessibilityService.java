package com.bethena.studyaccessibilityservice.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BaseAccessibilityService extends AccessibilityService {
    final String TAG = getClass().getSimpleName();

    private AccessibilityManager mAccessibilityManager;
    private Context mContext;
    private static BaseAccessibilityService mInstance;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }


    public static BaseAccessibilityService getInstance() {
        if (mInstance == null) {
            mInstance = new BaseAccessibilityService();
        }

        return mInstance;
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    private boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    long lastPerformBackClickTime = 0;

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
        lastPerformBackClickTime = System.currentTimeMillis();
    }

    /**
     * 模拟返回操作
     */

    public void performBackClick() {

        if (lastPerformBackClickTime != 0 && System.currentTimeMillis() - lastPerformBackClickTime < 200) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        performGlobalAction(GLOBAL_ACTION_BACK);

        lastPerformBackClickTime = System.currentTimeMillis();
    }

    /**
     * 模拟下滑操作
     */
    public void performScrollBackward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);

    }

    /**
     * 模拟上滑操作
     */
    public void performScrollForward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByTextFromNode(String text, AccessibilityNodeInfo rootNodeInfo) {
        AccessibilityNodeInfo accessibilityNodeInfo = rootNodeInfo;
        if (accessibilityNodeInfo == null) {
            return null;
        }
        printAllNode(accessibilityNodeInfo);
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {

                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public boolean isCleanTargetApp(String appname) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();

        if (accessibilityNodeInfo == null) {
            return false;
        }


        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(appname);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            return true;
        }

        return false;
    }


    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public void clickTextViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickTextViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void printAllNode(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }

        Log.w(TAG, "printAllNode ===  text = " + nodeInfo.getText()
                + ", descript = " + nodeInfo.getContentDescription()
                + ", className = " + nodeInfo.getClassName() + ", resId = " + nodeInfo.getViewIdResourceName());

        int childCount = nodeInfo.getChildCount();
        if (childCount > 0) {
            for (int n = 0; n < childCount; n++) {
                printAllNode(nodeInfo.getChild(n));
            }
        }
    }

    /**
     * 判断是否跳到了app视图，粗略的判断方式
     *
     * @return
     */
    public boolean isRootOfAppView(AccessibilityNodeInfo rootNodeInfo, String appName) {
        if (rootNodeInfo == null) {
            return false;
        } else {
            if (rootNodeInfo.getText() != null) {
                String text = rootNodeInfo.getText().toString();
//                Log.d(TAG, "printAllNode ===  text = " + text);
                if (appName.equals(text)) {
                    return true;
                }
            }
            int childCount = rootNodeInfo.getChildCount();
            boolean isAppView = false;
            if (childCount > 0) {
                for (int n = 0; n < childCount; n++) {
                    if (isRootOfAppView(rootNodeInfo.getChild(n), appName)) {
                        isAppView = true;
                        break;
                    }

                }
            }
            return isAppView;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    protected void overridePendingTransition(int enterAnim, int exitAnim) {
        try {

            Class serviceClass = getClass()
                    .getSuperclass()
                    .getSuperclass()
                    .getSuperclass();
            Field mActivityManager = serviceClass
                    .getDeclaredField("mActivityManager");
            Field mToken = serviceClass
                    .getDeclaredField("mToken");
//            mActivityManager.getClass().getMethod("overridePendingTransition");


            Method method = mActivityManager.getType()
                    .getDeclaredMethod("overridePendingTransition", mToken.getType(), String.class, int.class, int.class);

//            Log.d(TAG,"mActivityManager.getType()  = "+);
//            for (Method method : mActivityManager.getType().getDeclaredMethods()) {
            Log.d(TAG, "" + method.getName());
            Log.d(TAG, "" + mActivityManager.get(this));
            Log.d(TAG, "" + mToken.get(this));
            Log.d(TAG, "" + getPackageName());

//            method.invoke(mActivityManager.get(this),mToken.get(this),getPackageName(),enterAnim,exitAnim);

//            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("BASE", "onAccessibilityEvent");

    }

    @Override
    public void onInterrupt() {
    }
}
