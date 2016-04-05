package com.kosbrother.mongmongwoo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.kosbrother.mongmongwoo.adpters.StyleGridAdapter;
import com.kosbrother.mongmongwoo.api.ProductApi;
import com.kosbrother.mongmongwoo.fragments.ProductImageFragment;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ProductSpec;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

/**
 * Created by kolichung on 3/17/16.
 */
public class ProductActivity extends AppCompatActivity {

    TextView nameText;
    TextView priceText;
    TextView loadingText;

    Button addCarButton;
    TextView infoText;
    private ViewPager viewPager;
    private PageControl pageControl;
//    SampleFragmentPagerAdapter adapter;

    Product theProduct;
    private StyleGridAdapter styleGridAdapter;
    MenuItem menuItem;

    RelativeLayout spotLightShoppingCarLayout;
    Button spotLightConfirmButton;

    LinearLayout no_net_layout;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Intent theIntent = getIntent();
        Bundle bundle = theIntent.getExtras();
        theProduct = (Product) bundle.getSerializable("Selected_Product");

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
        priceText.setText("NT$ "+ Integer.toString(theProduct.getPrice()));
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (theProduct.getSpecs().size()>0) {
                    showStyleDialog();
                }else{
                    Toast.makeText(ProductActivity.this, "樣式讀取中,請稍受再加購物車", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new NewsTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuItem != null){
            ShoppingCarPreference pref = new ShoppingCarPreference();
            int count = pref.getShoppingCarItemSize(ProductActivity.this);
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.icon_shopping_car_2));
        }

        if (NetworkUtil.getConnectivityStatus(this) == 0){
            no_net_layout.setVisibility(View.VISIBLE);
        }else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(99)==null) {
            menuItem = menu.add(0, 99, 0, "購物車");
        }else {
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

        switch (id){
            case android.R.id.home:
                finish();
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

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (theProduct.getImages().size() == 0) {
                return 1;
            }else {
                return theProduct.getImages().size();
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (theProduct.getImages().size() == 0){
                return ProductImageFragment.newInstance(theProduct.getPic_url());
            }else {
                return ProductImageFragment.newInstance(theProduct.getImages().get(position).getUrl());
            }
        }

    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            theProduct = ProductApi.updateProductById(theProduct.getId(),theProduct);
            if (theProduct != null){
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            loadingText.setVisibility(View.GONE);
            if (result != null) {
                SampleFragmentPagerAdapter adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(adapter);
                pageControl.setViewPager(viewPager);
//                adapter.notifyDataSetChanged();
//                pageControl.setViewPager(viewPager);
                infoText.setText(Html.fromHtml(theProduct.getDescription()));
            }else{
                Toast.makeText(ProductActivity.this, "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private GridView styleGrid;
    private ImageView styleImage;
    private TextView styleName;
    private int tempCount;

    public void showStyleDialog(){

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
                if (tempCount != 1){
                    tempCount = tempCount -1;
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

                if (Settings.checkIsFirstAddShoppingCar(ProductActivity.this)){
                    showShoppingCarInstruction();
                    Settings.setKownShoppingCar(ProductActivity.this);
                }
            }
        });

        styleGridAdapter = new StyleGridAdapter(this,theProduct.getSpecs(), styleImage,styleName);
        styleGrid.setAdapter(styleGridAdapter);

        Glide.with(this)
                .load(theProduct.getSpecs().get(0).getPic_url())
                .centerCrop()
                .placeholder(R.drawable.icon_head)
                .crossFade()
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

    public void showShoppingCarInstruction(){
        spotLightShoppingCarLayout.setVisibility(View.VISIBLE);
    }

}
