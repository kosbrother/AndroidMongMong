package com.kosbrother.mongmongwoo.login;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.model.User;

public class LoginModel {
    private final LoginUser loginUser;

    public LoginModel(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public String getEmail() {
        return loginUser.getEmail();
    }

    public String getPassword() {
        return loginUser.getPassword();
    }

    public void requestMmwLogin(DataManager.ApiCallBack callBack) {
        String registrationId = FirebaseInstanceId.getInstance().getToken();
        DataManager.getInstance().login(getEmail(), getPassword(), registrationId, callBack);
    }

    public void saveMmwUserData(int userId) {
        String email = getEmail();
        User user = new User(email, "", "", "", email, "mmw");
        user.setUserId(userId);
        Settings.saveUserData(user);
    }

    public void checkLoginData(EmailPasswordChecker.OnCheckResultListener listener) {
        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(getEmail(), getPassword(), listener);
    }

    public void unSubscribe(DataManager.ApiCallBack callBack) {
        DataManager.getInstance().unSubscribe(callBack);
    }
}
