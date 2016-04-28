package com.kosbrother.mongmongwoo.utils;

import android.content.Context;

import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.Settings;

public class VersionUtil {

    public static boolean checkVersionUpToDate(int version_code) {
        return version_code == BuildConfig.VERSION_CODE;
    }

    public static boolean remindUpdate(Context context) {
        int notUpdateTimes = Settings.getNotUpdateTimes(context);
        return notUpdateTimes == 0 || notUpdateTimes == 7;
    }
}
