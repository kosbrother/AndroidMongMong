package com.kosbrother.mongmongwoo.model;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kosbrother.mongmongwoo.entity.UserEntity;

public class User extends UserEntity {

    private String fbPic;
    private int userId;

    public User(String userName, String gender, String uid, String fbPic, String email, String provider) {
        this.userName = userName;
        this.gender = gender;
        this.uid = uid;
        this.fbPic = fbPic;
        this.email = email;
        this.provider = provider;
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

    public String getProvider() {
        return provider;
    }

    public String getFbPic() {
        return fbPic;
    }

    public String getEmail() {
        return email;
    }

    public UserEntity getPostBody() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setGender(gender);
        userEntity.setUid(uid);
        userEntity.setUserName(userName);
        userEntity.setProvider(provider);
        userEntity.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
        return userEntity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
