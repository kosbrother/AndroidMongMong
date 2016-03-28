package com.jasonko.mongmongwoo.model;

/**
 * Created by kolichung on 3/23/16.
 */
public class PastOrderProduct {

    public PastOrderProduct(String name, String style, int quantity, int price) {
        this.name = name;
        this.style = style;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    String name;
    String style;
    int quantity;
    int price;



}
