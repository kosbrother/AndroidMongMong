package com.kosbrother.mongmongwoo.login;

import com.kosbrother.mongmongwoo.api.DataManager;

public class LoginPresenter implements
        LoginContract.Presenter,
        EmailPasswordChecker.OnCheckResultListener,
        DataManager.ApiCallBack {

    private final LoginContract.View view;
    private final LoginModel model;

    public LoginPresenter(LoginContract.View view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void onDestroy() {
        view.hideProgressDialog();
        model.unSubscribe(this);
    }

    @Override
    public void onMmwRegisterClick() {
        view.showRegisterDialog();
    }

    @Override
    public void onForgetPasswordClick() {
        view.showForgetDialog();
    }

    @Override
    public void onGoogleSignInClick() {
        view.startGoogleSignInActivityForResult();
    }

    @Override
    public void onFacebookLoginClick() {
        view.startFacebookLoginActivityForResult();
    }

    @Override
    public void onMmwLoginClick() {
        model.checkLoginData(this);
    }

    @Override
    public void onCheckValid() {
        view.showProgressDialog();
        model.requestMmwLogin(this);
    }

    @Override
    public void onCheckError(String errorMessage) {
        view.showToast(errorMessage);
    }

    @Override
    public void onSignInResultOK(String email) {
        view.resultOkThenFinish(email);
    }

    @Override
    public void onSignInResultError(String errorMessage) {
        view.showToast(errorMessage);
    }

    @Override
    public void onError(String errorMessage) {
        view.hideProgressDialog();
        view.showToast(errorMessage);
    }

    @Override
    public void onSuccess(Object data) {
        view.hideProgressDialog();
        model.saveMmwUserData((Integer) data);
        view.resultOkThenFinish(model.getEmail());
    }
}
