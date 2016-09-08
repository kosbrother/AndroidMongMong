package com.kosbrother.mongmongwoo.appindex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.kosbrother.mongmongwoo.category.CategoryActivity;
import com.kosbrother.mongmongwoo.launch.LaunchActivity;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.myshoppingpoints.MyShoppingPointsActivity;
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
    private static final String SHOPPING_POINT_CAMPAIGNS_PATTERN =
            "^android-app:[/][/]com.kosbrother.mongmongwoo[/]https[/]www.mmwooo.com[/]shopping_point_campaigns$";
    private static final String PRODUCT_FROM_APP_INDEX_URL_PATTERN =
            "^android-app:[/][/]com.kosbrother.mongmongwoo[/]https[/]www.mmwooo.com[/]categories[/](.+)[/]items[/](.+)$";
    private static final String CATEGORY_FROM_APP_INDEX_URL_PATTERN =
            "^android-app:[/][/]com.kosbrother.mongmongwoo[/]https[/]www.mmwooo.com[/]categories[/]([^?]+)(?:[?](.+))?$";

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
            Matcher shoppingPointCampaignsMatcher = Pattern.compile(SHOPPING_POINT_CAMPAIGNS_PATTERN).matcher(input);
            Matcher productFromAppIndexUrlMatcher = Pattern.compile(PRODUCT_FROM_APP_INDEX_URL_PATTERN).matcher(input);
            Matcher categoryFromAppIndexUrlMatcher = Pattern.compile(CATEGORY_FROM_APP_INDEX_URL_PATTERN).matcher(input);

            Intent indexIntent;
            if (productMatcher.matches()) {
                indexIntent = getProductIntent(productMatcher);
            } else if (productFromAppIndexUrlMatcher.matches()) {
                indexIntent = getProductIntent(productFromAppIndexUrlMatcher);
            } else if (categoryMatcher.matches()) {
                indexIntent = getCategoryIntent(categoryMatcher);
            } else if (categoryFromAppIndexUrlMatcher.matches()) {
                indexIntent = getCategoryIntent(categoryFromAppIndexUrlMatcher);
            } else if (shoppingPointCampaignsMatcher.matches()) {
                indexIntent = new Intent(this, MyShoppingPointsActivity.class);
                indexIntent.putExtra(MyShoppingPointsActivity.EXTRA_BOOLEAN_CAMPAIGN_PAGE, true);
            } else {
                indexIntent = new Intent(this, LaunchActivity.class);
            }
            startActivity(indexIntent);
            finish();
        }
    }

    @NonNull
    private Intent getProductIntent(Matcher productMatcher) {
        Intent indexIntent = new Intent(this, ProductActivity.class);

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
        return indexIntent;
    }

    @NonNull
    private Intent getCategoryIntent(Matcher categoryMatcher) {
        String categoryName = getDecodeString(categoryMatcher.group(1));
        String sortName = getCategorySortName(categoryMatcher.group(2));

        Intent indexIntent = new Intent(this, CategoryActivity.class);
        indexIntent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
        indexIntent.putExtra(CategoryActivity.EXTRA_STRING_SORT_NAME, sortName);
        indexIntent.putExtra(CategoryActivity.EXTRA_BOOLEAN_FROM_INDEX_ACTIVITY, true);
        return indexIntent;
    }

    private String getCategorySortName(String queryString) {
        String sortName = Category.SortName.popular.name();
        if (queryString == null) {
            return sortName;
        }

        String[] queryArray = queryString.split("&");
        for (String q : queryArray) {
            if (q.contains("sort")) {
                sortName = q.split("=")[1];
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
