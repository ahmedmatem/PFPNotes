package com.ahmedmatem.android.snagging.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Drawing {
    private String id;
    private String path;

    public Drawing() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Drawing(String id, String path) {
        this.id = id;
        this.path = path;
    }

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
}
