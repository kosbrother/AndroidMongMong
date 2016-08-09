package com.kosbrother.mongmongwoo.entity.postorder;

import com.kosbrother.mongmongwoo.model.Spec;

public class UnableToBuyModel extends UnableToBuyEntity {
    public String getStockText() {
        Spec spec = getSpec();
        if (spec.getStatus().equals("off_shelf")) {
            return "已下架";
        } else {
            return "現貨數：" + spec.getStockAmount();
        }
    }

    public String getQuantityText() {
        return "選購數量：" + getQuantityToBuy();
    }

    public boolean isOffShelf() {
        return getSpec().getStatus().equals("off_shelf");
    }

    public int getSpecStockAmount() {
        Spec spec = getSpec();
        if (spec != null) {
            return spec.getStockAmount();
        } else {
            return 0;
        }
    }

    public int getSpecId() {
        Spec spec = getSpec();
        if (spec != null) {
            return spec.getId();
        } else {
            return 0;
        }
    }
}
