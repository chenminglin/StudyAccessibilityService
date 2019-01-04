package com.bethena.studyaccessibilityservice.bean;

import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class ProcessTransInfo implements Parcelable {

    public static int CLEAN_ACCIDENT_TYPE_NOTHING = 0;
    public static int CLEAN_ACCIDENT_TYPE_CLEAN_BUTTON_NOT_FOUND = 1;
    public static int CLEAN_ACCIDENT_TYPE_CLEAN_VIEW_NOT_FOUND = 2;

    public String packageName;
    public PackageInfo packageInfo;

    public boolean isCleaned;

    //发生意外的类型
    public int cleanAccidentType;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeParcelable(this.packageInfo, flags);
        dest.writeByte(this.isCleaned ? (byte) 1 : (byte) 0);
    }

    public ProcessTransInfo() {
    }

    protected ProcessTransInfo(Parcel in) {
        this.packageName = in.readString();
        this.packageInfo = in.readParcelable(PackageInfo.class.getClassLoader());
        this.isCleaned = in.readByte() != 0;
    }

    public static final Creator<ProcessTransInfo> CREATOR = new Creator<ProcessTransInfo>() {
        @Override
        public ProcessTransInfo createFromParcel(Parcel source) {
            return new ProcessTransInfo(source);
        }

        @Override
        public ProcessTransInfo[] newArray(int size) {
            return new ProcessTransInfo[size];
        }
    };
}
