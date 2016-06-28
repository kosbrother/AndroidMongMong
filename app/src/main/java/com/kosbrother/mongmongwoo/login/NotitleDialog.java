package com.kosbrother.mongmongwoo.login;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class NoTitleDialog extends Dialog{
    public NoTitleDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public NoTitleDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected NoTitleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
