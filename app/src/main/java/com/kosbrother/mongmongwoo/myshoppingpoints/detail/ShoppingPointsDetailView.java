package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import java.util.List;

public interface ShoppingPointsDetailView {
    void setTotalTextView(String totalText);

    void setRecyclerViewEmpty();

    void setRecyclerView(List<ShoppingPointsViewModel> viewModels);
}
