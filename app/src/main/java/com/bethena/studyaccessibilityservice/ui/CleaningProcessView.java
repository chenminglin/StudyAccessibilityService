package com.bethena.studyaccessibilityservice.ui;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CleaningProcessView {
    public View vRootView;

    public ImageView iVIconView;
    public TextView tvAppName;

    public Button btnCancel;

    public WindowManager mWindowManager;

    public WindowManager.LayoutParams layoutParams;

    public boolean isShowing;

    public void dismissWindow() {
        if (mWindowManager != null && vRootView != null) {
            mWindowManager.removeViewImmediate(vRootView);
            isShowing = false;
        }
    }

}
