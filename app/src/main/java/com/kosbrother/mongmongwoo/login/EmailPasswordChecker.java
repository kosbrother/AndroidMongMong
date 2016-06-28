package com.kosbrother.mongmongwoo.login;

public class EmailPasswordChecker {

    public void check(String emailText, String passwordText, OnCheckResultListener listener) {
        String errorMessage = checkError(emailText, passwordText);
        if (errorMessage == null) {
            listener.onCheckValid();
        } else {
            listener.onCheckError(errorMessage);
        }
    }

    private String checkError(String emailText, String passwordText) {
        String errorMsg = null;
        if (emailText.isEmpty() && passwordText.isEmpty()) {
            errorMsg = "Email、密碼不可空白";
        } else if (emailText.isEmpty()) {
            errorMsg = "Email不可空白";
        } else if (passwordText.isEmpty()) {
            errorMsg = "密碼不可空白";
        }
        return errorMsg;
    }

    public interface OnCheckResultListener {

        void onCheckValid();

        void onCheckError(String errorMessage);
    }
}
