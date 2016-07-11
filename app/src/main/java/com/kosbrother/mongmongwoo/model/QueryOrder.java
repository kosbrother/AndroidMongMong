package com.kosbrother.mongmongwoo.model;

import com.kosbrother.mongmongwoo.entity.QueryOrderEntity;

public class QueryOrder extends QueryOrderEntity {

    @Override
    public String getStatus() {
        String status = super.getStatus();
        if (status.equals("新訂單")) {
            status = "訂單成立";
        }
        return status;
    }
}
