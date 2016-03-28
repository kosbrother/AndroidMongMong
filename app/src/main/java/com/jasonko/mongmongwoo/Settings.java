package com.jasonko.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.jasonko.mongmongwoo.model.User;

/**
 * Created by kolichung on 3/15/16.
 */
public class Settings {

    public static final String PREFS_NAME = "USER_DATA";

    final static String keyUserName = "USER_NAME";
    final static String keyUserFBUid = "USER_UID";
    final static String keyUserGender = "USER_GENDER";
    final static String keyUserRealName = "USER_REAL_NAME";
    final static String keyUserPhone = "USER_PHONE";
    final static String keyUserAddress = "USER_ADDRESS";
    final static String keyUserFBPic = "USER_PIC";

    public static void saveUserFBData(Context context, User user){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, user.getUser_name());
        editor.putString(keyUserFBUid, user.getFb_uid());
        editor.putString(keyUserGender, user.getGender());
        editor.putString(keyUserFBPic, user.getFb_pic());
        editor.commit();
    }

    public static void saveUserRealNameAndPhone(Context context, String user_real_name, String user_phone){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserPhone, user_phone);
        editor.putString(keyUserRealName, user_real_name);
        editor.commit();
    }

    public static void saveUserAddress(Context context, String user_address){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserAddress, user_address);
        editor.commit();
    }

    public static void clearAllUserData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserName, "");
        editor.putString(keyUserFBUid, "");
        editor.putString(keyUserGender, "");
        editor.putString(keyUserFBPic, "");
        editor.putString(keyUserPhone, "");
        editor.putString(keyUserRealName, "");
        editor.putString(keyUserAddress, "");
        editor.commit();
    }

    public static boolean checkIsLogIn(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUserName = prefs.getString(keyUserName,"");
        if (savedUserName.equals("")){
            return false;
        }else {
            return true;
        }
    }

    public static User getSavedUser(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userName = prefs.getString(keyUserName, "");
        String realName = prefs.getString(keyUserRealName, "");
        String gender = prefs.getString(keyUserGender, "");
        String phone = prefs.getString(keyUserPhone, "");
        String address = prefs.getString(keyUserAddress, "");
        String fb_uid = prefs.getString(keyUserFBUid,"");
        String fb_pic = prefs.getString(keyUserFBPic,"");
        User theUser = new User(userName,realName,gender,phone,address,fb_uid,fb_pic);
        return theUser;
    }
}
