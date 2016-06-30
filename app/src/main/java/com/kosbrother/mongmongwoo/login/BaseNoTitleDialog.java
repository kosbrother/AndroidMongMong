package com.kosbrother.mongmongwoo.login;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class BaseNoTitleDialog extends Dialog{
    public BaseNoTitleDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public BaseNoTitleDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected BaseNoTitleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
