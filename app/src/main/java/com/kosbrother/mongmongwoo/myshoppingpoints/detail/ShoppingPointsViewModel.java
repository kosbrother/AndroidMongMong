package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointRecordsEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsEntity;
import com.kosbrother.mongmongwoo.utils.DateFormatUtil;

import java.util.ArrayList;
import java.util.List;

public class ShoppingPointsViewModel {
    private ShoppingPointsEntity entity;

    public ShoppingPointsViewModel(ShoppingPointsEntity entity) {
        this.entity = entity;
    }

    public boolean isValid() {
        return entity.getValid();
    }

    public String getPointTypeText() {
        return entity.getPointType();
    }

    public String getValidUntilText() {
        String validUntil = entity.getValidUntil();
        if (validUntil == null || validUntil.isEmpty()) {
            return "";
        } else {
            return "到期日：" + DateFormatUtil.parseToYearMonthDay(validUntil);
        }
    }

    public String getDescriptionText() {
        return entity.getDescription();
    }

    public List<ShoppingPointsRecordsViewModel> getShoppingPointsRecordsViewModels() {
        List<ShoppingPointsRecordsViewModel> list = new ArrayList<>();
        List<ShoppingPointRecordsEntity> records = entity.getShoppingPointRecords();
        for (ShoppingPointRecordsEntity entity : records) {
            list.add(new ShoppingPointsRecordsViewModel(entity));
        }
        return list;
    }

    public String getAmountText() {
        return "$" + entity.getAmount();
    }
}
