package com.kosbrother.mongmongwoo.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.OldVersionProduct;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductUtil {

    public static List<Product> getNewProductListFromOldProductString(String oldProductJsonString) {
        List<Product> productList = new ArrayList<>();
        Gson gson = new Gson();
        Type typeToken = new TypeToken<List<OldVersionProduct>>() {
        }.getType();
        List<OldVersionProduct> oldVersionProductList = gson.fromJson(oldProductJsonString, typeToken);

        for (OldVersionProduct oldProduct : oldVersionProductList) {
            Product product = new Product(
                    oldProduct.getId(),
                    oldProduct.getName(),
                    oldProduct.getPrice(),
                    oldProduct.getCover());
            product.setBuy_count(oldProduct.getBuyCount());

            OldVersionProduct.SelectedSpec selectedSpec = oldProduct.getSelectedSpec();
            if (selectedSpec != null) {
                Spec spec = new Spec(selectedSpec.getId(),
                        selectedSpec.getStyle(),
                        new Spec.StylePic(selectedSpec.getPic()));
                product.setSelectedSpec(spec);
            }

            productList.add(product);
        }
        return productList;
    }
}
