package com.kosbrother.mongmongwoo.entity.pastorder;

import com.kosbrother.mongmongwoo.common.ProductViewModel;

public class PastItem extends PastItemEntity {

    public ProductViewModel getProductViewModel() {
        return new ProductViewModel(this);
    }
}
