package com.ahmedmatem.android.snagging;

import android.content.Context;
import android.content.Intent;

public class Snagging {
    private Snagging() {
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, DrawingsListActivity.class);
        context.startActivity(intent);
    }
}
