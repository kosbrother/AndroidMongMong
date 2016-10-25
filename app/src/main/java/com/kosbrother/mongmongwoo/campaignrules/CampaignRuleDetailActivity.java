package com.kosbrother.mongmongwoo.campaignrules;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.category.ProductsAdapter;
import com.kosbrother.mongmongwoo.databinding.ActivityCampaignRuleDetailBinding;
import com.kosbrother.mongmongwoo.entity.camapign.CampaignRuleDetailEntity;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.product.ProductActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import java.util.List;

public class CampaignRuleDetailActivity extends BaseActivity implements
        DataManager.ApiCallBack, ProductsAdapter.GoodsGridAdapterListener {

    public static final String EXTRA_STRING_TITLE = "EXTRA_STRING_TITLE";
    public static final String EXTRA_INT_ID = "EXTRA_INT_ID";

    private ActivityCampaignRuleDetailBinding binding;
    private List<Product> products;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_campaign_rule_detail);
        setToolbar(getIntent().getStringExtra(EXTRA_STRING_TITLE));

        binding.setShowLoading(true);
        DataManager.getInstance().getCampaignRule(getIntent().getIntExtra(EXTRA_INT_ID, 0), this);
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onError(String errorMessage) {
        binding.setShowLoading(false);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        CampaignRuleDetailEntity result = (CampaignRuleDetailEntity) data;
        products = result.getItems();

        binding.setShowLoading(false);
        binding.setBannerUrl(result.getImage().getUrl());

        RecyclerView recyclerView = (RecyclerView) binding.getRoot().findViewById(R.id.activity_campaign_rule_detail_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ProductsAdapter(products, this));
    }

    @Override
    public void onAddShoppingCartButtonClick(int productId, int position) {
        showProductStyleDialog(products.get(position));
    }

    @Override
    public void onGoodsItemClick(int position) {
        if (products != null) {
            Product product = products.get(position);
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
            intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
            intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());

            startActivity(intent);
        }
    }

    private void showProductStyleDialog(Product product) {
        new ProductStyleDialog(this, product, new ProductStyleDialog.ProductStyleDialogListener() {
            @Override
            public void onFirstAddShoppingCart() {
                // ignore
            }

            @Override
            public void onConfirmButtonClick(Product product) {
                ShoppingCartManager.getInstance().addShoppingItem(product);
                Toast.makeText(CampaignRuleDetailActivity.this, "成功加入購物車", Toast.LENGTH_SHORT).show();
            }
        }).showWithInitState();
    }
}
