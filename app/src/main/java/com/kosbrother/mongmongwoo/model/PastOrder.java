package com.kosbrother.mongmongwoo.model;

import com.kosbrother.mongmongwoo.entity.PastOrderEntity;

public class PastOrder extends PastOrderEntity {

    @Override
    public String getStatus() {
        String status = super.getStatus();
        if (status.equals("新訂單")) {
            status = "訂單成立";
        }
        return status;
    }
}
