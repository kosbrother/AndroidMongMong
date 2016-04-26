package com.kosbrother.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.User;

/**
 * Created by kolichung on 3/15/16.
 */
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

    public static void saveUserFBData(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, user.getUser_name());
        editor.putString(keyUserFBUid, user.getFb_uid());
        editor.putString(keyUserGender, user.getGender());
        editor.putString(keyUserFBPic, user.getFb_pic());
        editor.putString(keyUserFBEmail, user.getEmail());
        editor.apply();
    }

    public static void saveUserStoreData(Context context, Store theStore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(keyStoreId, theStore.getStore_id());
        editor.putString(keyStoreCode, theStore.getStore_code());
        editor.putString(keyStoreName, theStore.getName());
        editor.putString(keyStoreAddress, theStore.getAddress());
        editor.apply();
    }

    public static Store getSavedStore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int storeId = prefs.getInt(keyStoreId, -1);
        if (storeId == -1) {
            return null;
        } else {
            String storeCode = prefs.getString(keyStoreCode, "");
            String storeName = prefs.getString(keyStoreName, "");
            String storeAddress = prefs.getString(keyStoreAddress, "");
            return new Store(storeId, storeCode, storeName, storeAddress);
        }
    }

    public static void saveUserShippingNameAndPhone(Context context, String user_real_name, String user_phone) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserPhone, user_phone);
        editor.putString(keyUserRealName, user_real_name);
        editor.apply();
    }

    public static String getShippingName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(keyUserRealName, "");
    }

    public static String getShippingPhone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(keyUserPhone, "");
    }

    public static void clearAllUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, "");
        editor.putString(keyUserFBUid, "");
        editor.putString(keyUserGender, "");
        editor.putString(keyUserFBPic, "");
        editor.putString(keyUserFBEmail, "");
        editor.apply();
    }

    public static boolean checkIsLogIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUserName = prefs.getString(keyUserName, "");
        return !savedUserName.equals("");
    }

    public static boolean checkIsFirstAddShoppingCar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(keyFirstAddShoppingCar, true);
    }

    public static void setKownShoppingCar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(keyFirstAddShoppingCar, false).apply();
    }

    public static User getSavedUser(Context context) {
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

    public static void saveAndroidVersion(Context context, String versionName, boolean upToDate) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_STRING_VERSION_NAME, versionName);
        editor.putBoolean(KEY_BOOLEAN_VERSION_UP_TO_DATE, upToDate);
        editor.apply();
    }

    public static boolean isUpToDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_BOOLEAN_VERSION_UP_TO_DATE, false);
    }

    public static String getVersionName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_VERSION, Context.MODE_PRIVATE);
        return prefs.getString(KEY_STRING_VERSION_NAME, "");
    }
}
