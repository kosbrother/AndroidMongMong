package com.kosbrother.mongmongwoo.entity.pastorder;

public class PastOrder extends PastOrderEntity {

    public String getIdText() {
        return "訂單編號：" + getId();
    }

    public String getItemsPriceText() {
        return "NT$ " + super.getItemsPrice();
    }

    public String getShipFeeText() {
        int shipFee = getShipFee();
        if (shipFee == 0) {
            return "免運費";
        }
        return "NT$ " + shipFee;
    }

    public String getTotalText() {
        return "NT$ " + super.getTotal();
    }

    @Override
    public String getStatus() {
        String status = super.getStatus();
        if (status.equals("新訂單")) {
            status = "訂單成立";
        }
        return status;
    }

}
