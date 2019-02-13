package com.bethena.studyaccessibilityservice.service;

import android.app.AppOpsManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MyIntentService extends IntentService {

    final String TAG = getClass().getSimpleName();

    public final static String ACTION = "MyIntentService";
    WindowManager wmManager;
    WindowManager.LayoutParams wmParams;
    View testView;

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        wmManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();

        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        wmParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL; // 调整悬浮窗口至右侧中间
        wmParams.x = 0;// 以屏幕左上角为原点，设置x、y初始值
        wmParams.y = 0;

        wmParams.width = 0;// 设置悬浮窗口长宽数据
        wmParams.height = 0;
        testView = new View(MyIntentService.this);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
//            final String action = intent.getAction();
//            SystemClock.sleep(2000);
//            Intent intent1 = new Intent(MyIntentService.this, GuideDialogActivity.class);
//            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent1);

            PackageManager pm = getPackageManager();
            ApplicationInfo ai = null;
            try {
                ai = pm.getApplicationInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Log.d("!!", "!!" + ai.uid);
            int uid = ai.uid;

            int pid = android.os.Process.myPid();
            while (true) {
                SystemClock.sleep(200);
//                try {
//                    wmManager.addView(new View(MyIntentService.this), wmParams);
//                    Log.d(TAG, "成功  。。。。。");
//                    break;
//                } catch (Exception e) {
//                    Log.d(TAG, "失败 message " + e.getMessage());
//                }

//                Intent intent1 = new Intent("vivo.intent.action.CHECK_ALERT_WINDOW");
//                intent1.putExtra("PID",pid);
//                intent1.putExtra("UID",ai.uid);
//                sendBroadcast(intent1);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                    AppOpsManager mOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
//                    int allowShowMode = mOpsManager.checkOp("checkOp", uid, getPackageName());
//                    Log.d(TAG, "allowShowMode = " + allowShowMode);

                    AppOpsManager manager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    try {
                        Class clazz = AppOpsManager.class;
                        Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                        int allowMode = (int) method.invoke(manager, 24, uid, getPackageName());
                        Log.d(TAG, "allowShowMode = " + allowMode);
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }



//                    Uri uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
//                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//                    if (cursor != null) {
//                        while (cursor.moveToNext()) {
//                            String[] names = cursor.getColumnNames();
//
//                            StringBuffer stringBuffer = new StringBuffer();
//                            for (String name : names) {
//                                String value = cursor.getString(cursor.getColumnIndex(name));
//                                stringBuffer.append(name + ":" + value + ",");
//
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "cursor is null");
//                    }


//
//
//                    Log.d(TAG,stringBuffer.toString());


                }

//                Class clazz = Settings.class;
//                Method canDrawOverlays = null;
//                try {
//                    canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
//                    boolean result = (Boolean) canDrawOverlays.invoke(null, MyIntentService.this);
//                    Log.d(TAG, "result = " + result);
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }


            }

//            }


        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
//        wmManager.removeView(testView);
    }
}
