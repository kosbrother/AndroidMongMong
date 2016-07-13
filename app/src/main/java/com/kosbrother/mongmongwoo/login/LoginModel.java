package com.kosbrother.mongmongwoo.login;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.model.User;

import rx.functions.Action1;

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

    public void requestMmwLogin(Action1<? super ResponseEntity<String>> onLoginNextAction) {
        Webservice.login(getEmail(), getPassword(), onLoginNextAction);
    }

    public void saveMmwUserData() {
        String email = getEmail();
        User user = new User(email, "", "", "", email, "mmw");
        Settings.saveUserData(user);
    }

    public void checkLoginData(EmailPasswordChecker.OnCheckResultListener listener) {
        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(getEmail(), getPassword(), listener);
    }
}
