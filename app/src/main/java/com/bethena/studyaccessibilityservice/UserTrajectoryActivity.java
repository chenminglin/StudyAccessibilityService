package com.bethena.studyaccessibilityservice;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bethena.studyaccessibilityservice.bean.UserTrajectory;

import java.util.List;

public class UserTrajectoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trajectory);


        List<Parcelable> list = getIntent().getParcelableArrayListExtra(Constants.KEY_PARAM1);


        TextView textView = findViewById(R.id.tv);
        for(Parcelable p:list){
            if(p instanceof UserTrajectory){
                textView.append("应用包："+((UserTrajectory) p).packageName);
                textView.append("界面名："+((UserTrajectory) p).viewClass);
                textView.append("\n");
            }
        }

    }
}
