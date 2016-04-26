package com.kosbrother.mongmongwoo.utils;

import com.kosbrother.mongmongwoo.BuildConfig;

public class VersionUtil {

    public static boolean checkVersionUpToDate(int version_code) {
        return version_code == BuildConfig.VERSION_CODE;
    }
}
