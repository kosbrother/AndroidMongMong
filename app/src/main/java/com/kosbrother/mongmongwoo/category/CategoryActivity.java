package com.kosbrother.mongmongwoo.category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.api.DensityApi;
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
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.widget.RecyclerViewEndlessScrollListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.functions.Action1;

public class CategoryActivity extends BaseActivity implements DataManager.ApiCallBack {

    public static final String EXTRA_INT_CATEGORY_ID = "EXTRA_INT_CATEGORY_ID";
    public static final String EXTRA_STRING_CATEGORY_NAME = "EXTRA_STRING_CATEGORY_NAME";
    public static final String EXTRA_STRING_SORT_NAME = "EXTRA_STRING_SORT_NAME";
    public static final String EXTRA_BOOLEAN_FROM_INDEX_ACTIVITY = "EXTRA_BOOLEAN_FROM_INDEX_ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String categoryName = getIntent().getStringExtra(EXTRA_STRING_CATEGORY_NAME);
        int categoryId = getCategoryId();
        int sortIndex = getSortIndex();

        setToolbar(categoryName);
        setViewPagerWithTabLayout(categoryName, categoryId, sortIndex);
        getSubCategory(categoryName);
        GAManager.sendEvent(new CategoryEnterEvent(categoryName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_INDEX_ACTIVITY, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            super.onBackPressed();
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

    @Override
    public void onError(String errorMessage) {
        // do nothing
    }

    @Override
    public void onSuccess(Object data) {
        List<Category> data1 = (List<Category>) data;
        setSubCategoryRecyclerView(data1);
    }

    private int getCategoryId() {
        return getIntent().getIntExtra(EXTRA_INT_CATEGORY_ID, 10);
    }

    private int getSortIndex() {
        int sortIndex = 0;
        String sortNameString = getIntent().getStringExtra(EXTRA_STRING_SORT_NAME);
        if (sortNameString == null || sortNameString.isEmpty()) {
            return sortIndex;
        }
        Category.SortName sortName = Category.SortName.valueOf(sortNameString);
        if (sortName != null) {
            sortIndex = sortName.ordinal();
        }
        return sortIndex;
    }

    private void getSubCategory(String categoryName) {
        DataManager.getInstance().getSubCategories(categoryName, this);
    }

    private void setSubCategoryRecyclerView(final List<Category> data) {
        if (data.isEmpty()) {
            return;
        }
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_category_subcategory_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                int childCount = recyclerView.getChildCount();
                int dp0Dot5 = getResources().getDimensionPixelSize(R.dimen.dp_0dot5);
                for (int i = 0; i < childCount; i++) {
                    View childView = recyclerView.getChildAt(i);
                    int childPosition = i + 1;
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();

                    if (childPosition > 3 && childPosition % 3 != 0) {
                        params.setMargins(0, dp0Dot5, dp0Dot5, 0);
                    } else if (childPosition > 3 && childPosition % 3 == 0) {
                        params.setMargins(0, dp0Dot5, 0, 0);
                    } else if (childPosition % 3 != 0) {
                        params.setMargins(0, 0, dp0Dot5, 0);
                    }
                    childView.setLayoutParams(params);
                }

            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter subCategoryAdapter = new SubCategoryAdapter(data);
        recyclerView.setAdapter(subCategoryAdapter);

        View cardView = findViewById(R.id.activity_category_subcategory_cv);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        int dp8 = getResources().getDimensionPixelSize(R.dimen.dp_8);
        int topMargin = findViewById(R.id.toolbar).getMeasuredHeight() + dp8;
        params.setMargins(0, topMargin, 0, dp8);
        cardView.setLayoutParams(params);
        cardView.setVisibility(View.VISIBLE);
    }

    private void setViewPagerWithTabLayout(String categoryName, int categoryId, int sortIndex) {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        FilterPagerAdapter mFilterPagerAdapter = new FilterPagerAdapter(
                getSupportFragmentManager(), categoryId, categoryName);
        mViewPager.setAdapter(mFilterPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(sortIndex);
    }

    public class FilterPagerAdapter extends FragmentStatePagerAdapter {

        private final Category.SortName[] sortNames = Category.SortName.values();
        private final int categoryId;
        private final String categoryName;

        public FilterPagerAdapter(
                FragmentManager fm,
                int categoryId,
                String categoryName) {
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
            bundle.putString(ProductsGridFragment.ARG_STRING_TITLE, getPageTitle(i).toString());
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

    public static class ProductsGridFragment extends Fragment implements
            ProductsAdapter.GoodsGridAdapterListener, DataManager.ApiCallBack {

        public static final String ARG_INT_CATEGORY_ID = "ARG_INT_CATEGORY_ID";
        public static final String ARG_STRING_SORT_NAME = "ARG_STRING_SORT_NAME";
        public static final String ARG_STRING_CATEGORY_NAME = "ARG_STRING_CATEGORY_NAME";
        public static final String ARG_STRING_TITLE = "ARG_STRING_TITLE";

        private static final String CATEGORY_URI_STRING =
                "android-app://com.kosbrother.mongmongwoo/https/www.mmwooo.com/categories/%s?sort=%s";

        // create boolean for fetching data
        private boolean isVisibleToUser = false;
        private boolean isViewCreated = false;
        private GoogleApiClient mClient;

        private int categoryId = 10;
        private String sortName;
        private String categoryName;
        private String title;

        private RecyclerView recyclerView;
        private ProductsAdapter productsAdapter;
        private List<Product> products = new ArrayList<>();
        private View loadingView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
            categoryId = arguments.getInt(ARG_INT_CATEGORY_ID);
            sortName = arguments.getString(ARG_STRING_SORT_NAME);
            categoryName = arguments.getString(ARG_STRING_CATEGORY_NAME);
            title = arguments.getString(ARG_STRING_TITLE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(
                    R.layout.fragment_products_gird, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_products_grid_rv);

            productsAdapter = new ProductsAdapter(products, this);
            recyclerView.setAdapter(productsAdapter);

            loadingView = rootView.findViewById(R.id.loading_no_toolbar_fl);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            isViewCreated = true;
            lazyLoad();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                this.isVisibleToUser = true;
                lazyLoad();
            } else {
                this.isVisibleToUser = false;
                products.clear();
                if (recyclerView != null) {
                    recyclerView.clearOnScrollListeners();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    stopCategoryAppIndex();
                }
            }
        }

        private void lazyLoad() {
            if (isVisibleToUser && isViewCreated) {
                if (mClient == null) {
                    mClient = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
                }
                getInitData();
            }
        }

        @Override
        public void onDestroy() {
            DataManager.getInstance().unSubscribe(this);
            super.onDestroy();
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

        @Override
        public void onGoodsItemClick(int position) {
            if (products != null) {
                Product product = products.get(position);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, categoryId);
                intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);

                startActivity(intent);
            }
        }

        @Override
        public void onError(String errorMessage) {
            loadingView.setVisibility(View.GONE);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Object data) {
            loadingView.setVisibility(View.GONE);
            if (products.size() == 0) {
                // only index page one
                startCategoryAppIndex();
            }
            products.addAll((Collection<? extends Product>) data);
            productsAdapter.notifyDataSetChanged();
        }

        private void getInitData() {
            loadingView.setVisibility(View.VISIBLE);

            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerViewEndlessScrollListener(
                    layoutManager, new Action1<Integer>() {
                @Override
                public void call(Integer page) {
                    getCategorySortItems(page);
                }
            }));
            getCategorySortItems(1);
        }

        private void getCategorySortItems(int page) {
            DataManager.getInstance().getCategorySortItems(categoryName, sortName, page, this);
        }

        public void startCategoryAppIndex() {
            if (mClient != null) {
                mClient.connect();
                AppIndex.AppIndexApi.start(mClient, getCategoryAction());
            }
        }

        public void stopCategoryAppIndex() {
            if (mClient != null && mClient.isConnected()) {
                AppIndex.AppIndexApi.end(
                        mClient, getCategoryAction());
                mClient.disconnect();
                mClient = null;
            }
        }

        private Action getCategoryAction() {
            String urlString = String.format(CATEGORY_URI_STRING, categoryName, sortName);
            Thing object = new Thing.Builder()
                    .setName(categoryName + " - " + title)
                    .setDescription(categoryName + " - " + title)
                    .setUrl(Uri.parse(urlString))
                    .build();

            return new Action.Builder(Action.TYPE_VIEW)
                    .setObject(object)
                    .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                    .build();
        }
    }
}
