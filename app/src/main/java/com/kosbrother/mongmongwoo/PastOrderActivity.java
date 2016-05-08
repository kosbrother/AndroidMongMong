package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.widget.LoginButton;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.kosbrother.mongmongwoo.adpters.PastOrdersGridAdapter;
import com.kosbrother.mongmongwoo.api.OrderApi;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.User;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by kolichung on 3/28/16.
 */
public class PastOrderActivity extends FbLoginActivity {

    User user;

    String TAG = "PastOrderActivity";
    LoginButton loginButton;
    Button fb;

    GridView mGridView;
    ArrayList<PastOrder> pastOrders = new ArrayList<>();
    int mPage = 1;
    PastOrdersGridAdapter pastOrdersAdapter;

    CircularImageView userImage;
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("我的訂單");

        userImage = (CircularImageView) findViewById(R.id.user_imageview);
        userNameText = (TextView) findViewById(R.id.user_name_text);
        user = Settings.getSavedUser(PastOrderActivity.this);
        Glide.with(PastOrderActivity.this)
                .load(user.getFb_pic())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.icon_head)
                .into(userImage);
        userNameText.setText(user.getUser_name());

        fb = (Button) findViewById(R.id.fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        setLoginButton(loginButton);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        mGridView = (GridView) findViewById(R.id.fragment_gridview);
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new NewsTask().execute();
            }
        });
        new NewsTask().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {

    }

    @Override
    protected void onFbLogout() {
        finish();
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<PastOrder> feedBackPastOrders = OrderApi.getOrdersByUid(user.getFb_uid(), mPage);
            if (feedBackPastOrders != null && feedBackPastOrders.size() > 0) {
                pastOrders.addAll(feedBackPastOrders);
                mPage = mPage + 1;
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                if (pastOrdersAdapter == null) {
                    pastOrdersAdapter = new PastOrdersGridAdapter(PastOrderActivity.this, pastOrders);
                    mGridView.setAdapter(pastOrdersAdapter);
                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(PastOrderActivity.this, PastOrderDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("THE_ORDER", pastOrders.get(position));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                } else {
                    pastOrdersAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
