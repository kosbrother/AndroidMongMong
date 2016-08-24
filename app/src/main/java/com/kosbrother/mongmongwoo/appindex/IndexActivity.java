package com.kosbrother.mongmongwoo.appindex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.kosbrother.mongmongwoo.category.CategoryActivity;
import com.kosbrother.mongmongwoo.launch.LaunchActivity;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.product.ProductActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexActivity extends AppCompatActivity {

    private static final String PRODUCT_PATTERN =
            "^https:[/][/]www.mmwooo.com[/]categories[/](.+)[/]items[/](.+)$";
    private static final String CATEGORY_PATTERN =
            "^https:[/][/]www.mmwooo.com[/]categories[/]([^?]+)(?:[?](.+))?$";

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
            String input = data.toString();
            Matcher productMatcher = Pattern.compile(PRODUCT_PATTERN).matcher(input);
            Matcher categoryMatcher = Pattern.compile(CATEGORY_PATTERN).matcher(input);

            Intent indexIntent;
            if (productMatcher.matches()) {
                indexIntent = new Intent(this, ProductActivity.class);

                String categoriesData = productMatcher.group(1);
                String itemsData = productMatcher.group(2);
                if (TextUtils.isDigitsOnly(categoriesData) && TextUtils.isDigitsOnly(itemsData)) {
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_INT_CATEGORY_ID, Integer.parseInt(categoriesData));
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_INT_PRODUCT_ID, Integer.parseInt(itemsData));
                } else {
                    String categoryName = getDecodeString(categoriesData);
                    String slug = getDecodeString(itemsData);
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
                    indexIntent.putExtra(
                            ProductActivity.EXTRA_STRING_SLUG, slug);
                }
                indexIntent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_APP_INDEX, true);
            } else if (categoryMatcher.matches()) {
                String categoryName = getDecodeString(categoryMatcher.group(1));
                String sortName = getCategorySortName(categoryMatcher.group(2));

                indexIntent = new Intent(this, CategoryActivity.class);
                indexIntent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
                indexIntent.putExtra(CategoryActivity.EXTRA_STRING_SORT_NAME, sortName);
                indexIntent.putExtra(CategoryActivity.EXTRA_BOOLEAN_FROM_INDEX_ACTIVITY, true);
            } else {
                indexIntent = new Intent(this, LaunchActivity.class);
            }
            startActivity(indexIntent);
            finish();
        }
    }

    private String getCategorySortName(String queryString) {
        String sortName = Category.SortName.popular.name();
        if (queryString == null) {
            return sortName;
        }
        if (queryString.contains("&")) {
            String[] queryArray = queryString.split("&");
            for (String q : queryArray) {
                if (q.contains("sort")) {
                    sortName = q.split("=")[1];
                }
            }
        } else {
            if (queryString.contains("sort")) {
                sortName = queryString.split("=")[1];
            }
        }
        return sortName;
    }

    private String getDecodeString(String categoryNameString) {
        String decodeString = categoryNameString;
        try {
            decodeString = URLDecoder.decode(categoryNameString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodeString;
    }

}
