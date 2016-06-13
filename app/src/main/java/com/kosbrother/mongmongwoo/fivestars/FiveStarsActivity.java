package com.kosbrother.mongmongwoo.fivestars;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.utils.MongMongWooUtil;

public class FiveStarsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_stars);
    }

    public void onGiveFiveStarsButtonClick(View view) {
        FiveStartsManager.getInstance(this).stared();
        MongMongWooUtil.startToGooglePlayPage(this);
        finish();
    }
}
