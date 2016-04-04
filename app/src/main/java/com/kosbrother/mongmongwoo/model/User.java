package com.kosbrother.mongmongwoo.model;

/**
 * Created by kolichung on 3/9/16.
 */
public class User {

    String user_name;
    String real_name;
    String gender;
    String phone;
    String address;
    String fb_uid;
    String fb_pic;

    public User(String user_name, String real_name, String gender, String phone, String address, String fb_uid, String fb_pic) {
        this.user_name = user_name;
        this.real_name = real_name;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.fb_uid = fb_uid;
        this.fb_pic = fb_pic;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getReal_name() {
        return real_name;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getFb_uid() {
        return fb_uid;
    }

    public String getFb_pic() {
        return fb_pic;
    }
}
