package com.bethena.studyaccessibilityservice.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UserTrajectory implements Parcelable {
    public String packageName;
    public String viewClass;

    public UserTrajectory(String packageName, String viewClass) {
        this.packageName = packageName;
        this.viewClass = viewClass;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.viewClass);
    }

    protected UserTrajectory(Parcel in) {
        this.packageName = in.readString();
        this.viewClass = in.readString();
    }

    public static final Parcelable.Creator<UserTrajectory> CREATOR = new Parcelable.Creator<UserTrajectory>() {
        @Override
        public UserTrajectory createFromParcel(Parcel source) {
            return new UserTrajectory(source);
        }

        @Override
        public UserTrajectory[] newArray(int size) {
            return new UserTrajectory[size];
        }
    };
}
