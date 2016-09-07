package com.kosbrother.mongmongwoo.myshoppingpoints;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsCampaignsEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;
import com.kosbrother.mongmongwoo.myshoppingpoints.campaign.ShoppingPointsCampaignFragment;
import com.kosbrother.mongmongwoo.myshoppingpoints.detail.ShoppingPointsDetailFragment;
import com.kosbrother.mongmongwoo.shopinfo.ShopInfoActivity;

import java.io.Serializable;
import java.util.List;

public class MyShoppingPointsActivity extends BaseActivity {

    public static final String EXTRA_BOOLEAN_CAMPAIGN_PAGE = "EXTRA_BOOLEAN_CAMPAIGN_PAGE";

    private List<ShoppingPointsCampaignsEntity> shoppingPointsCampaignsEntities;
    private ShoppingPointsDetailEntity shoppingPointsDetailEntity;

    private DataManager.ApiCallBack getShoppingPointsInfoCallBack = new DataManager.ApiCallBack() {

        @Override
        public void onError(String errorMessage) {
            showToast(errorMessage);
        }

        @Override
        public void onSuccess(Object data) {
            shoppingPointsDetailEntity = (ShoppingPointsDetailEntity) data;
            if (DataManager.getInstance().isAllTasksDone()) {
                onAllTasksDone();
            }
        }
    };

    private DataManager.ApiCallBack getShoppingPointsCampaignCallBack = new DataManager.ApiCallBack() {

        @Override
        public void onError(String errorMessage) {
            showToast(errorMessage);
        }

        @Override
        public void onSuccess(Object data) {
            shoppingPointsCampaignsEntities = (List<ShoppingPointsCampaignsEntity>) data;
            if (DataManager.getInstance().isAllTasksDone()) {
                onAllTasksDone();
            }
        }
    };

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();

        getShoppingPointsDetailAndCampaignData();
    }

    private void getShoppingPointsDetailAndCampaignData() {
        int userId = Settings.getSavedUser().getUserId();
        DataManager.getInstance().getShoppingPointsInfo(userId, getShoppingPointsInfoCallBack);
        DataManager.getInstance().getShoppingPointsCampaign(userId, getShoppingPointsCampaignCallBack);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_my_shopping_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.activity_my_shopping_points_shopping_info) {
            startActivity(new Intent(this, ShopInfoActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void onAllTasksDone() {
        setContentView(R.layout.activity_my_shopping_points);
        setToolbar();

        setTabLayoutWithViewPager();
    }

    private void setTabLayoutWithViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_my_shopping_points_vp);
        MyShoppingPointsPagerAdapter adapter =
                new MyShoppingPointsPagerAdapter(getSupportFragmentManager(),
                        shoppingPointsDetailEntity, shoppingPointsCampaignsEntities);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_my_shopping_points_tl);
        tabLayout.setupWithViewPager(viewPager);

        if (getIntent().getBooleanExtra(EXTRA_BOOLEAN_CAMPAIGN_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }
    }

    private void showToast(String errorMessage) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static class MyShoppingPointsPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] titles = new String[]{"購物金明細", "購物金活動"};
        private ShoppingPointsDetailEntity shoppingPointsDetailEntity;
        private List<ShoppingPointsCampaignsEntity> shoppingPointsCampaignsEntities;

        public MyShoppingPointsPagerAdapter(
                FragmentManager fm,
                ShoppingPointsDetailEntity shoppingPointsDetailEntity,
                List<ShoppingPointsCampaignsEntity> shoppingPointsCampaignsEntities) {
            super(fm);
            this.shoppingPointsDetailEntity = shoppingPointsDetailEntity;
            this.shoppingPointsCampaignsEntities = shoppingPointsCampaignsEntities;
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                ShoppingPointsDetailFragment shoppingPointsDetailFragment =
                        new ShoppingPointsDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable(ShoppingPointsDetailFragment.ARG_SHOPPING_POINTS_INFO,
                        shoppingPointsDetailEntity);
                shoppingPointsDetailFragment.setArguments(args);
                return shoppingPointsDetailFragment;
            } else {
                ShoppingPointsCampaignFragment shoppingPointsCampaignFragment =
                        new ShoppingPointsCampaignFragment();
                Bundle args = new Bundle();
                args.putSerializable(ShoppingPointsCampaignFragment.ARG_SHOPPING_POINTS_CAMPAIGNS,
                        (Serializable) shoppingPointsCampaignsEntities);
                shoppingPointsCampaignFragment.setArguments(args);
                return shoppingPointsCampaignFragment;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }
}
