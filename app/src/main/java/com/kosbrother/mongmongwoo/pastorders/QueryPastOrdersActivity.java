package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;

public class QueryPastOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_past_orders);
        setToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressWarnings("ConstantConditions")
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedParameters"})
    public void onSubmitQueryButtonClick(View view) {
        String emailString = ((EditText) findViewById(R.id.email_et)).getText().toString().trim();
        String phoneString = ((EditText) findViewById(R.id.phone_et)).getText().toString().trim();
        if (emailString.isEmpty() || phoneString.isEmpty()) {
            Toast.makeText(this, "Email跟電話不可空白", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, QueryPastOrdersResultActivity.class);
        intent.putExtra(QueryPastOrdersResultActivity.EXTRA_STRING_EMAIL, emailString);
        intent.putExtra(QueryPastOrdersResultActivity.EXTRA_STRING_PHONE, phoneString);
        startActivity(intent);
    }

}
