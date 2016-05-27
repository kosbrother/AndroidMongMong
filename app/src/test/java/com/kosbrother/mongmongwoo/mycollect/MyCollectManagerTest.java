package com.kosbrother.mongmongwoo.mycollect;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyCollectManagerTest {

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception {
        gson = null;
    }

    @Test
    public void testRemoveNullFromList() throws Exception {
        List<Product> productList = new ArrayList<>();
        productList.add(null);
        productList.add(null);
        productList.add(new Product(0, "", 0, ""));
        String myCollectedJsonString = gson.toJson(productList);

        Type listType = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> resultList = gson.fromJson(myCollectedJsonString, listType);
        MyCollectManager.removeAllNullFromList(resultList);

        Assert.assertEquals(1, resultList.size());
    }

}