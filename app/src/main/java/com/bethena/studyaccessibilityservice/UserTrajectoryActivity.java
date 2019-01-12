package com.bethena.studyaccessibilityservice;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.bethena.studyaccessibilityservice.ui.TrajectoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserTrajectoryActivity extends AppCompatActivity {

    RecyclerView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trajectory);


        ArrayList<UserTrajectory> list = getIntent().getParcelableArrayListExtra(Constants.KEY_PARAM1);


        mListView = findViewById(R.id.list_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(new TrajectoryAdapter(list));



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("UserTrajectoryActivity","onNewIntent");

    }
}
