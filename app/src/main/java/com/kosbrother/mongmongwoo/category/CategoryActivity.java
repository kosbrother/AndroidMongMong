package com.kosbrother.mongmongwoo.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.cart.CartClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.category.CategoryEnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.indexgridcart.IndexGridCartAddToCartEvent;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.product.ProductActivity;
import com.kosbrother.mongmongwoo.search.SearchActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class CategoryActivity extends BaseActivity {

    public static final String EXTRA_INT_CATEGORY_ID = "EXTRA_INT_CATEGORY_ID";
    public static final String EXTRA_STRING_CATEGORY_NAME = "EXTRA_STRING_CATEGORY_NAME";
    public static final String EXTRA_INT_SORT_INDEX = "EXTRA_INT_SORT_INDEX";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        String categoryName = intent.getStringExtra(EXTRA_STRING_CATEGORY_NAME);
        int categoryId = intent.getIntExtra(EXTRA_INT_CATEGORY_ID, 10);
        int sortIndex = intent.getIntExtra(EXTRA_INT_SORT_INDEX, 0);

        setToolbar(categoryName);
        setViewPagerWithTabLayout(categoryName, categoryId, sortIndex);
        GAManager.sendEvent(new CategoryEnterEvent(categoryName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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

                Intent shoppingCarIntent = new Intent(CategoryActivity.this, ShoppingCarActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewPagerWithTabLayout(String categoryName, int categoryId, int sortIndex) {
        FilterPagerAdapter mFilterPagerAdapter = new FilterPagerAdapter(
                getSupportFragmentManager(), categoryId, categoryName);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFilterPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(sortIndex);
    }

    public class FilterPagerAdapter extends FragmentStatePagerAdapter {

        private final Category.SortName[] sortNames = Category.SortName.values();
        private final int categoryId;
        private final String categoryName;

        public FilterPagerAdapter(FragmentManager fm, int categoryId, String categoryName) {
            super(fm);
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        @Override
        public Fragment getItem(int i) {
            ProductsGridFragment productsGridFragment = new ProductsGridFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ProductsGridFragment.ARG_INT_CATEGORY_ID, categoryId);
            bundle.putString(ProductsGridFragment.ARG_STRING_CATEGORY_NAME, categoryName);
            bundle.putString(ProductsGridFragment.ARG_STRING_SORT_NAME, sortNames[i].name());
            productsGridFragment.setArguments(bundle);
            return productsGridFragment;
        }

        @Override
        public int getCount() {
            return sortNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sortNames[position].getTitle();
        }
    }

    public static class ProductsGridFragment extends Fragment
            implements GoodsGridAdapter.GoodsGridAdapterListener {

        public static final String ARG_INT_CATEGORY_ID = "ARG_INT_CATEGORY_ID";
        public static final String ARG_STRING_SORT_NAME = "ARG_STRING_SORT_NAME";
        public static final String ARG_STRING_CATEGORY_NAME = "ARG_STRING_CATEGORY_NAME";

        private int categoryId = 10;
        private String sortName;
        private String categoryName;

        private GridView mGridView;
        private GoodsGridAdapter goodsGridAdapter;
        private List<Product> products = new ArrayList<>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
            categoryId = arguments.getInt(ARG_INT_CATEGORY_ID);
            sortName = arguments.getString(ARG_STRING_SORT_NAME);
            categoryName = arguments.getString(ARG_STRING_CATEGORY_NAME);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_items_gird, container, false);
            mGridView = (GridView) rootView.findViewById(R.id.products_gv);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (products != null) {
                        Product product = products.get(position);
                        Intent intent = new Intent(getContext(), ProductActivity.class);
                        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                        intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, categoryId);
                        intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);

                        startActivity(intent);
                    }
                }
            });
            mGridView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getCategorySortItems(page);
                }
            });
            goodsGridAdapter = new GoodsGridAdapter(getContext(), products, this);
            mGridView.setAdapter(goodsGridAdapter);
            getCategorySortItems(1);
            return rootView;
        }

        @Override
        public void onAddShoppingCartButtonClick(int productId, int position) {
            Product product = products.get(position);
            GAManager.sendEvent(new IndexGridCartAddToCartEvent(product.getName()));
            new ProductStyleDialog(getContext(), product, new ProductStyleDialog.ProductStyleDialogListener() {
                @Override
                public void onFirstAddShoppingCart() {
                    ViewStub viewStub = (ViewStub) getActivity().findViewById(R.id.shopping_car_spotlight_vs);
                    if (viewStub != null) {
                        final View spotLightShoppingCarLayout = viewStub.inflate();
                        spotLightShoppingCarLayout.setPadding(
                                0, (int) DensityApi.convertDpToPixel(56, getContext()), 0, 0);
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
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getContext(), "成功加入購物車", Toast.LENGTH_SHORT).show();
                }
            }).showWithInitState();
        }

        private void getCategorySortItems(int page) {
            Webservice.getCategorySortItems(categoryId, sortName, page, new Action1<List<Product>>() {
                @Override
                public void call(List<Product> products) {
                    if (products != null) {
                        ProductsGridFragment.this.products.addAll(products);
                        goodsGridAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
