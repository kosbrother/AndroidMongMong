package com.kosbrother.mongmongwoo.appindex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.ProductActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            if (dataString.contains("categories")) {
                String categoryName = dataString.substring(
                        dataString.indexOf("categories/") + 11, dataString.indexOf("/items"));
                int productId = Integer.parseInt(dataString.substring(dataString.lastIndexOf("-") + 1));

                indexIntent = new Intent(this, ProductActivity.class);
                indexIntent.putExtra(
                        ProductActivity.EXTRA_INT_PRODUCT_ID, productId);
                indexIntent.putExtra(
                        ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
            } else {
                indexIntent = new Intent(this, MainActivity.class);
            }
            startActivity(indexIntent);
            finish();
        }
    }

}
