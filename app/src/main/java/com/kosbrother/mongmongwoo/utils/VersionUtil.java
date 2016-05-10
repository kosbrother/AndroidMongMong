package com.kosbrother.mongmongwoo.utils;

import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.Settings;

public class VersionUtil {

    public static boolean isVersionUpToDate(int version_code) {
        return BuildConfig.VERSION_CODE >= version_code;
    }

    public static boolean remindUpdate() {
        int notUpdateTimes = Settings.getNotUpdateTimes();
        return notUpdateTimes == 0 || notUpdateTimes == 7;
    }
}
