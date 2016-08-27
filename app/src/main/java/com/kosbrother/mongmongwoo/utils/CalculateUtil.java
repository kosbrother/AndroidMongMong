package com.kosbrother.mongmongwoo.utils;

import com.kosbrother.mongmongwoo.model.Product;

import java.io.Serializable;
import java.util.List;

public class CalculateUtil {

    public static final int SHIP_FEE = 90;
    private static final int FREE_SHIP_REQUIRED_PRICE = 490;

    public static OrderPrice calculateOrderPrice(List<Product> shoppingCarProducts, int shoppingPointsAmount) {
        int itemsPrice = calculateItemsPrice(shoppingCarProducts);
        int shoppingPointsSubTotal;
        if (itemsPrice > shoppingPointsAmount) {
            shoppingPointsSubTotal = itemsPrice - shoppingPointsAmount;
        } else {
            shoppingPointsSubTotal = 0;
            shoppingPointsAmount = itemsPrice;
        }
        int shippingPrice = shoppingPointsSubTotal >= FREE_SHIP_REQUIRED_PRICE ? 0 : SHIP_FEE;

        int freeShippingPriceRemain = FREE_SHIP_REQUIRED_PRICE - shoppingPointsSubTotal;
        if (freeShippingPriceRemain < 0) {
            freeShippingPriceRemain = 0;
        }

        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setItemsPrice(itemsPrice);
        orderPrice.setShoppingPointsSubTotal(shoppingPointsSubTotal);
        orderPrice.setShoppingPointsAmount(shoppingPointsAmount);
        orderPrice.setShipFee(shippingPrice);
        orderPrice.setFreeShippingRemain(freeShippingPriceRemain);
        orderPrice.setTotal(shoppingPointsSubTotal + shippingPrice);

        return orderPrice;
    }

    private static int calculateItemsPrice(List<Product> shoppingCarProducts) {
        int itemsPrice = 0;
        for (int i = 0; i < shoppingCarProducts.size(); i++) {
            Product product = shoppingCarProducts.get(i);
            itemsPrice += product.getFinalPrice() * product.getBuy_count();
        }
        return itemsPrice;
    }

    public static class OrderPrice implements Serializable {
        private int itemsPrice;
        private int shoppingPointsSubTotal;
        private int shoppingPointsAmount;
        private int shipFee;
        private int freeShippingRemain;
        private int total;

        public int getItemsPrice() {
            return itemsPrice;
        }

        public void setItemsPrice(int itemsPrice) {
            this.itemsPrice = itemsPrice;
        }

        public int getShoppingPointsSubTotal() {
            return shoppingPointsSubTotal;
        }

        public void setShoppingPointsSubTotal(int shoppingPointsSubTotal) {
            this.shoppingPointsSubTotal = shoppingPointsSubTotal;
        }

        public int getShoppingPointsAmount() {
            return shoppingPointsAmount;
        }

        public void setShoppingPointsAmount(int shoppingPointsAmount) {
            this.shoppingPointsAmount = shoppingPointsAmount;
        }

        public int getShipFee() {
            return shipFee;
        }

        public void setShipFee(int shipFee) {
            this.shipFee = shipFee;
        }

        public int getFreeShippingRemain() {
            return freeShippingRemain;
        }

        public void setFreeShippingRemain(int freeShippingRemain) {
            this.freeShippingRemain = freeShippingRemain;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
