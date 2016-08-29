package com.kosbrother.mongmongwoo.login;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

public class ForgetDialog extends BaseNoTitleDialog implements
        View.OnClickListener,
        DataManager.ApiCallBack,
        DialogInterface.OnCancelListener {

    private CenterProgressDialog progressDialog;

    public ForgetDialog(Context context) {
        super(context);
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
            progressDialog = CenterProgressDialog.show(getContext(), this);
            DataManager.getInstance().forget(emailText, this);
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        DataManager.getInstance().unSubscribe(this);
    }

    @Override
    public void onError(String errorMessage) {
        progressDialog.dismiss();
        progressDialog = null;

        showToast(errorMessage);
    }

    @Override
    public void onSuccess(Object data) {
        progressDialog.dismiss();
        progressDialog = null;

        setContentView(R.layout.dialog_forget_send);
        findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
