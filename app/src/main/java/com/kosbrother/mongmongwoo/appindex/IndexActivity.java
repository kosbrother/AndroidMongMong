package com.kosbrother.mongmongwoo.appindex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.launch.LaunchActivity;
import com.kosbrother.mongmongwoo.utils.InitUtil;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitUtil.initApp(getApplicationContext(), getApplication());
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String dataString = data.getEncodedPath();
            Intent indexIntent;
            if (dataString.contains("categories") && dataString.contains("items")) {
                indexIntent = new Intent(this, ProductActivity.class);

                String categoriesData = dataString.substring(
                        dataString.indexOf("categories/") + 11, dataString.indexOf("/items"));
                String itemsData = dataString.substring(dataString.indexOf("items/") + 6);
                if (TextUtils.isDigitsOnly(categoriesData) && TextUtils.isDigitsOnly(itemsData)) {
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_INT_CATEGORY_ID, Integer.parseInt(categoriesData));
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_INT_PRODUCT_ID, Integer.parseInt(itemsData));
                } else {
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoriesData);
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_STRING_SLUG, itemsData);
                }
                indexIntent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_APP_INDEX, true);
            } else {
                indexIntent = new Intent(this, LaunchActivity.class);
            }
            startActivity(indexIntent);
            finish();
        }
    }

}
