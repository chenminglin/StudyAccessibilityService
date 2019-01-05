package com.bethena.studyaccessibilityservice;

import android.support.annotation.Nullable;
import android.text.format.Formatter;

import com.bethena.studyaccessibilityservice.bean.ProcessInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class AppAdapter extends BaseQuickAdapter<ProcessInfo, BaseViewHolder> {

    List<ProcessInfo> mDatas;

    public AppAdapter(@Nullable List<ProcessInfo> datas) {
        super(R.layout.item_app, datas);
        this.mDatas = datas;
    }


    @Override
    protected void convert(BaseViewHolder helper, ProcessInfo item) {
        helper.setImageDrawable(R.id.app_icon, item.appIcon)
                .setText(R.id.app_name, item.appName)
                .setText(R.id.app_pkg, item.packageName)
                .setChecked(R.id.cb, item.isChecked);

        String size = Formatter.formatFileSize(mContext, item.size);

        helper.setText(R.id.app_size,size);
        helper.addOnClickListener(R.id.item_root);

    }

}
