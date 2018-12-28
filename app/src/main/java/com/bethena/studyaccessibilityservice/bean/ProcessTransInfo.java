package com.bethena.studyaccessibilityservice.bean;

import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class ProcessTransInfo implements Parcelable {

    public String packageName;
    public PackageInfo packageInfo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeParcelable(this.packageInfo, flags);
    }

    public ProcessTransInfo() {
    }

    protected ProcessTransInfo(Parcel in) {
        this.packageName = in.readString();
        this.packageInfo = in.readParcelable(PackageInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProcessTransInfo> CREATOR = new Parcelable.Creator<ProcessTransInfo>() {
        @Override
        public ProcessTransInfo createFromParcel(Parcel source) {
            return new ProcessTransInfo(source);
        }

        @Override
        public ProcessTransInfo[] newArray(int size) {
            return new ProcessTransInfo[size];
        }
    };

    @Override
    public String toString() {
        return "ProcessTransInfo{" +
                "packageName='" + packageName + '\'' +
                ", packageInfo=" + packageInfo +
                '}';
    }
}
