package com.kosbrother.mongmongwoo.login;

import com.kosbrother.mongmongwoo.entity.ResponseEntity;

import rx.functions.Action1;

public class LoginPresenter implements
        LoginContract.Presenter,
        EmailPasswordChecker.OnCheckResultListener,
        Action1<ResponseEntity<String>> {

    private final LoginContract.View view;
    private final LoginModel model;

    public LoginPresenter(LoginContract.View view, LoginModel model) {
        this.view = view;
        this.model = model;
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
        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(model.getEmail(), model.getPassword(), this);
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
    public void call(ResponseEntity<String> stringResponseEntity) {
        view.hideProgressDialog();
        String data = stringResponseEntity.getData();
        if (data != null) {
            model.saveMmwUserData();
            view.resultOkThenFinish(model.getEmail());
        } else {
            String errorMessage = stringResponseEntity.getError().getMessage();
            view.showToast(errorMessage);
        }
    }
}
