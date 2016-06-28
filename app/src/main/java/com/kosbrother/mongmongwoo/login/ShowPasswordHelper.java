package com.kosbrother.mongmongwoo.login;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class ShowPasswordHelper implements View.OnClickListener {
    private CheckBox checkBox;
    private EditText passwordEditText;

    public void setShowPasswordBehavior(
            View showPasswordLayout, CheckBox checkBox, EditText passwordEditText) {
        this.checkBox = checkBox;
        this.passwordEditText = passwordEditText;
        showPasswordLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        checkBox.toggle();

        int start = passwordEditText.getSelectionStart();
        int end = passwordEditText.getSelectionEnd();
        if (checkBox.isChecked()) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
        passwordEditText.setSelection(start, end);
    }
}
