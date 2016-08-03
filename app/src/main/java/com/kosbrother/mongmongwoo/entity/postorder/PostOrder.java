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

    public String getIdText() {
        return "訂單編號：" + getId();
    }

    public String getTotalPriceText() {
        return "消費金額：NT$ " + getTotal();
    }

}
