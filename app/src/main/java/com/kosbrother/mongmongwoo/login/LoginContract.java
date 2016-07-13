package com.kosbrother.mongmongwoo.login;

public interface LoginContract {

    interface View {

        void startGoogleSignInActivityForResult();

        void startFacebookLoginActivityForResult();

        void showRegisterDialog();

        void showForgetDialog();

        void showToast(String message);

        void showProgressDialog();

        void hideProgressDialog();

        void resultOkThenFinish(String email);
    }

    interface Presenter {

        void onMmwRegisterClick();

        void onForgetPasswordClick();

        void onMmwLoginClick();

        void onGoogleSignInClick();

        void onFacebookLoginClick();

        void onSignInResultOK(String email);

        void onSignInResultError(String errorMessage);
    }
}
