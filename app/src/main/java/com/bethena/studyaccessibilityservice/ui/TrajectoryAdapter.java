package com.bethena.studyaccessibilityservice.ui;

import android.support.annotation.Nullable;

import com.bethena.studyaccessibilityservice.R;
import com.bethena.studyaccessibilityservice.bean.UserTrajectory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TrajectoryAdapter extends BaseQuickAdapter<UserTrajectory,BaseViewHolder> {
    public TrajectoryAdapter( @Nullable List<UserTrajectory> data) {
        super(R.layout.item_trajectory, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserTrajectory item) {

        helper.setText(R.id.package_name,item.packageName);
        helper.setText(R.id.view_name,item.viewClass);

    }
}
