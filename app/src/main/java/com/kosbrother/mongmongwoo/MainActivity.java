package com.kosbrother.mongmongwoo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.widget.LoginButton;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.kosbrother.mongmongwoo.adpters.CategoryAdapter;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.api.UrlCenter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.appindex.AppIndexManager;
import com.kosbrother.mongmongwoo.category.CategoryActivity;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.facebook.FbLoginActivity;
import com.kosbrother.mongmongwoo.fivestars.FiveStarsActivity;
import com.kosbrother.mongmongwoo.fivestars.FiveStartsManager;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.cart.CartClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer.NavigationDrawerClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer.NavigationDrawerOpenEvent;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.User;
import com.kosbrother.mongmongwoo.mycollect.MyCollectActivity;
import com.kosbrother.mongmongwoo.pastorders.PastOrderActivity;
import com.kosbrother.mongmongwoo.pastorders.QueryPastOrdersActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.ExpandableHeightGridView;
import com.kosbrother.mongmongwoo.utils.InteractiveNestedScrollView;
import com.kosbrother.mongmongwoo.utils.MongMongWooUtil;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.utils.ShareUtil;
import com.kosbrother.mongmongwoo.utils.VersionUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.functions.Action1;

public class MainActivity extends FbLoginActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircularImageView userImage;
    private TextView userText;
    private LoginButton loginButton;

    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppIndexManager.init(this);
        csBottomSheetDialogFragment = new CsBottomSheetDialogFragment();

        setMorePopularViewWithScrollView();
        setToolbarAndDrawer();
        setNavigationView();
        setLoginButton(loginButton);
        getCategories();
        getAllPopularItems();
        getNewDateItems();
        checkAndroidVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppIndexManager.startMainAppIndex();
    }

    @Override
    protected void onStop() {
        AppIndexManager.stopMainAppIndex();
        super.onStop();
    }

    @Override
    public void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
        setUserLoinView(user_name, picUrl);
    }

    @Override
    public void onFbLogout() {
        setUserLogoutView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (FiveStartsManager.getInstance(this).showFiveStarsRecommend()) {
                startActivity(new Intent(this, FiveStarsActivity.class));
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        setNoNetLayout();

        User user = Settings.getSavedUser();
        String userName = user.getUserName();

        if (userName != null && !userName.isEmpty()) {
            setUserLoinView(userName, user.getFb_pic());
        } else {
            setUserLogoutView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem shoppingCartItem = menu.findItem(R.id.shopping_cart);
        View shoppingCartView = MenuItemCompat.getActionView(shoppingCartItem);
        shoppingCartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CartClickEvent());

                Intent shoppingCarIntent = new Intent(MainActivity.this, ShoppingCarActivity.class);
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
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        GAManager.sendEvent(new NavigationDrawerClickEvent(item.getTitle().toString()));

        int id = item.getItemId();
        if (id == R.id.nav_orders) {
            if (Settings.checkIsLogIn()) {
                if (NetworkUtil.getConnectivityStatus(MainActivity.this) != 0) {
                    Intent intent = new Intent(MainActivity.this, PastOrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                }
            } else {
                startActivity(new Intent(this, QueryPastOrdersActivity.class));
            }
        } else if (id == R.id.nav_service) {
            startActivity(new Intent(this, ServiceActivity.class));
        } else if (id == R.id.nav_collect) {
            startActivity(new Intent(this, MyCollectActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_share) {
            String title = "分享萌萌屋";
            String subject = "萌萌屋 - 走在青年流行前線";
            String text = UrlCenter.GOOGLE_PLAY_SHARE;
            ShareUtil.shareText(this, title, subject, text);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("UnusedParameters")
    public void onCustomerServiceFabClick(View view) {
        csBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
        GAManager.sendEvent(new CustomerServiceClickEvent("FAB"));
    }

    public void onMoreLatestItemsClick(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
        intent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());
        intent.putExtra(CategoryActivity.EXTRA_INT_SORT_INDEX, Category.SortName.date.ordinal());
        startActivity(intent);
    }

    public void onMorePopularItemsClick(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
        intent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());
        intent.putExtra(CategoryActivity.EXTRA_INT_SORT_INDEX, Category.SortName.popular.ordinal());
        startActivity(intent);
    }

    private void setMorePopularViewWithScrollView() {
        getMorePopularView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMorePopularItemsClick(v);
            }
        });
        InteractiveNestedScrollView scrollView = (InteractiveNestedScrollView) findViewById(R.id.scrollView);
        scrollView.setOnBottomReachedListener(new InteractiveNestedScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                final View morePopularView = getMorePopularView();
                ObjectAnimator animY = ObjectAnimator.ofFloat(
                        morePopularView, "y", -morePopularView.getHeight(), 0);
                animY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        morePopularView.setVisibility(View.VISIBLE);
                    }
                });
                animY.start();
            }

            @Override
            public void onBottomLeft() {
                final View morePopularView = getMorePopularView();
                ObjectAnimator animY = ObjectAnimator.ofFloat(
                        morePopularView, "y", 0, -morePopularView.getHeight());
                animY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        morePopularView.setVisibility(View.GONE);
                    }
                });
                animY.start();
            }

        });
    }

    private void getCategories() {
        Webservice.getCategories(new Action1<ResponseEntity<List<Category>>>() {
            @Override
            public void call(ResponseEntity<List<Category>> listResponseEntity) {
                List<Category> data = listResponseEntity.getData();
                if (data == null) {
                    GAManager.sendError("getCategoriesError", listResponseEntity.getError());
                } else {
                    setCategoryGridView(data);
                }
            }
        });
    }

    private void getAllPopularItems() {
        Webservice.getCategorySortItems(
                Category.Type.ALL.getId(), Category.SortName.popular.name(), 1, new Action1<List<Product>>() {
                    @Override
                    public void call(List<Product> products) {
                        if (products != null) {
                            setPopularItemsGridView(products);
                        }
                    }
                });
    }

    private void getNewDateItems() {
        Webservice.getCategorySortItems(
                Category.Type.NEW.getId(), Category.SortName.date.name(), 1, new Action1<List<Product>>() {
                    @Override
                    public void call(List<Product> products) {
                        if (products != null) {
                            setLatestItemsGridView(products);
                        }
                    }
                });
    }

    private void checkAndroidVersion() {
        Webservice.getAndroidVersion(new Action1<AndroidVersionEntity>() {
            @Override
            public void call(AndroidVersionEntity version) {
                onGetVersionResult(version);
            }
        });
    }

    private void onGetVersionResult(AndroidVersionEntity version) {
        if (version == null) {
            return;
        }
        boolean upToDate = VersionUtil.isVersionUpToDate(version.getVersionCode());
        String version_name = version.getVersionName();
        Settings.saveAndroidVersion(version_name, upToDate);

        NavigationView navigationView = getNavigationView();
        Menu navigationViewMenu = navigationView.getMenu();
        View lastVersionActionView = navigationViewMenu.findItem(R.id.nav_about).getActionView();

        if (upToDate) {
            lastVersionActionView.setVisibility(View.INVISIBLE);
            Settings.resetNotUpdateTimes();
        } else {
            lastVersionActionView.setVisibility(View.VISIBLE);
            if (VersionUtil.remindUpdate()) {
                showUpdateDialog(version_name, version.getUpdateMessage());
                Settings.resetNotUpdateTimes();
            }
            Settings.addNotUpdateTimes();
        }
    }

    private void setToolbarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setText("萌萌屋");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                GAManager.sendEvent(new NavigationDrawerOpenEvent());
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigationView() {
        NavigationView navigationView = getNavigationView();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userImage = (CircularImageView) header.findViewById(R.id.user_imageview);
        userText = (TextView) header.findViewById(R.id.user_name_text);
        loginButton = (LoginButton) header.findViewById(R.id.login_button_main);
    }

    private void setCategoryGridView(List<Category> data) {
        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.category_gv);
        gridView.setAdapter(new CategoryAdapter(this, data));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra(CategoryActivity.EXTRA_INT_CATEGORY_ID, category.getId());
                intent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, category.getName());
                startActivity(intent);
            }
        });
    }

    private void setLatestItemsGridView(List<Product> products) {
        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.latest_items_gv);
        List<Product> displayProducts = getDisplayProducts(products);
        setDisplayGridView(displayProducts, gridView);
    }

    private void setPopularItemsGridView(List<Product> products) {
        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.popular_items_gv);
        List<Product> displayProducts = getDisplayProducts(products);
        setDisplayGridView(displayProducts, gridView);
    }

    private void setDisplayGridView(final List<Product> displayProducts, ExpandableHeightGridView gridView) {
        GoodsGridAdapter adapter = new GoodsGridAdapter(this, displayProducts,
                new GoodsGridAdapter.GoodsGridAdapterListener() {
                    @Override
                    public void onAddShoppingCartButtonClick(int productId, int position) {
                        showProductStyleDialog(displayProducts.get(position));
                    }
                });
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
                intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());
                startActivity(intent);
            }
        });
    }

    private List<Product> getDisplayProducts(List<Product> products) {
        final List<Product> displayProducts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            displayProducts.add(products.get(i));
        }
        return displayProducts;
    }

    private void showProductStyleDialog(Product product) {
        new ProductStyleDialog(MainActivity.this, product, new ProductStyleDialog.ProductStyleDialogListener() {
            @Override
            public void onFirstAddShoppingCart() {
                ViewStub viewStub = (ViewStub) findViewById(R.id.shopping_car_spotlight_vs);
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

            @Override
            public void onConfirmButtonClick(Product product) {
                ShoppingCartManager.getInstance().addShoppingItem(product);
                invalidateOptionsMenu();
                Toast.makeText(MainActivity.this, "成功加入購物車", Toast.LENGTH_SHORT).show();
            }
        }).showWithInitState();
    }

    private void setNoNetLayout() {
        LinearLayout no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);
        if (NetworkUtil.getConnectivityStatus(this) == 0) {
            no_net_layout.setVisibility(View.VISIBLE);
        } else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    private void setUserLoinView(String user_name, String picUrl) {
        loginButton.setVisibility(View.GONE);
        userText.setText(user_name);
        Glide.with(this)
                .load(picUrl)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.icon_head)
                .into(userImage);

        MenuItem ordersMenuItem = getNavigationView().getMenu().findItem(R.id.nav_orders);
        ordersMenuItem.setTitle("我的訂單");
    }

    private void setUserLogoutView() {
        userImage.setImageResource(R.drawable.icon_head);
        userText.setText("");
        loginButton.setVisibility(View.VISIBLE);

        MenuItem ordersMenuItem = getNavigationView().getMenu().findItem(R.id.nav_orders);
        ordersMenuItem.setTitle("查詢訂單");
    }

    private NavigationView getNavigationView() {
        return (NavigationView) findViewById(R.id.nav_view);
    }

    private View getMorePopularView() {
        return findViewById(R.id.more_popular_rl);
    }

    @SuppressLint("InflateParams")
    private void showUpdateDialog(String version_name, String update_message) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_version, null);
        ((TextView) dialogView.findViewById(R.id.version_name_tv)).setText(version_name);
        ((TextView) dialogView.findViewById(R.id.update_msg_tv)).setText(update_message);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();

        dialogView.findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MongMongWooUtil.startToGooglePlayPage(MainActivity.this);
                dialog.dismiss();
            }
        });
    }

}
