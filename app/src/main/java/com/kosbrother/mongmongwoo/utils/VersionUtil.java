package com.kosbrother.mongmongwoo.utils;

import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.Settings;

import android.content.Context;

public class VersionUtil {

    public static boolean isVersionUpToDate(int version_code) {
        return BuildConfig.VERSION_CODE >= version_code;
    }

    public static boolean remindUpdate(Context context) {
        int notUpdateTimes = Settings.getNotUpdateTimes(context);
        return notUpdateTimes == 0 || notUpdateTimes == 7;
    }
}
