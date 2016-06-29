package com.kosbrother.mongmongwoo.model;

import com.google.gson.Gson;
import com.kosbrother.mongmongwoo.entity.UserEntity;

public class User extends UserEntity {

    private String fbPic;

    public User(String userName, String gender, String uid, String fbPic, String email) {
        this.userName = userName;
        this.gender = gender;
        this.uid = uid;
        this.fbPic = fbPic;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getGender() {
        return gender;
    }

    public String getUid() {
        return uid;
    }

    public String getFbPic() {
        return fbPic;
    }

    public String getEmail() {
        return email;
    }

    public String getFbUserJsonString() {
        UserEntity postEntity = new UserEntity();
        postEntity.setEmail(email);
        postEntity.setGender(gender);
        postEntity.setUid(uid);
        postEntity.setUserName(userName);
        postEntity.setProvider("facebook");
        return new Gson().toJson(postEntity);
    }
}
