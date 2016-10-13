package com.kosbrother.mongmongwoo.common;

import com.kosbrother.mongmongwoo.entity.checkout.OrderPrice;
import com.kosbrother.mongmongwoo.entity.checkout.ShoppingPoint;
import com.kosbrother.mongmongwoo.entity.pastorder.PastOrder;

public class OrderPriceViewModel {

    private final String itemsPriceText;
    private final String shipFeeText;
    private final String totalText;
    private final boolean useShoppingPoints;
    private final String shoppingPointsAmountText;
    private final String shoppingPointsSubtotalText;

    public OrderPriceViewModel(OrderPrice orderPrice) {
        itemsPriceText = "NT$" + orderPrice.getOriginItemsPrice();
        shipFeeText = "NT$" + orderPrice.getShipFee();
        totalText = "NT$" + orderPrice.getTotal();

        ShoppingPoint shoppingPoint = orderPrice.getShoppingPoint();
        useShoppingPoints = shoppingPoint.getUsedAmount() > 0;
        shoppingPointsAmountText = "-NT$" + shoppingPoint.getUsedAmount();
        shoppingPointsSubtotalText = "NT$" + shoppingPoint.getReducedItemsPrice();
    }

    public OrderPriceViewModel(PastOrder pastOrder) {
        itemsPriceText = pastOrder.getItemsPriceText();
        shipFeeText = pastOrder.getShipFeeText();
        totalText = pastOrder.getTotalText();

        int shoppingPointsAmount = pastOrder.getShoppingPointAmount();
        useShoppingPoints = shoppingPointsAmount > 0;
        shoppingPointsAmountText = pastOrder.getShoppingPointAmountText();
        shoppingPointsSubtotalText = pastOrder.getShoppingPointSubTotalText();
    }

    public String getItemsPriceText() {
        return itemsPriceText;
    }

    public String getShipFeeText() {
        return shipFeeText;
    }

    public String getTotalText() {
        return totalText;
    }

    public boolean isUseShoppingPoints() {
        return useShoppingPoints;
    }

    public String getShoppingPointsAmountText() {
        return shoppingPointsAmountText;
    }

    public String getShoppingPointsSubtotalText() {
        return shoppingPointsSubtotalText;
    }
}
