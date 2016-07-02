package com.kosbrother.mongmongwoo.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class BaseLoginActivity extends AppCompatActivity {

    public static final String EXTRA_STRING_ERROR_MESSAGE = "EXTRA_STRING_ERROR_MESSAGE";
    public static final String EXTRA_STRING_EMAIL = "EXTRA_STRING_EMAIL";

    public void resultOkThenFinish(String email) {
        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_STRING_EMAIL, email);
        setResult(RESULT_OK, resultData);
        finish();
    }

    public void resultOkThenFinish() {
        setResult(RESULT_OK);
        finish();
    }

    public void resultCancelThenFinish(String errorMessage) {
        Intent data = new Intent();
        data.putExtra(EXTRA_STRING_ERROR_MESSAGE, errorMessage);
        setResult(RESULT_CANCELED, data);
        finish();
    }

    protected void resultCancelThenFinish() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
