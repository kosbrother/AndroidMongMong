package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.appindex.AppIndexManager;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.cart.CartClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPromoOpenedEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCartEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCollectionEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductViewEvent;
import com.kosbrother.mongmongwoo.model.Photo;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.mycollect.MyCollectManager;
import com.kosbrother.mongmongwoo.utils.CustomerServiceUtil;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.utils.ShareUtil;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_INT_PRODUCT_ID = "EXTRA_INT_PRODUCT_ID";
    public static final String EXTRA_INT_CATEGORY_ID = "EXTRA_INT_CATEGORY_ID";
    public static final String EXTRA_STRING_CATEGORY_NAME = "EXTRA_STRING_CATEGORY_NAME";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";
    public static final String EXTRA_BOOLEAN_FROM_MY_COLLECT = "EXTRA_BOOLEAN_FROM_MY_COLLECT";

    private Button addCarButton;

    private Product theProduct;

    private RelativeLayout spotLightShoppingCarLayout;

    private Toast toast;
    private ProductStyleDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        AppIndexManager.init(this);
        setToolbar();
        initSpotlightShoppingCartLayout();
        initSpotLightConfirmButton();
        initAddCartButton();
        initCollectImageView();
        getProduct();
    }

    private void getProduct() {
        Webservice.getProduct(getCategoryId(), getProductId(), new Action1<ResponseEntity<Product>>() {
            @Override
            public void call(ResponseEntity<Product> listResponseEntity) {
                Product data = listResponseEntity.getData();
                if (data == null) {
                    GAManager.sendError("getProduct", listResponseEntity.getError());
                    showAToast("無法取得資料，請檢查網路連線");
                } else {
                    theProduct = data;
                    onGetProductResult();
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        LinearLayout no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);
        boolean noNetwork = NetworkUtil.getConnectivityStatus(this) == 0;
        no_net_layout.setVisibility(noNetwork ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStop() {
        if (theProduct != null) {
            String categoryName = theProduct.getCategoryName();
            if (categoryName != null && !categoryName.isEmpty()) {
                AppIndexManager.stopItemAppIndex(theProduct);
            }
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product, menu);
        MenuItem shoppingCartItem = menu.findItem(R.id.shopping_cart);
        View shoppingCartView = MenuItemCompat.getActionView(shoppingCartItem);
        shoppingCartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CartClickEvent());

                Intent shoppingCarIntent = new Intent(ProductActivity.this, ShoppingCarActivity.class);
                startActivity(shoppingCarIntent);
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.shopping_cart);
        View countView = MenuItemCompat.getActionView(item);
        TextView countTextView = (TextView) countView.findViewById(R.id.count);

        ShoppingCarPreference pref = new ShoppingCarPreference();
        int count = pref.getShoppingCarItemSize(this);
        if (count == 0) {
            countTextView.setVisibility(View.GONE);
        } else {
            countTextView.setText(String.valueOf(count));
            countTextView.setVisibility(View.VISIBLE);
        }

        if (theProduct == null) {
            return true;
        }
        if (theProduct.isShareUrlValid()) {
            menu.findItem(R.id.share).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.share:
                GAManager.sendShareItemEvent(
                        getCategoryName(), String.valueOf(theProduct.getId()), theProduct.getName());

                String text = theProduct.getUrl();
                String title = "分享商品";
                String subject = theProduct.getName();
                ShareUtil.shareText(this, title, subject, text);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        boolean fromMyCollect = getIntent()
                .getBooleanExtra(EXTRA_BOOLEAN_FROM_MY_COLLECT, false);
        if (fromMyCollect) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public ArrayList<String> getImages() {
        List<Photo> photos = theProduct.getPhotos();
        int size = photos.size();
        ArrayList<String> images = new ArrayList<>();
        if (size == 0) {
            images.add(theProduct.getCover().getUrl());
        } else {
            for (int i = 0; i < size; i++) {
                images.add(photos.get(i).getImage().getUrl());
            }
        }
        return images;
    }

    public void onLineClick(View view) {
        CustomerServiceUtil.startToLineService(this);
    }

    public void onFbClick(View view) {
        CustomerServiceUtil.startToFbService(this);
    }

    @SuppressWarnings("ConstantConditions")
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void initSpotlightShoppingCartLayout() {
        spotLightShoppingCarLayout = (RelativeLayout) findViewById(R.id.spotlight_shopping_car_layout);
        spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
    }

    @SuppressWarnings("ConstantConditions")
    private void initSpotLightConfirmButton() {
        Button spotLightConfirmButton = (Button) findViewById(R.id.confirm_button);
        spotLightConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initAddCartButton() {
        addCarButton = (Button) findViewById(R.id.product_add_car_button);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAToast("樣式讀取中,請稍後再加購物車");
            }
        });
    }

    private void showStyleDialog() {
        if (dialog == null) {
            dialog = new ProductStyleDialog(this, theProduct, new ProductStyleDialog.ProductStyleDialogListener() {
                @Override
                public void onFirstAddShoppingCart() {
                    spotLightShoppingCarLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onConfirmButtonClick() {
                    invalidateOptionsMenu();
                    showAToast("成功加入購物車");
                }
            });
        }
        dialog.showWithInitState();
    }

    @SuppressWarnings("ConstantConditions")
    private void initCollectImageView() {
        List<Product> collectList = MyCollectManager.getCollectedList(this);
        boolean collected = checkCollected(collectList);

        ImageView collectImageView = getCollectImageView();
        collectImageView.setTag(collected);
        setCollectImageViewRes(collectImageView);
    }

    private boolean checkCollected(List<Product> collectList) {
        int productId = getProductId();
        for (Product p : collectList) {
            if (p.getId() == productId) {
                return true;
            }
        }
        return false;
    }

    private void onCollectImageViewClick(ImageView v) {
        // prevent add null product
        if (theProduct == null) {
            return;
        }

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

    @SuppressWarnings("ConstantConditions")
    private void onGetProductResult() {
        GAManager.sendEvent(new ProductViewEvent(theProduct.getName()));
        startAppIndexIfCategoryNameValid();
        sendPromoOpenedEventIfFromNotification();
        setProductView();
        setViewPagerAndPageControl();
        setAddCartButton();
        setCollectImageListener();
        invalidateOptionsMenu();
    }

    private void startAppIndexIfCategoryNameValid() {
        String categoryName = getCategoryName();
        if (categoryName != null && !categoryName.isEmpty()) {
            theProduct.setCategoryName(categoryName);
            theProduct.setCategoryId(getCategoryId());
            AppIndexManager.startItemAppIndex(theProduct);
        }
    }

    private void sendPromoOpenedEventIfFromNotification() {
        boolean fromNotification = getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_NOTIFICATION, false);
        if (fromNotification) {
            GAManager.sendEvent(new NotificationPromoOpenedEvent(theProduct.getName()));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setProductView() {
        TextView nameText = (TextView) findViewById(R.id.product_name_text);
        nameText.setText(theProduct.getName());

        TextView priceTextView = (TextView) findViewById(R.id.product_price_text);
        String priceText = "NT$ " + theProduct.getFinalPrice();
        priceTextView.setText(priceText);

        TextView specialPriceTextView = (TextView) findViewById(R.id.product_special_price_tv);
        specialPriceTextView.setText(theProduct.getSpecialPriceText());
        if (theProduct.isSpecial()) {
            TextViewUtil.paintLineThroughTextView(specialPriceTextView);
        }

        TextView infoTextView = (TextView) findViewById(R.id.product_information_text);
        infoTextView.setText(Html.fromHtml(theProduct.getDescription()));
    }

    @SuppressWarnings("ConstantConditions")
    private void setViewPagerAndPageControl() {
        ProductImageFragmentPagerAdapter adapter = new ProductImageFragmentPagerAdapter(
                getSupportFragmentManager(),
                getImages());

        ViewPager viewPager = (ViewPager) findViewById(R.id.image_pager);
        viewPager.setAdapter(adapter);

        PageControl pageControl = (PageControl) findViewById(R.id.page_control);
        pageControl.setViewPager(viewPager);
    }

    private void setAddCartButton() {
        if (theProduct.isOnShelf()) {
            addCarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GAManager.sendEvent(new ProductAddToCartEvent(theProduct.getName()));
                    showStyleDialog();
                }
            });
        } else {
            addCarButton.setText("商品已下架, 如有需要請聯絡客服");
            addCarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAToast("商品已下架, 如有需要請聯絡客服");
                }
            });
        }
    }

    private void setCollectImageListener() {
        getCollectImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCollectImageViewClick((ImageView) v);
            }
        });
    }

    private void setCollectImageViewRes(ImageView collectImageView) {
        collectImageView.setImageResource((boolean) collectImageView.getTag() ?
                R.mipmap.ic_favorite_pink_border : R.mipmap.ic_favorite_white_border);
    }

    private int getProductId() {
        return getIntent().getIntExtra(EXTRA_INT_PRODUCT_ID, 0);
    }

    private int getCategoryId() {
        int categoryId = getIntent().getIntExtra(EXTRA_INT_CATEGORY_ID, 0);
        return categoryId == 0 ? 10 : categoryId;
    }

    private String getCategoryName() {
        return getIntent().getStringExtra(EXTRA_STRING_CATEGORY_NAME);
    }

    private ImageView getCollectImageView() {
        return (ImageView) findViewById(R.id.collect_iv);
    }

    private void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
