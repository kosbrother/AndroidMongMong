package com.kosbrother.mongmongwoo.entity.postorder;

public class PostOrder extends PostOrderEntity {

    @Override
    public String getStatus() {
        String status = super.getStatus();
        if (status.equals("新訂單")) {
            status = "訂單成立";
        }
        return status;
    }
}
