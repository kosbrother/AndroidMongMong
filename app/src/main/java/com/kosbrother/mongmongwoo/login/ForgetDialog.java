package com.kosbrother.mongmongwoo.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;

import rx.functions.Action1;

public class ForgetDialog extends NoTitleDialog implements View.OnClickListener {

    public ForgetDialog(Context context) {
        super(context);
    }

    public ForgetDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ForgetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forget);
        // Fix dialog size problem
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.send_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText emailEditText = (EditText) findViewById(R.id.email_et);
        String emailText = emailEditText.getText().toString().trim();
        if (emailText.isEmpty()) {
            showToast("email不可空白");
        } else {
            requestForget(emailText);
        }
    }

    private void requestForget(String emailText) {
        Webservice.forget(emailText, new Action1<ResponseEntity<String>>() {
            @Override
            public void call(ResponseEntity<String> stringResponseEntity) {
                String data = stringResponseEntity.getData();
                if (data == null) {
                    showToast(stringResponseEntity.getError().getMessage());
                } else {
                    setContentView(R.layout.dialog_forget_send);
                    findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
