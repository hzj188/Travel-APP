package com.app.luxingapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Description: SharedPreferences工具类
 * @Author: LM
 * @CreateDate: 2020/3/20 15:55
 * @Question:
 */
public class SpsUtil {

    //用户状态 全部以1开头
    public static final String USER_DATA = "1-1";
    public static final String USER_LOGIN = "1-2";

    public static void put(String key, Object value,Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences("app", Context.MODE_PRIVATE).edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else {
            throw new RuntimeException("SharedPreferences 不可保存当前类型");
        }
        edit.apply();
    }

    public static String getString(Context context,String key, String... defaultValue) {
        return context.getSharedPreferences("app", Context.MODE_PRIVATE).getString(key, defaultValue == null ? "" : defaultValue[0]);
    }
}