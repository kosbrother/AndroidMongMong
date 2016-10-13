package com.kosbrother.mongmongwoo.common;

import com.kosbrother.mongmongwoo.entity.checkout.Campaign;
import com.kosbrother.mongmongwoo.entity.pastorder.PastItem;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.util.List;

public class ProductViewModel {

    private Product product;
    private int productPosition;

    private String specImageUrl;
    private String nameText;
    private boolean showCampaign;
    private String campaignText;
    private String selectedSpecText;
    private boolean isDiscount = false;
    private String quantityText;
    private String originalPriceText;
    private String finalPriceText;
    private String subTotalText;

    public ProductViewModel(Product product, int productPosition) {
        this.product = product;
        this.productPosition = productPosition;
        Spec selectedSpec = product.getSelectedSpec();
        specImageUrl = selectedSpec.getStylePic().getUrl();
        nameText = product.getName();
        List<Campaign> campaigns = product.getCampaigns();
        showCampaign = campaigns != null && campaigns.size() > 0;
        if (showCampaign) {
            Campaign campaign = campaigns.get(0);
            campaignText = campaign.getTitle();
            isDiscount = campaign.isApplied();
        }
        selectedSpecText = selectedSpec.getStyle();
        quantityText = "數量：" + product.getQuantity();
        originalPriceText = "NT$" + product.getPrice();
        finalPriceText = "NT$" + product.getFinalPrice() + "X" + product.getQuantity();
        subTotalText = "小計：" + product.getSubtotal();
    }

    public ProductViewModel(PastItem pastItem) {
        specImageUrl = pastItem.getStylePic();
        nameText = pastItem.getName();
        List<Campaign> campaigns = pastItem.getCampaigns();
        showCampaign = campaigns != null && campaigns.size() > 0;
        if (showCampaign) {
            Campaign campaign = campaigns.get(0);
            campaignText = campaign.getTitle();
            isDiscount = campaign.isApplied();
        }
        selectedSpecText = pastItem.getStyle();
        quantityText = "數量：" + pastItem.getQuantity();
        finalPriceText = "NT$" + pastItem.getPrice() + "X" + pastItem.getQuantity();
        subTotalText = "小計：" + pastItem.getSubtotal();
    }

    public String getSpecImageUrl() {
        return specImageUrl;
    }

    public String getNameText() {
        return nameText;
    }

    public boolean isShowCampaign() {
        return showCampaign;
    }

    public String getCampaignText() {
        return campaignText;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public String getSelectedSpecText() {
        return selectedSpecText;
    }

    public String getQuantityText() {
        return quantityText;
    }

    public String getOriginalPriceText() {
        return originalPriceText;
    }

    public String getFinalPriceText() {
        return finalPriceText;
    }

    public String getSubTotalText() {
        return subTotalText;
    }

    public int getQuantity() {
        return product.getQuantity();
    }

    public int getProductPosition() {
        return productPosition;
    }
}
