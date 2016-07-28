package com.kosbrother.mongmongwoo.entity.user;

import com.google.gson.annotations.SerializedName;

public class UserIdEntity {
    @SerializedName("user_id")
    private int userId;

    public int getUserId() {
        return userId;
    }
}
