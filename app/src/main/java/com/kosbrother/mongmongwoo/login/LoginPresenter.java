package com.kosbrother.mongmongwoo.login;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.model.User;

import rx.functions.Action1;

public class LoginPresenter implements LoginContract.Presenter {

    private static final int REQUEST_GOOGLE_SIGN_IN = 9001;
    private static final int REQUEST_FACEBOOK_SIGN_IN = REQUEST_GOOGLE_SIGN_IN + 1;

    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
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
    public void onMmwLoginClick(final String email, final String password) {
        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(email, password, new EmailPasswordChecker.OnCheckResultListener() {
            @Override
            public void onCheckValid() {
                onLoginInputValid(email, password);
            }

            @Override
            public void onCheckError(String errorMessage) {
                view.showToast(errorMessage);
            }
        });
    }

    @Override
    public void onGoogleSignInClick() {
        view.startGoogleSignInActivityForResult(REQUEST_GOOGLE_SIGN_IN);
    }

    @Override
    public void onFacebookLoginClick() {
        view.startFacebookLoginActivityForResult(REQUEST_FACEBOOK_SIGN_IN);
    }

    @Override
    public void onSignInResultOK(String email) {
        view.resultOkThenFinish(email);
    }

    @Override
    public void onSignInResultError(String errorMessage) {
        view.resultCancelThenFinish(errorMessage);
    }

    private void onLoginInputValid(final String email, String password) {
        Webservice.login(email, password, new Action1<ResponseEntity<String>>() {
            @Override
            public void call(ResponseEntity<String> stringResponseEntity) {
                String data = stringResponseEntity.getData();
                if (data != null) {
                    User user = new User(email, "", "", "", email, "mmw");
                    Settings.saveUserData(user);
                    view.resultOkThenFinish(email);
                } else {
                    String errorMessage = stringResponseEntity.getError().getMessage();
                    view.showToast(errorMessage);
                }
            }
        });
    }

}
