package com.kosbrother.mongmongwoo.googleanalytics.event.product;

import com.kosbrother.mongmongwoo.googleanalytics.action.GAAction;
import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class ProductSelectDialogConfirmEvent implements GAEvent {

    private final String label;

    public ProductSelectDialogConfirmEvent(String productName) {
        label = productName;
    }

    @Override
    public String getCategory() {
        return GACategory.PRODUCT_SELECT_DIALOG;
    }

    @Override
    public String getAction() {
        return GAAction.CONFIRM;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public long getValue() {
        return 0;
    }
}
