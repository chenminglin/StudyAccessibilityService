package com.bethena.studyaccessibilityservice;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bethena.studyaccessibilityservice.bean.ProcessTransInfo;

public class CleaningProcessActivity extends AppCompatActivity {
    ImageView ivAppIcon;
    TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaning_process);

        ivAppIcon = findViewById(R.id.iv_app_icon);
        tvAppName = findViewById(R.id.tv_appname);

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    void initData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            PackageManager pm = getPackageManager();
            ProcessTransInfo processTransInfo = intent.getParcelableExtra(Constants.KEY_PARAM1);
            PackageInfo packageInfo = processTransInfo.packageInfo;
            ivAppIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(pm));
            tvAppName.setText(packageInfo.applicationInfo.loadLabel(pm));
        }

    }

}
