package com.ahmedmatem.android.pfpnotes.models;

/**
 * Created by ahmed on 11/03/2018.
 */

public abstract class Item {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_NOTE = 2;

    public abstract int getType();
}
