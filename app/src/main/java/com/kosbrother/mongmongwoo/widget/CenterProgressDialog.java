package com.kosbrother.mongmongwoo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kosbrother.mongmongwoo.R;

public class CenterProgressDialog extends Dialog {

    public static CenterProgressDialog show(Context context) {
        return show(context, null, null, true, true);
    }

    public static CenterProgressDialog show(Context context, CharSequence title,
                                            CharSequence message) {
        return show(context, title, message, false);
    }

    public static CenterProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static CenterProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static CenterProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate,
                                            boolean cancelable, OnCancelListener cancelListener) {
        CenterProgressDialog dialog = new CenterProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public CenterProgressDialog(Context context) {
        super(context, R.style.CenterProgressDialog);
    }
}
