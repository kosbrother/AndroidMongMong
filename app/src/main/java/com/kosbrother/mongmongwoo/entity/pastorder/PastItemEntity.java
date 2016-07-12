package com.kosbrother.mongmongwoo.entity.pastorder;

import com.google.gson.annotations.SerializedName;

class PastItemEntity {
    @SerializedName("name")
    private String name;
    @SerializedName("style")
    private String style;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private int price;
    @SerializedName("style_pic")
    private String stylePic;

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

    public String getStylePic() {
        return stylePic;
    }
}
