package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

// For get required data from old version product saved in SharedPreferences
public class OldVersionProduct {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private int price;
    @SerializedName("cover")
    private String cover;
    @SerializedName("buy_count")
    private int buyCount;
    @SerializedName("selectedSpec")
    private SelectedSpec selectedSpec;

    public SelectedSpec getSelectedSpec() {
        return selectedSpec;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public String getCover() {
        return cover;
    }

    public class SelectedSpec {
        @SerializedName("style")
        private String style;

        @SerializedName("id")
        private int id;

        @SerializedName("pic")
        private String pic;

        public String getStyle() {
            return style;
        }

        public int getId() {
            return id;
        }

        public String getPic() {
            return pic;
        }
    }
}
