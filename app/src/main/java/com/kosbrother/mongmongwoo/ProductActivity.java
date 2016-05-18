package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidpagecontrol.PageControl;
import com.kosbrother.mongmongwoo.adpters.ProductImageFragmentPagerAdapter;
import com.kosbrother.mongmongwoo.api.ProductApi;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPromoOpenedEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCartEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCollectionEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductViewEvent;
import com.kosbrother.mongmongwoo.model.Photo;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.mycollect.MyCollectManager;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_INT_PRODUCT_ID = "EXTRA_INT_PRODUCT_ID";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";
    public static final String EXTRA_BOOLEAN_FROM_MY_COLLECT = "EXTRA_BOOLEAN_FROM_MY_COLLECT";

    private TextView loadingText;

    private Button addCarButton;
    private TextView infoText;
    private ViewPager viewPager;
    private PageControl pageControl;

    private Product theProduct;
    private MenuItem menuItem;

    private RelativeLayout spotLightShoppingCarLayout;
    private Button spotLightConfirmButton;

    private LinearLayout no_net_layout;
    private Toast toast;
    private int productId;
    private ProductStyleDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent theIntent = getIntent();
        productId = theIntent.getIntExtra(EXTRA_INT_PRODUCT_ID, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("商品資訊");

        spotLightShoppingCarLayout = (RelativeLayout) findViewById(R.id.spotlight_shopping_car_layout);
        spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
        spotLightConfirmButton = (Button) findViewById(R.id.confirm_button);
        spotLightConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
            }
        });
        no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);

        viewPager = (ViewPager) findViewById(R.id.image_pager);
        pageControl = (PageControl) findViewById(R.id.page_control);

        loadingText = (TextView) findViewById(R.id.loading_text);

        addCarButton = (Button) findViewById(R.id.product_add_car_button);
        infoText = (TextView) findViewById(R.id.product_information_text);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new ProductAddToCartEvent(theProduct.getName()));
                if (theProduct.getSpecs().size() > 0) {
                    showStyleDialog();
                } else {
                    Toast.makeText(ProductActivity.this, "樣式讀取中,請稍受再加購物車", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setCollectImageView();

        new NewsTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuItem != null) {
            ShoppingCarPreference pref = new ShoppingCarPreference();
            int count = pref.getShoppingCarItemSize(ProductActivity.this);
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.icon_shopping_car_2));
        }

        if (NetworkUtil.getConnectivityStatus(this) == 0) {
            no_net_layout.setVisibility(View.VISIBLE);
        } else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(99) == null) {
            menuItem = menu.add(0, 99, 0, "購物車");
        } else {
            menuItem = menu.findItem(99);
        }
        ShoppingCarPreference pref = new ShoppingCarPreference();
        int count = pref.getShoppingCarItemSize(ProductActivity.this);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.icon_shopping_car_2));
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shoppingCarIntent = new Intent(ProductActivity.this, ShoppingCarActivity.class);
                startActivity(shoppingCarIntent);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_search:
                Intent searchIntent = new Intent(ProductActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.action_shopping_car:
                Intent shoppingCarIntent = new Intent(ProductActivity.this, ShoppingCarActivity.class);
                startActivity(shoppingCarIntent);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_MY_COLLECT, false)) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public ArrayList<String> getImages() {
        List<Photo> photos = theProduct.getImages();
        int size = photos.size();
        ArrayList<String> images = new ArrayList<>();
        if (size == 0) {
            images.add(theProduct.getCover());
        } else {
            for (int i = 0; i < size; i++) {
                images.add(photos.get(i).getImageUrl());
            }
        }
        return images;
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            theProduct = ProductApi.getProductById(productId);
            if (theProduct != null) {
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                ProductImageFragmentPagerAdapter adapter = new ProductImageFragmentPagerAdapter(
                        getSupportFragmentManager(),
                        getImages());
                viewPager.setAdapter(adapter);
                pageControl.setViewPager(viewPager);
                infoText.setText(Html.fromHtml(theProduct.getDescription()));
                if (!theProduct.isOnShelf()) {
                    addCarButton.setText("商品已下架, 如有需要請聯絡客服");
                    addCarButton.setEnabled(false);
                }

                String productNameString = theProduct.getName();

                GAManager.sendEvent(new ProductViewEvent(productNameString));

                boolean fromNotification = getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_NOTIFICATION, false);
                if (fromNotification) {
                    GAManager.sendEvent(new NotificationPromoOpenedEvent(productNameString));
                }

                TextView nameText = (TextView) findViewById(R.id.product_name_text);
                nameText.setText(productNameString);

                TextView priceText = (TextView) findViewById(R.id.product_price_text);
                String priceString = "NT$ " + theProduct.getPrice();
                priceText.setText(priceString);
            } else {
                Toast.makeText(ProductActivity.this, "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
            }
            loadingText.setVisibility(View.GONE);
        }
    }

    public void showStyleDialog() {
        if (dialog == null) {
            dialog = new ProductStyleDialog(this, theProduct, new ProductStyleDialog.ProductStyleDialogListener() {
                @Override
                public void onFirstAddShoppingCart() {
                    showShoppingCarInstruction();
                }

                @Override
                public void onConfirmButtonClick() {
                    doIncrease();
                }
            });
        }
        dialog.showWithInitState();
    }

    public void doIncrease() {
        invalidateOptionsMenu();
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
//        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterPanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public void showShoppingCarInstruction() {
        spotLightShoppingCarLayout.setVisibility(View.VISIBLE);
    }

    private void setCollectImageView() {
        List<Product> collectList = MyCollectManager.getCollectedList(this);
        boolean collected = checkCollected(collectList);

        ImageView collectImageView = (ImageView) findViewById(R.id.collect_iv);
        collectImageView.setTag(collected);
        setCollectImageViewRes(collectImageView);
        collectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCollectImageViewClick((ImageView) v);
            }
        });
    }

    private boolean checkCollected(List<Product> collectList) {
        for (Product p : collectList) {
            if (p.getId() == productId) {
                return true;
            }
        }
        return false;
    }

    private void onCollectImageViewClick(ImageView v) {
        boolean collected = (boolean) v.getTag();

        if (collected) {
            collected = false;
            MyCollectManager.removeProductFromCollectedList(this, theProduct);
            showAToast("取消收藏");
        } else {
            collected = true;
            MyCollectManager.addProductToCollectedList(this, theProduct);
            showAToast("成功收藏");

            GAManager.sendEvent(new ProductAddToCollectionEvent(theProduct.getName()));
        }

        v.setTag(collected);
        setCollectImageViewRes(v);
    }

    private void setCollectImageViewRes(ImageView collectImageView) {
        collectImageView.setImageResource((boolean) collectImageView.getTag() ?
                R.mipmap.ic_favorite_pink_border : R.mipmap.ic_favorite_white_border);
    }

    private void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
