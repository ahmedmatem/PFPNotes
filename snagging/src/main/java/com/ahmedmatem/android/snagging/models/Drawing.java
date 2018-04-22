package com.ahmedmatem.android.snagging.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Drawing implements Parcelable {
    private String id;
    private String path;

    public Drawing() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Drawing(String id, String path) {
        this.id = id;
        this.path = path;
    }

    protected Drawing(Parcel in) {
        id = in.readString();
        path = in.readString();
    }

    public static final Creator<Drawing> CREATOR = new Creator<Drawing>() {
        @Override
        public Drawing createFromParcel(Parcel in) {
            return new Drawing(in);
        }

        @Override
        public Drawing[] newArray(int size) {
            return new Drawing[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
    }
}
