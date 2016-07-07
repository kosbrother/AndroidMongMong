package com.kosbrother.mongmongwoo.login;

public interface LoginContract {

    interface View {

        void startGoogleSignInActivityForResult(int requestCode);

        void startFacebookLoginActivityForResult(int requestCode);

        void showRegisterDialog();

        void showForgetDialog();

        void showToast(String message);

        void resultOkThenFinish(String email);

        void resultCancelThenFinish(String errorMessage);
    }

    interface Presenter {

        void onMmwRegisterClick();

        void onForgetPasswordClick();

        void onMmwLoginClick(String email, String password);

        void onGoogleSignInClick();

        void onFacebookLoginClick();

        void onSignInResultOK(String email);

        void onSignInResultError(String errorMessage);
    }
}
