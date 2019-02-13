package com.bethena.studyaccessibilityservice.service;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.Constants;
import com.bethena.studyaccessibilityservice.R;
import com.bethena.studyaccessibilityservice.ui.CleaningProcessView;
import com.bethena.studyaccessibilityservice.utils.AppUtil;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

public class CleaningProcessWindowService extends Service {

    final String TAG = getClass().getSimpleName();

    CleaningProcessView mCleaningWindow;
    WindowManager windowManager;
    PackageManager pm;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    dismissFloatWindow();
                    break;
            }
        }
    };


    public CleaningProcessWindowService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        initWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        pm = getPackageManager();

//        mHandler.sendEmptyMessageDelayed(1, 5000);

    }

    void initWindow() {
        if (windowManager == null) {
            windowManager = (WindowManager) getApplicationContext().getSystemService(Application.WINDOW_SERVICE);
        }

        if (mCleaningWindow == null) {
            View cleaningLayout = LayoutInflater.from(this).inflate(R.layout.activity_cleaning_process, null);
            mCleaningWindow = new CleaningProcessView();
            mCleaningWindow.vRootView = cleaningLayout;
            mCleaningWindow.tvAppName = cleaningLayout.findViewById(R.id.tv_appname);
            mCleaningWindow.iVIconView = cleaningLayout.findViewById(R.id.iv_app_icon);
            mCleaningWindow.btnCancel = cleaningLayout.findViewById(R.id.btn_cancel);
            mCleaningWindow.mWindowManager = windowManager;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                cleaningLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        return null;
                    }
                });
            }

            mCleaningWindow.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCleaningWindow.btnCancel.setClickable(false);
                    Log.d(TAG, "service cancel ");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dismissFloatWindow();


                }
            });

            Point size = new Point();
            windowManager.getDefaultDisplay().getSize(size);
            int screenWidth = size.x;
            int screenHeight = size.y;

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            layoutParams.packageName = getPackageName();


            int windowFlags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                windowFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                windowFlags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                windowFlags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            }

            layoutParams.flags = windowFlags;

            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else  if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            layoutParams.type = type;


            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            Log.d(TAG, "height = " + (screenHeight + AppUtil.getNavigationBarHeight(this)));
            layoutParams.height = screenHeight + AppUtil.getNavigationBarHeight(this);

            layoutParams.x = 0;
            layoutParams.y = -AppUtil.getNavigationBarHeight(this);

//            int flag =
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
////                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            cleaningLayout.setSystemUiVisibility(flag);

            mCleaningWindow.layoutParams = layoutParams;
            cleaningLayout.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                            || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {
                        Toast.makeText(CleaningProcessWindowService.this, "正在进行中，请稍后等待。。。。", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    return false;
                }
            });

        }

        if (mCleaningWindow != null && !mCleaningWindow.isShowing) {
            mCleaningWindow.btnCancel.setClickable(true);
            try {
                windowManager.addView(mCleaningWindow.vRootView, mCleaningWindow.layoutParams);
                mCleaningWindow.isShowing = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"e.message = "+e.getMessage());
            }
        }

    }


    public void dismissFloatWindow() {
        if (mCleaningWindow != null && mCleaningWindow.isShowing) {
            mCleaningWindow.dismissWindow();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
