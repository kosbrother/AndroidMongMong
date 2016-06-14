package com.kosbrother.mongmongwoo.fivestars;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FiveStartsManager {

    private static FiveStartsManager instance;
    private final SharedPreferences sharedPreferences;

    private FiveStartsManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static FiveStartsManager getInstance(Context context) {
        if (instance == null) {
            instance = new FiveStartsManager(context);
        }
        return instance;
    }

    public boolean showFiveStarsRecommend() {
        boolean stared = sharedPreferences.getBoolean(FiveStarsPreferences.STARED, false);
        if (stared) {
            return false;
        }
        int count = sharedPreferences.getInt(FiveStarsPreferences.COUNT, 4);
        saveUpdateCount(count);
        return count == 7;
    }

    public void stared() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FiveStarsPreferences.STARED, true);
        editor.apply();
    }

    private void saveUpdateCount(int count) {
        int updateCount;
        updateCount = count == 7 ? 0 : count + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FiveStarsPreferences.COUNT, updateCount);
        editor.apply();
    }

    private static class FiveStarsPreferences {
        public static final String COUNT = "COUNT";
        public static final String STARED = "STARED";
    }
}
