package com.kosbrother.mongmongwoo.entity.pastorder;

public class PastItem extends PastItemEntity {

    public String getPriceAndQuantityText() {
        return "NT$ " + getPrice() + " x " + getQuantity();
    }
}
