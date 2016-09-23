package com.kosbrother.mongmongwoo.checkout;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

public class MpgActivity extends BaseActivity implements DataManager.ApiCallBack {

    public static final String EXTRA_BYTE_ARRAY_POST_DATA = "EXTRA_BYTE_ARRAY_POST_DATA";
    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";

    private String urlPath = BuildConfig.DEBUG ?
            "https://capi.pay2go.com/MPG/mpg_gateway" : "https://api.pay2go.com/MPG/mpg_gateway";
    private CenterProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpg);
        setWebView();
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MmwAlertDialog);
            alertDialogBuilder.setTitle("確定要離開付款頁面？")
                    .setMessage("若您返回上一頁，這次的訂單將不會成立！")
                    .setNegativeButton("返回上一頁", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onConfirmCancelOrderClick();
                        }
                    })
                    .setPositiveButton("繼續付款", null);

            alertDialogBuilder.show();
        }
    }

    private void setWebView() {
        WebView webView = (WebView) findViewById(R.id.activity_mpg_wb);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("Status=SUCCESS")) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
        webView.postUrl(urlPath, getIntent().getByteArrayExtra(EXTRA_BYTE_ARRAY_POST_DATA));
    }

    @Override
    public void onError(String errorMessage) {
        progressDialog.dismiss();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        progressDialog.dismiss();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onConfirmCancelOrderClick() {
        progressDialog = CenterProgressDialog.show(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DataManager.getInstance().unSubscribe(MpgActivity.this);
            }
        });
        DataManager.getInstance().cancelOrder(getUserId(), getOrderId(), this);
    }

    private int getUserId() {
        return Settings.getSavedUser().getUserId();
    }

    private int getOrderId() {
        return getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
    }
}
