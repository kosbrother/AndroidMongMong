package com.kosbrother.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.User;

public class Settings {

    public static final int GUEST_USER_ID = 31;

    private static final String PREFS_USER_DATA = "USER_DATA";

    private final static String keyUserName = "USER_NAME";
    private final static String keyUserFBUid = "USER_UID";
    private final static String keyUserGender = "USER_GENDER";

    private final static String keyUserFBPic = "USER_PIC";
    private final static String keyUserFBEmail = "USER_EMAIL";
    private final static String keyUserProvider = "USER_PROVIDER";
    private final static String keyUserId = "USER_ID";

    private static final String PREFS_VERSION = "PREFS_VERSION";

    private static final String KEY_STRING_VERSION_NAME = "KEY_STRING_VERSION_NAME";
    private static final String KEY_BOOLEAN_VERSION_UP_TO_DATE = "KEY_BOOLEAN_VERSION_UP_TO_DATE";
    private static final String KEY_INT_NOT_UPDATE_TIMES = "KEY_INT_NOT_UPDATE_TIMES";

    private static final String PREFS_TEMP_DATA = "PREFS_TEMP_DATA";

    private static final String TEMP_DATA_STRING_SHIP_DETAIL_ADDRESS = "TEMP_DATA_STRING_SHIP_DETAIL_ADDRESS";
    private static final String TEMP_DATA_STRING_SHIP_NAME = "TEMP_DATA_STRING_SHIP_NAME";
    private static final String TEMP_DATA_STRING_SHIP_PHONE = "TEMP_DATA_STRING_SHIP_PHONE";
    private static final String TEMP_DATA_STRING_SHIP_EMAIL = "TEMP_DATA_STRING_SHIP_EMAIL";

    private final static String TEMP_DATA_INT_STORE_ID = "TEMP_DATA_INT_STORE_ID";
    private final static String TEMP_DATA_INT_STORE_CODE = "TEMP_DATA_INT_STORE_CODE";
    private final static String TEMP_DATA_STRING_STORE_NAME = "TEMP_DATA_STRING_STORE_NAME";
    private final static String TEMP_DATA_STRING_STORE_ADDRESS = "TEMP_DATA_STRING_STORE_ADDRESS";
    private final static String TEMP_DATA_STRING_STORE_PHONE = "TEMP_DATA_STRING_STORE_PHONE";

    private final static String TEMP_DATA_BOOLEAN_FIRST_ADD_SHOPPING_CAR =
            "TEMP_DATA_BOOLEAN_FIRST_ADD_SHOPPING_CAR";

    private static Context context;

    public static void init(Context context) {
        Settings.context = context;
    }

    public static void saveUserData(User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, user.getUserName());
        editor.putString(keyUserFBUid, user.getUid());
        editor.putString(keyUserGender, user.getGender());
        editor.putString(keyUserFBPic, user.getFbPic());
        editor.putString(keyUserFBEmail, user.getEmail());
        editor.putString(keyUserProvider, user.getProvider());
        editor.putInt(keyUserId, user.getUserId());
        editor.apply();
    }

    public static String getEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER_DATA, Context.MODE_PRIVATE);
        return prefs.getString(keyUserFBEmail, "");
    }

    public static void clearAllUserData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }

    public static boolean checkIsLogIn() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER_DATA, Context.MODE_PRIVATE);
        int userId = prefs.getInt(keyUserId, GUEST_USER_ID);
        String provider = prefs.getString(keyUserProvider, "");
        if (userId == GUEST_USER_ID || provider.equals("")) {
            clearAllUserData();
            return false;
        }
        return true;
    }

    public static User getSavedUser() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER_DATA, Context.MODE_PRIVATE);
        String provider = prefs.getString(keyUserProvider, "");
        int userId = prefs.getInt(keyUserId, GUEST_USER_ID);
        String userName = prefs.getString(keyUserName, "");
        String gender = prefs.getString(keyUserGender, "");
        String fb_uid = prefs.getString(keyUserFBUid, "");
        String fb_pic = prefs.getString(keyUserFBPic, "");
        String email = prefs.getString(keyUserFBEmail, "");

        User user = new User(userName, gender, fb_uid, fb_pic, email, provider);
        user.setUserId(userId);
        return user;
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

    public static void saveTempStoreData(Store theStore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TEMP_DATA_INT_STORE_ID, theStore.getId());
        editor.putString(TEMP_DATA_INT_STORE_CODE, theStore.getStoreCode());
        editor.putString(TEMP_DATA_STRING_STORE_NAME, theStore.getName());
        editor.putString(TEMP_DATA_STRING_STORE_ADDRESS, theStore.getAddress());
        editor.putString(TEMP_DATA_STRING_STORE_PHONE, theStore.getPhone());
        editor.apply();
    }

    public static Store getSavedStore() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        String storeName = prefs.getString(TEMP_DATA_STRING_STORE_NAME, "選擇配送門市");
        String storePhone = prefs.getString(TEMP_DATA_STRING_STORE_PHONE, "");
        String storeCode = prefs.getString(TEMP_DATA_INT_STORE_CODE, "");
        String storeAddress = prefs.getString(TEMP_DATA_STRING_STORE_ADDRESS, "");
        int storeId = prefs.getInt(TEMP_DATA_INT_STORE_ID, 0);
        return new Store(storeId, storeCode, storeName, storeAddress, storePhone);

    }

    public static void saveTempUserShipDetailAddress(String shipDetailAddress) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(TEMP_DATA_STRING_SHIP_DETAIL_ADDRESS, shipDetailAddress)
                .apply();
    }

    public static void saveTempUserShipInfo(String shipName, String shipPhone, String shipEmail) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(TEMP_DATA_STRING_SHIP_NAME, shipName)
                .putString(TEMP_DATA_STRING_SHIP_PHONE, shipPhone)
                .putString(TEMP_DATA_STRING_SHIP_EMAIL, shipEmail)
                .apply();
    }

    public static String getShippingAddress() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        return prefs.getString(TEMP_DATA_STRING_SHIP_DETAIL_ADDRESS, "");
    }

    public static String getShipName() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        return prefs.getString(TEMP_DATA_STRING_SHIP_NAME, "");
    }

    public static String getShipPhone() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        return prefs.getString(TEMP_DATA_STRING_SHIP_PHONE, "");
    }

    public static String getShipEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        return prefs.getString(TEMP_DATA_STRING_SHIP_EMAIL, "");
    }

    public static boolean checkIsFirstAddShoppingCar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        return prefs.getBoolean(TEMP_DATA_BOOLEAN_FIRST_ADD_SHOPPING_CAR, true);
    }

    public static void setKnowShoppingCar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TEMP_DATA, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(TEMP_DATA_BOOLEAN_FIRST_ADD_SHOPPING_CAR, false).apply();
    }
}
