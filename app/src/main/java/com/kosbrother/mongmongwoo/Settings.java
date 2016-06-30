package com.kosbrother.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.User;

public class Settings {

    private static final String PREFS_NAME = "USER_DATA";

    private final static String keyUserName = "USER_NAME";
    private final static String keyUserFBUid = "USER_UID";
    private final static String keyUserGender = "USER_GENDER";
    private final static String keyUserRealName = "USER_REAL_NAME";
    private final static String keyUserPhone = "USER_PHONE";
    private final static String keyUserAddress = "USER_ADDRESS";
    private final static String keyUserFBPic = "USER_PIC";
    private final static String keyUserFBEmail = "USER_EMAIL";

    private final static String keyFirstAddShoppingCar = "IS_FIRST_ADD_SHOPPING_CAR";

    private final static String keyStoreId = "KeyStoreID";
    private final static String keyStoreCode = "KeyStoreCode";
    private final static String keyStoreName = "KeyStoreName";
    private final static String keyStoreAddress = "KeyStoreAddress";

    private static final String PREFS_VERSION = "PREFS_VERSION";
    private final static String KEY_STRING_VERSION_NAME = "KEY_STRING_VERSION_NAME";
    private final static String KEY_BOOLEAN_VERSION_UP_TO_DATE = "KEY_BOOLEAN_VERSION_UP_TO_DATE";
    private static final String KEY_INT_NOT_UPDATE_TIMES = "KEY_INT_NOT_UPDATE_TIMES";

    private static Context context;

    public static void init(Context context) {
        Settings.context = context;
    }

    public static void saveUserFBData(User user) {
        clearAllUserData();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, user.getUserName());
        editor.putString(keyUserFBUid, user.getFb_uid());
        editor.putString(keyUserGender, user.getGender());
        editor.putString(keyUserFBPic, user.getFb_pic());
        editor.putString(keyUserFBEmail, user.getEmail());
        editor.apply();
    }

    public static void saveMmwUserData(String emailText) {
        clearAllUserData();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, emailText);
        editor.putString(keyUserFBEmail, emailText);
        editor.apply();
    }

    public static void saveUserStoreData(Store theStore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(keyStoreId, theStore.getId());
        editor.putString(keyStoreCode, theStore.getStoreCode());
        editor.putString(keyStoreName, theStore.getName());
        editor.putString(keyStoreAddress, theStore.getAddress());
        editor.apply();
    }

    public static Store getSavedStore() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String storeName = prefs.getString(keyStoreName, "");
        if (storeName.contains("7-11")) {
            String storeCode = prefs.getString(keyStoreCode, "");
            String storeAddress = prefs.getString(keyStoreAddress, "");
            int storeId = prefs.getInt(keyStoreId, -1);
            return new Store(storeId, storeCode, storeName, storeAddress);
        } else {
            return null;
        }
    }

    public static void saveUserShippingNameAndPhone(String user_real_name, String user_phone) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserPhone, user_phone);
        editor.putString(keyUserRealName, user_real_name);
        editor.apply();
    }

    public static String getShippingName() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(keyUserRealName, "");
    }

    public static String getEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(keyUserFBEmail, "");
    }

    public static String getShippingPhone() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(keyUserPhone, "");
    }

    public static void clearAllUserData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, "");
        editor.putString(keyUserFBUid, "");
        editor.putString(keyUserGender, "");
        editor.putString(keyUserFBPic, "");
        editor.putString(keyUserFBEmail, "");
        editor.apply();
    }

    public static boolean checkIsLogIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUserName = prefs.getString(keyUserName, "");
        return !savedUserName.equals("");
    }

    public static boolean checkIsFirstAddShoppingCar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(keyFirstAddShoppingCar, true);
    }

    public static void setKownShoppingCar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(keyFirstAddShoppingCar, false).apply();
    }

    public static User getSavedUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userName = prefs.getString(keyUserName, "");
        String realName = prefs.getString(keyUserRealName, "");
        String gender = prefs.getString(keyUserGender, "");
        String phone = prefs.getString(keyUserPhone, "");
        String address = prefs.getString(keyUserAddress, "");
        String fb_uid = prefs.getString(keyUserFBUid, "");
        String fb_pic = prefs.getString(keyUserFBPic, "");
        String email = prefs.getString(keyUserFBEmail, "");
        return new User(userName, realName, gender, phone, address, fb_uid, fb_pic, email);
    }

    public static void saveAndroidVersion(String versionName, boolean upToDate) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_STRING_VERSION_NAME, versionName);
        editor.putBoolean(KEY_BOOLEAN_VERSION_UP_TO_DATE, upToDate);
        editor.apply();
    }

    public static boolean isUpToDate() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_BOOLEAN_VERSION_UP_TO_DATE, false);
    }

    public static String getVersionName() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        return prefs.getString(KEY_STRING_VERSION_NAME, "");
    }

    public static int getNotUpdateTimes() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_INT_NOT_UPDATE_TIMES, 0);
    }

    public static void resetNotUpdateTimes() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_INT_NOT_UPDATE_TIMES, 0);
        editor.apply();
    }

    public static void addNotUpdateTimes() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_INT_NOT_UPDATE_TIMES, prefs.getInt(KEY_INT_NOT_UPDATE_TIMES, 0) + 1);
        editor.apply();
    }
}
