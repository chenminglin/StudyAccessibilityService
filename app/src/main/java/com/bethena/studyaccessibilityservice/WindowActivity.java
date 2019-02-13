package com.bethena.studyaccessibilityservice;

import android.app.Application;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bethena.studyaccessibilityservice.service.CleaningProcessWindowService;
import com.bethena.studyaccessibilityservice.ui.CleaningProcessView;
import com.bethena.studyaccessibilityservice.utils.AppUtil;

public class WindowActivity extends AppCompatActivity {

    CleaningProcessView mCleaningWindow;
    WindowManager windowManager;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initWindow();
                    }
                });
            }
        }).start();

    }

    void initWindow() {
        if (windowManager == null) {
            windowManager = (WindowManager)getSystemService(Application.WINDOW_SERVICE);
        }

        if (mCleaningWindow == null) {
            View cleaningLayout = LayoutInflater.from(this).inflate(R.layout.activity_cleaning_process, null);
            mCleaningWindow = new CleaningProcessView();
            mCleaningWindow.vRootView = cleaningLayout;
            mCleaningWindow.tvAppName = cleaningLayout.findViewById(R.id.tv_appname);
            mCleaningWindow.iVIconView = cleaningLayout.findViewById(R.id.iv_app_icon);
            mCleaningWindow.btnCancel = cleaningLayout.findViewById(R.id.btn_cancel);
            mCleaningWindow.mWindowManager = windowManager;

            mCleaningWindow.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCleaningWindow.btnCancel.setClickable(false);
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
            layoutParams.gravity = Gravity.LEFT;
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
            } else {
                type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            layoutParams.type = type;


            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            layoutParams.x = screenWidth;
            layoutParams.y = screenHeight;

//            int flag =
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
////                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//
//            cleaningLayout.setSystemUiVisibility(flag);

            mCleaningWindow.layoutParams = layoutParams;
            cleaningLayout.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                            || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {
                        Toast.makeText(WindowActivity.this, "正在进行中，请稍后等待。。。。", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    return false;
                }
            });

        }

        if (mCleaningWindow != null && !mCleaningWindow.isShowing) {
            mCleaningWindow.btnCancel.setClickable(true);
            windowManager.addView(mCleaningWindow.vRootView, mCleaningWindow.layoutParams);
            mCleaningWindow.isShowing = true;
        }

    }

    public void dismissFloatWindow() {
        if (mCleaningWindow != null && mCleaningWindow.isShowing) {
            mCleaningWindow.dismissWindow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissFloatWindow();
    }
}
