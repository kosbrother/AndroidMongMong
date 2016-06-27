package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

public class ShopInfoEntity {
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
