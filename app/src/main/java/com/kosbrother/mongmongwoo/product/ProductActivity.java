package com.kosbrother.mongmongwoo.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.adpters.ProductImageFragmentPagerAdapter;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.appindex.AppIndexUtil;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.cart.CartClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPromoOpenedEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCartEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductAddToCollectionEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductViewEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.search.SearchEnterEvent;
import com.kosbrother.mongmongwoo.login.LoginActivity;
import com.kosbrother.mongmongwoo.model.Photo;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.mycollect.FavoriteManager;
import com.kosbrother.mongmongwoo.search.SearchActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.CustomerServiceUtil;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.utils.ShareUtil;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends BaseActivity {

    public static final String EXTRA_INT_PRODUCT_ID = "EXTRA_INT_PRODUCT_ID";
    public static final String EXTRA_INT_CATEGORY_ID = "EXTRA_INT_CATEGORY_ID";
    public static final String EXTRA_STRING_CATEGORY_NAME = "EXTRA_STRING_CATEGORY_NAME";
    public static final String EXTRA_STRING_SLUG = "EXTRA_STRING_SLUG";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";
    public static final String EXTRA_BOOLEAN_FROM_SEARCH = "EXTRA_BOOLEAN_FROM_SEARCH";
    public static final String EXTRA_BOOLEAN_FROM_APP_INDEX = "EXTRA_BOOLEAN_FROM_APP_INDEX";

    private static final int REQUEST_LOGIN = 222;

    private Button addCarButton;

    private Product theProduct;

    private Toast toast;
    private ProductStyleDialog dialog;
    private DataManager.ApiCallBack isProductCollectedCallBack;
    private DataManager.ApiCallBack addFavoriteItemCallBack;
    private DataManager.ApiCallBack deleteFavoriteItemCallBack;
    private DataManager.ApiCallBack getProductCallBack;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        googleApiClient = AppIndexUtil.buildAppIndexClient(this);
        setToolbar();
        initAddCartButton();
        initCollectImageView();
        getProduct();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                initCollectImageView();
            }
        }
    }

    private void getProduct() {
        getProductCallBack = new DataManager.ApiCallBack() {
            @Override
            public void onError(String errorMessage) {
                showAToast(errorMessage);
            }

            @Override
            public void onSuccess(Object data) {
                theProduct = (Product) data;
                onGetProductResult();
            }
        };

        String slug = getIntent().getStringExtra(EXTRA_STRING_SLUG);
        if (slug == null || slug.isEmpty()) {
            DataManager.getInstance().getProduct(getCategoryId(), getProductId(), getProductCallBack);
        } else {
            DataManager.getInstance().getProduct(getCategoryName(), slug, getProductCallBack);
        }
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
                AppIndexUtil.stopProductAppIndex(googleApiClient, theProduct);
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.unSubscribe(isProductCollectedCallBack);
        dataManager.unSubscribe(addFavoriteItemCallBack);
        dataManager.unSubscribe(deleteFavoriteItemCallBack);
        dataManager.unSubscribe(getProductCallBack);
        super.onDestroy();
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

        int count = ShoppingCartManager.getInstance().getShoppingCarItemSize();
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
            case R.id.share:
                GAManager.sendShareItemEvent(
                        getCategoryName(), String.valueOf(theProduct.getId()), theProduct.getName());

                String text = theProduct.getUrl();
                String title = "分享商品";
                String subject = theProduct.getName();
                ShareUtil.shareText(this, title, subject, text);
                return true;
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        boolean fromAppIndex = getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_APP_INDEX, false);
        if (isFromNotification() || fromAppIndex) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
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
                    ViewStub viewStub = (ViewStub) findViewById(R.id.shopping_car_spotlight_vs);
                    if (viewStub != null) {
                        final View spotLightShoppingCarLayout = viewStub.inflate();
                        Button spotLightConfirmButton =
                                (Button) spotLightShoppingCarLayout.findViewById(R.id.confirm_button);
                        spotLightConfirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onConfirmButtonClick(Product product) {
                    ShoppingCartManager.getInstance().addShoppingItem(product);
                    invalidateOptionsMenu();
                    showAToast("成功加入購物車");
                }

            });
        }
        dialog.showWithInitState();
    }

    @SuppressWarnings("ConstantConditions")
    private void initCollectImageView() {
        final ImageView collectImageView = getCollectImageView();
        collectImageView.setVisibility(View.GONE);

        if (Settings.checkIsLogIn()) {
            int userId = Settings.getSavedUser().getUserId();
            isProductCollectedCallBack = new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    showAToast(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    if (data instanceof Boolean) {
                        boolean collected = (boolean) data;
                        collectImageView.setTag(collected);
                        setCollectImageViewRes(collectImageView);
                        collectImageView.setVisibility(View.VISIBLE);
                    }
                }
            };
            FavoriteManager.getInstance(userId, getApplicationContext())
                    .isProductCollected(getProductId(), isProductCollectedCallBack);
        } else {
            collectImageView.setTag(false);
            setCollectImageViewRes(collectImageView);
            collectImageView.setVisibility(View.VISIBLE);
        }
    }

    private void onCollectImageViewClick(final ImageView v) {
        // prevent add null product
        if (theProduct == null) {
            return;
        }

        boolean collected = (boolean) v.getTag();

        if (!Settings.checkIsLogIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return;
        }

        FavoriteManager manager = FavoriteManager.getInstance(Settings.getSavedUser().getUserId(), getApplicationContext());
        if (collected) {
            deleteFavoriteItemCallBack = new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    showAToast(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    showAToast("取消收藏");
                    v.setTag(false);
                    setCollectImageViewRes(v);
                }
            };
            manager.deleteFavoriteItemsFromId(getProductId(), deleteFavoriteItemCallBack);
        } else {
            GAManager.sendEvent(new ProductAddToCollectionEvent(theProduct.getName()));

            addFavoriteItemCallBack = new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    showAToast(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    FacebookLogger.getInstance().logAddedToWishlistEvent(
                            String.valueOf(theProduct.getId()),
                            getCategoryName(),
                            theProduct.getName(),
                            theProduct.getFinalPrice());
                    showAToast("成功收藏");
                    v.setTag(true);
                    setCollectImageViewRes(v);
                }
            };
            manager.addFavoriteItems(theProduct, addFavoriteItemCallBack);
        }

    }

    @SuppressWarnings("ConstantConditions")
    private void onGetProductResult() {
        startAppIndexIfCategoryNameValid();

        GAManager.sendEvent(new ProductViewEvent(theProduct.getName()));
        FacebookLogger.getInstance().logViewedContentEvent(
                getCategoryName(),
                String.valueOf(theProduct.getId()),
                theProduct.getName(),
                theProduct.getFinalPrice());
        sendPromoOpenedEventIfFromNotification();
        sendSearchEnterEventIfFromSearch();

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
            AppIndexUtil.startProductAppIndex(googleApiClient, theProduct);
        }
    }

    private void sendPromoOpenedEventIfFromNotification() {
        if (isFromNotification()) {
            GAManager.sendEvent(new NotificationPromoOpenedEvent(theProduct.getName()));
        }
    }

    private void sendSearchEnterEventIfFromSearch() {
        if (getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_SEARCH, false)) {
            GAManager.sendEvent(new SearchEnterEvent(theProduct.getName()));
        }
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    private void setProductView() {
        TextView nameText = (TextView) findViewById(R.id.product_name_text);
        nameText.setText(theProduct.getName());

        TextView priceTextView = (TextView) findViewById(R.id.product_price_text);
        String priceText = "NT$ " + theProduct.getFinalPrice();
        priceTextView.setText(priceText);

        TextView originalPriceTextView = (TextView) findViewById(R.id.product_original_price_tv);
        originalPriceTextView.setText(theProduct.getOriginalPriceText());
        if (theProduct.isSpecial()) {
            TextViewUtil.paintLineThroughTextView(originalPriceTextView);
        }

        TextView infoTextView = (TextView) findViewById(R.id.product_information_text);
        Spanned infoText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            infoText = Html.fromHtml(theProduct.getDescription(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            infoText = Html.fromHtml(theProduct.getDescription());
        }
        infoTextView.setMovementMethod(LinkMovementMethod.getInstance());
        infoTextView.setText(infoText);
    }

    @SuppressWarnings("ConstantConditions")
    private void setViewPagerAndPageControl() {
        ProductImageFragmentPagerAdapter adapter = new ProductImageFragmentPagerAdapter(
                getSupportFragmentManager(),
                getImages());

        ViewPager viewPager = (ViewPager) findViewById(R.id.image_pager);
        viewPager.setAdapter(adapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    private void setAddCartButton() {
        List<Spec> specs = theProduct.getSpecs();
        if (theProduct.isOnShelf() && specs != null && specs.size() > 0) {
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
        return getIntent().getIntExtra(EXTRA_INT_CATEGORY_ID, 10);
    }

    private String getCategoryName() {
        return getIntent().getStringExtra(EXTRA_STRING_CATEGORY_NAME);
    }

    private ImageView getCollectImageView() {
        return (ImageView) findViewById(R.id.item_favorite_collect_iv);
    }

    private void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean isFromNotification() {
        return getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_NOTIFICATION, false);
    }

}
