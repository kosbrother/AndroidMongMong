package com.kosbrother.mongmongwoo.entity.pastorder;

import com.kosbrother.mongmongwoo.common.DeliveryUserInfoViewModel;
import com.kosbrother.mongmongwoo.common.OrderPriceViewModel;
import com.kosbrother.mongmongwoo.model.ShipType;

public class PastOrder extends PastOrderEntity {

    public String getIdText() {
        return "訂單編號：" + getId();
    }

    public String getItemsPriceText() {
        return "NT$" + getItemsPrice();
    }

    public String getShipFeeText() {
        return "NT$" + getShipFee();
    }

    public String getTotalText() {
        return "NT$" + getTotal();
    }

    @Override
    public String getStatus() {
        String status = super.getStatus();
        if (status.equals("新訂單")) {
            status = "訂單成立";
        }
        return status;
    }

    public boolean isCancelable() {
        if (getShipType().equals(ShipType.homeByCreditCard.getShipType())) {
            return false;
        }
        return getStatus().equals("訂單成立");
    }

    public String getShoppingPointAmountText() {
        return "-NT$" + getShoppingPointAmount();
    }

    public String getShoppingPointSubTotalText() {
        int subtotal = getItemsPrice() - getShoppingPointAmount();
        return "NT$" + subtotal;
    }

    public DeliveryUserInfoViewModel getDeliveryUserInfoViewModel(String shipName, String shipPhone, String shipEmail) {
        return new DeliveryUserInfoViewModel(getInfo(), getShipType(), shipName, shipPhone, shipEmail);
    }

    public OrderPriceViewModel getOrderPrice() {
        return new OrderPriceViewModel(this);
    }
}
