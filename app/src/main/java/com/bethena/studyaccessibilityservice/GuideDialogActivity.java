package com.bethena.studyaccessibilityservice;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class GuideDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setTheme(R.style.dialog);

        if (Build.VERSION.SDK_INT >= 19)
        {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.flags |= 0x4000000;
            getWindow().setAttributes(layoutParams);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            setFinishOnTouchOutside(true);
        }

        getWindow().setGravity(Gravity.BOTTOM);

        setContentView(R.layout.activity_guide_dialog);
    }
}
