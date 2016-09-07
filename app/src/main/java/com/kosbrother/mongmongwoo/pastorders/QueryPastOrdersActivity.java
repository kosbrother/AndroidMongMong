package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;

public class QueryPastOrdersActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_past_orders);
        setToolbar();
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
