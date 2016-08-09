package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.utils.StringUtil;

import java.io.Serializable;

public class Spec implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("style")
    private String style;
    @SerializedName("style_pic")
    private StylePic stylePic;
    @SerializedName("stock_amount")
    private int stockAmount;
    @SerializedName("status")
    private String status;

    public Spec(int id, String style, StylePic stylePic) {
        this.id = id;
        this.style = style;
        this.stylePic = stylePic;
    }

    public int getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public StylePic getStylePic() {
        return stylePic;
    }

    public int getStockAmount() {
        return stockAmount;
    }

    public String getStockText() {
        if (stockAmount >= 10) {
            return "庫存充分";
        }
        return String.format("剩下%s件", StringUtil.transToFullWidth(String.valueOf(stockAmount)));
    }

    public String getCurrentStockText() {
        if (stockAmount >= 10) {
            return "現貨數：庫存充分";
        }
        return "現貨數：" + stockAmount;
    }

    public String getStatus() {
        return status;
    }

    public static class StylePic implements Serializable {
        @SerializedName("url")
        private String url;

        public StylePic(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
