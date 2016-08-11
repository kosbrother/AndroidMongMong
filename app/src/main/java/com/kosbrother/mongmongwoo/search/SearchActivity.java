package com.kosbrother.mongmongwoo.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.search.SearchSubmitQueryEvent;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.product.ProductActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;
import com.kosbrother.mongmongwoo.utils.KeyboardUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int QUERY_HISTORY_DISPLAY_NUM = 4;
    private static final int SUGGESTION_LIMIT = 50;

    private static final String SEARCH_RESULT_EMPTY_FORMAT =
            "哎呀！找不到有關「%s」" + "\n" +
                    "的相關商品呢！";

    // To prevent trigger initQuerySuggestion at onQueryTextChange and onFocusChange
    // when start search
    private boolean startSearch = false;

    private RecentQueryHelper queryHelper;

    private SearchView searchView;
    private List<String> suggestionList;
    private List<String> hotKeywordList;

    private String query;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryHelper = RecentQueryHelper.getInstance(getApplicationContext());
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        initSearchLayout();
        getSuggestionList();
        getHotKeywordList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    KeyboardUtil.show(SearchActivity.this, searchView);

                    String query = searchView.getQuery().toString();
                    if (!query.isEmpty() && !startSearch) {
                        initQuerySuggestionLayout(getDisplaySuggestionList(query));
                    }
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || TextUtils.getTrimmedLength(newText) == 0) {
                    initSearchLayout();
                    return true;
                }
                if (!startSearch) {
                    initQuerySuggestionLayout(getDisplaySuggestionList(newText));
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hot_keyword_tv) {
            TextView textView = (TextView) v;
            startSearch(textView.getText().toString());
        }
    }

    private void getSuggestionList() {
        Webservice.getSuggestions(new Action1<List<String>>() {
            @Override
            public void call(List<String> suggestionList) {
                SearchActivity.this.suggestionList = suggestionList;
            }
        });
    }

    private void getHotKeywordList() {
        Webservice.getHotKeywords(new Action1<List<String>>() {
            @Override
            public void call(List<String> hotKeywordList) {
                SearchActivity.this.hotKeywordList = hotKeywordList;
                setHotKeywordLayout();
            }
        });
    }

    private List<String> getDisplaySuggestionList(String query) {
        List<String> list = new ArrayList<>();
        if (suggestionList != null) {
            int length = suggestionList.size();
            for (int i = 0; i < length && list.size() < SUGGESTION_LIMIT; i++) {
                String product = suggestionList.get(i);
                if (product.contains(query)) {
                    list.add(product);
                }
            }
        }
        return list;
    }

    private void startSearch(final String query) {
        this.query = query;
        this.products = new ArrayList<>();
        queryHelper.saveRecentQuery(query);
        searchView.setQuery(query, false);
        GAManager.sendEvent(new SearchSubmitQueryEvent(query));
        KeyboardUtil.hide(this, searchView);

        setContentView(R.layout.loading_no_toolbar);
        getSearchResult(1);
    }

    private void getSearchResult(int page) {
        startSearch = true;
        Webservice.getSearchItems(query, page, new Action1<List<Product>>() {
            @Override
            public void call(final List<Product> products) {
                int searchResultSize = products.size();
                if (SearchActivity.this.products.size() == 0 && searchResultSize == 0) {
                    setContentView(R.layout.activity_search_result_empty);
                    setResultEmptyLayout(query);
                } else if (searchResultSize > 0) {
                    if (SearchActivity.this.products.size() == 0) {
                        SearchActivity.this.products.addAll(products);
                        setContentView(R.layout.activity_search_result);
                        setSearchResultLayout();
                    } else {
                        SearchActivity.this.products.addAll(products);
                        GridView gridView = (GridView) findViewById(R.id.search_result_gv);
                        assert gridView != null;
                        GoodsGridAdapter adapter = (GoodsGridAdapter) gridView.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
                searchView.clearFocus();
                startSearch = false;
            }
        });
    }

    private void setResultEmptyLayout(String query) {
        TextView textView = (TextView) findViewById(R.id.search_result_empty_tv);
        assert textView != null;
        textView.setText(String.format(SEARCH_RESULT_EMPTY_FORMAT, query));
    }

    private void setSearchResultLayout() {
        GoodsGridAdapter adapter = new GoodsGridAdapter(SearchActivity.this, products,
                new GoodsGridAdapter.GoodsGridAdapterListener() {
                    @Override
                    public void onAddShoppingCartButtonClick(int productId, int position) {
                        showProductStyleDialog(products.get(position));
                    }
                });
        GridView gridView = (GridView) findViewById(R.id.search_result_gv);
        assert gridView != null;
        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getSearchResult(page);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getAdapter().getItem(position);
                Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
                intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());
                intent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_SEARCH, true);
                startActivity(intent);
            }
        });
    }

    private void showProductStyleDialog(Product product) {
        new ProductStyleDialog(this, product, new ProductStyleDialog.ProductStyleDialogListener() {
            @Override
            public void onFirstAddShoppingCart() {
            }

            @Override
            public void onConfirmButtonClick(Product product) {
                ShoppingCartManager.getInstance().addShoppingItem(product);
                Toast.makeText(SearchActivity.this, "成功加入購物車", Toast.LENGTH_SHORT).show();
            }
        }).showWithInitState();
    }

    private void initSearchLayout() {
        List<String> queryStringList = queryHelper.getQueryStringList();

        setContentView(R.layout.activity_search);
        View queryHistoryCardView = findViewById(R.id.query_history_cv);
        assert queryHistoryCardView != null;
        queryHistoryCardView.setVisibility(
                queryStringList.size() == 0 ? View.GONE : View.VISIBLE);
        setQueryHistoryRecyclerView(getDisplayQueryHistory(queryStringList));
        setQueryHistoryBottomTextView();
        setHotKeywordLayout();
    }

    private void setQueryHistoryRecyclerView(List<String> queryStringList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.query_history_rv);
        assert recyclerView != null;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new QueryHistoryAdapter(queryStringList,
                new QueryHistoryAdapter.OnQueryHistoryTextViewListener() {
                    @Override
                    public void onQueryTextViewClick(String query) {
                        startSearch(query);
                    }

                    @Override
                    public void onQueryTextViewLongClick(final String query) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);
                        alertDialogBuilder.setTitle(query);
                        alertDialogBuilder.setMessage("要從搜尋紀錄中移除嗎？");
                        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialogBuilder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                queryHelper.removeQuery(query);
                                dialog.dismiss();
                                TextView queryHistoryBottomTextView =
                                        (TextView) findViewById(R.id.more_query_history_tv);
                                assert queryHistoryBottomTextView != null;
                                if (queryHistoryBottomTextView.getText().toString().equals("查看更多")) {
                                    setQueryHistoryRecyclerView(
                                            getDisplayQueryHistory(queryHelper.getQueryStringList()));
                                } else {
                                    setQueryHistoryRecyclerView(queryHelper.getQueryStringList());
                                }
                            }
                        });
                        alertDialogBuilder.show();
                    }
                }));
    }

    private void initQuerySuggestionLayout(List<String> suggestionList) {
        setContentView(R.layout.activity_search_suggestion);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.query_suggestion_rv);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuerySuggestionAdapter querySuggestionAdapter = new QuerySuggestionAdapter(suggestionList,
                new QuerySuggestionAdapter.OnQuerySuggestionTextViewListener() {
                    @Override
                    public void onQueryTextViewClick(String query) {
                        startSearch(query);
                    }
                });
        recyclerView.setAdapter(querySuggestionAdapter);
    }

    private void setQueryHistoryBottomTextView() {
        TextView queryHistoryBottomTextView = (TextView) findViewById(R.id.more_query_history_tv);
        assert queryHistoryBottomTextView != null;
        int size = queryHelper.getQueryStringList().size();
        if (size < 5) {
            queryHistoryBottomTextView.setText("清除搜尋紀錄");
        } else {
            queryHistoryBottomTextView.setText("查看更多");
        }
        queryHistoryBottomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().toString().equals("查看更多")) {
                    setQueryHistoryRecyclerView(queryHelper.getQueryStringList());
                    tv.setText("清除搜尋紀錄");
                    View view = SearchActivity.this.getCurrentFocus();
                    if (view != null) {
                        KeyboardUtil.hide(SearchActivity.this, view);
                    }
                } else if (tv.getText().toString().equals("清除搜尋紀錄")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);
                    alertDialogBuilder.setTitle("搜尋紀錄");
                    alertDialogBuilder.setMessage("要清除搜尋紀錄嗎？");
                    alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.setPositiveButton("清除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            queryHelper.clear();
                            dialog.dismiss();
                            initSearchLayout();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });
    }

    private void setHotKeywordLayout() {
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.hot_keyword_container_fl);
        if (flowLayout != null && hotKeywordList != null && hotKeywordList.size() > 0) {
            flowLayout.removeAllViews();
            for (String hotKeyword : hotKeywordList) {
                TextView hotKeywordItemView = (TextView) getLayoutInflater()
                        .inflate(R.layout.item_hot_keyword, null);
                hotKeywordItemView.setText(hotKeyword);
                hotKeywordItemView.setOnClickListener(this);
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                        FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                int margin = (int) getResources().getDimension(R.dimen.hot_keyword_margin);
                params.setMargins(0, 0, margin, margin);
                hotKeywordItemView.setLayoutParams(params);
                flowLayout.addView(hotKeywordItemView);
            }
        }
    }

    private List<String> getDisplayQueryHistory(List<String> queryStringList) {
        List<String> list = new ArrayList<>();
        int size = queryStringList.size();
        for (int i = 0; i < size && i < QUERY_HISTORY_DISPLAY_NUM; i++) {
            list.add(0, queryStringList.get(size - 1 - i));
        }
        return list;
    }

}
