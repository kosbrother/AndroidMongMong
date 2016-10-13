package com.kosbrother.mongmongwoo.checkout;

import com.kosbrother.mongmongwoo.entity.checkout.CheckoutResultEntity;
import com.kosbrother.mongmongwoo.entity.checkout.OrderPrice;
import com.kosbrother.mongmongwoo.entity.checkout.ShipCampaign;
import com.kosbrother.mongmongwoo.entity.checkout.ShoppingPoint;

public class Step1FragmentViewModel {
    private static final String SHIP_CAMPAIGN_FORMAT = "(%s，還差NT$%s)";

    private String originItemsPriceText;
    private boolean showShoppingPointView;
    private String spendableShoppingPointText;
    private boolean showReducedItemsPrice;
    private String reducedItemsPriceText;
    private String usedShoppingPointAmount;
    private String leftToApplyShipCampaignText;
    private String shipFeeText;
    private String totalText;
    private boolean showLoginConfirmButton;

    public Step1FragmentViewModel(CheckoutResultEntity checkoutResultEntity, boolean isLogin) {
        OrderPrice orderPrice = checkoutResultEntity.getOrderPrice();

        originItemsPriceText = "NT$" + orderPrice.getOriginItemsPrice();

        ShoppingPoint shoppingPoint = orderPrice.getShoppingPoint();
        int spendableShoppingPoint = shoppingPoint.getSpendableAmount();
        showShoppingPointView = spendableShoppingPoint > 0;
        spendableShoppingPointText = "折抵NT$" + spendableShoppingPoint;

        showReducedItemsPrice = shoppingPoint.getUsedAmount() > 0;
        usedShoppingPointAmount = "-NT$" + shoppingPoint.getUsedAmount();
        reducedItemsPriceText = "NT$" + shoppingPoint.getReducedItemsPrice();

        ShipCampaign shipCampaign = orderPrice.getShipCampaign();
        leftToApplyShipCampaignText = String.format(SHIP_CAMPAIGN_FORMAT, shipCampaign.getTitle(), shipCampaign.getLeftToApply());
        shipFeeText = "NT$" + orderPrice.getShipFee();

        totalText = "NT$" + orderPrice.getTotal();
        showLoginConfirmButton = isLogin;
    }

    public String getOriginItemsPriceText() {
        return originItemsPriceText;
    }

    public boolean isShowShoppingPointView() {
        return showShoppingPointView;
    }

    public String getSpendableShoppingPointText() {
        return spendableShoppingPointText;
    }

    public boolean isShowReducedItemsPrice() {
        return showReducedItemsPrice;
    }

    public String getReducedItemsPriceText() {
        return reducedItemsPriceText;
    }

    public String getUsedShoppingPointAmount() {
        return usedShoppingPointAmount;
    }

    public String getLeftToApplyShipCampaignText() {
        return leftToApplyShipCampaignText;
    }

    public String getShipFeeText() {
        return shipFeeText;
    }

    public String getTotalText() {
        return totalText;
    }

    public boolean isShowLoginConfirmButton() {
        return showLoginConfirmButton;
    }
}
