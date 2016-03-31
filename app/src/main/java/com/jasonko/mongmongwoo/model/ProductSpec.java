package com.jasonko.mongmongwoo.model;

import java.io.Serializable;

/**
 * Created by kolichung on 3/22/16.
 */
public class ProductSpec implements Serializable {

    public ProductSpec(int spec_id, String style, int amount, String pic_url) {
        this.spec_id = spec_id;
        this.style = style;
        this.amount = amount;
        this.pic_url = pic_url;
    }

    public int getSpec_id() {
        return spec_id;
    }

    public String getStyle() {
        return style;
    }

    public int getAmount() {
        return amount;
    }

    public String getPic_url() {
        return pic_url;
    }

    int spec_id;
    String style;
    int amount;
    String pic_url;

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    String product_name;


}
