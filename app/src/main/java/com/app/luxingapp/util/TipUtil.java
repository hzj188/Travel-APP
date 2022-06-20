package com.app.luxingapp.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class TipUtil {

    public static void show(Context context,String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}