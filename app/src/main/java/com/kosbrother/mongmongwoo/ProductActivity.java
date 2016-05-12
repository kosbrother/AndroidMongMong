package com.kosbrother.mongmongwoo;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidpagecontrol.PageControl;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kosbrother.mongmongwoo.adpters.ProductImageFragmentPagerAdapter;
import com.kosbrother.mongmongwoo.adpters.StyleGridAdapter;
import com.kosbrother.mongmongwoo.api.ProductApi;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ProductImage;
import com.kosbrother.mongmongwoo.model.ProductSpec;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    public static final String BOOLEAN_EXTRA_FROM_NOTIFICATION = "BOOLEAN_EXTRA_FROM_NOTIFICATION";
    public static final String EXTRA_SERIALIZABLE_PRODUCT = "EXTRA_SERIALIZABLE_PRODUCT";
    public static final String EXTRA_BOOLEAN_FROM_MY_COLLECT = "EXTRA_BOOLEAN_FROM_MY_COLLECT";

    TextView nameText;
    TextView priceText;
    TextView loadingText;

    Button addCarButton;
    TextView infoText;
    private ViewPager viewPager;
    private PageControl pageControl;

    Product theProduct;
    private StyleGridAdapter styleGridAdapter;
    MenuItem menuItem;

    RelativeLayout spotLightShoppingCarLayout;
    Button spotLightConfirmButton;

    LinearLayout no_net_layout;
    Tracker mTracker;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Intent theIntent = getIntent();
        theProduct = (Product) theIntent.getSerializableExtra(EXTRA_SERIALIZABLE_PRODUCT);
        boolean fromNotification = theIntent.getBooleanExtra(BOOLEAN_EXTRA_FROM_NOTIFICATION, false);

        if (fromNotification) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("PRODUCT")
                    .setAction("CLICK_NOTIFICATION")
                    .setLabel(theProduct.getName())
                    .build());
        }

        mTracker.setScreenName("Product Name " + theProduct.getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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
        nameText = (TextView) findViewById(R.id.product_name_text);
        priceText = (TextView) findViewById(R.id.product_price_text);
        addCarButton = (Button) findViewById(R.id.product_add_car_button);
        infoText = (TextView) findViewById(R.id.product_information_text);

        nameText.setText(theProduct.getName());
        priceText.setText("NT$ " + Integer.toString(theProduct.getPrice()));
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        ArrayList<ProductImage> productImages = theProduct.getImages();
        int size = productImages.size();
        ArrayList<String> images = new ArrayList<>();
        if (size == 0) {
            images.add(theProduct.getPic_url());
        } else {
            for (int i = 0; i < size; i++) {
                images.add(productImages.get(i).getUrl());
            }
        }
        return images;
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            theProduct = ProductApi.updateProductById(theProduct.getId(), theProduct);
            if (theProduct != null) {
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            loadingText.setVisibility(View.GONE);
            if (result != null) {
                ProductImageFragmentPagerAdapter adapter = new ProductImageFragmentPagerAdapter(
                        getSupportFragmentManager(),
                        getImages());
                viewPager.setAdapter(adapter);
                pageControl.setViewPager(viewPager);
                infoText.setText(Html.fromHtml(theProduct.getDescription()));
            } else {
                Toast.makeText(ProductActivity.this, "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private GridView styleGrid;
    private ImageView styleImage;
    private TextView styleName;
    private int tempCount;

    public void showStyleDialog() {

        View view = LayoutInflater.from(ProductActivity.this)
                .inflate(R.layout.dialog_add_shopping_car_item, null, false);
        styleGrid = (GridView) view.findViewById(R.id.dialog_styles_gridview);
        styleImage = (ImageView) view.findViewById(R.id.dialog_style_image);
        styleName = (TextView) view.findViewById(R.id.dialog_style_name);

        Button style_confirm_button = (Button) view.findViewById(R.id.dialog_style_confirm_button);

        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(Integer.toString(tempCount));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(Integer.toString(tempCount));
            }
        });
        tempCount = 1;
        countText.setText(Integer.toString(tempCount));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

        style_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedStylePosition = styleGridAdapter.getSelectedPosition();
                ProductSpec theSelectedSpec = theProduct.getSpecs().get(selectedStylePosition);
                theProduct.setSelectedSpec(theSelectedSpec);
                ShoppingCarPreference pref = new ShoppingCarPreference();
                theProduct.setBuy_count(tempCount);
                pref.addShoppingItem(ProductActivity.this, theProduct);
                doIncrease();
                alertDialog.cancel();

                if (Settings.checkIsFirstAddShoppingCar()) {
                    showShoppingCarInstruction();
                    Settings.setKownShoppingCar();
                }
            }
        });

        styleGridAdapter = new StyleGridAdapter(this, theProduct.getSpecs());
        styleGrid.setAdapter(styleGridAdapter);
        styleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductSpec productSpec = theProduct.getSpecs().get(position);

                Glide.with(ProductActivity.this)
                        .load(productSpec.getPic_url())
                        .centerCrop()
                        .placeholder(R.mipmap.img_pre_load_square)
                        .into(styleImage);
                styleName.setText(productSpec.getStyle());

                styleGridAdapter.updateSelectedPosition(position);
            }
        });

        Glide.with(this)
                .load(theProduct.getSpecs().get(0).getPic_url())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(styleImage);
        styleName.setText(theProduct.getSpecs().get(0).getStyle());

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
            if (p.getId() == theProduct.getId()) {
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
