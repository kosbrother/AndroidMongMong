package com.jasonko.mongmongwoo.model;

import java.io.Serializable;

/**
 * Created by kolichung on 3/14/16.
 */
public class Store  implements Serializable {

    int store_id;
    String store_code;
    String name;
    String address;

    public Store(int store_id, String store_code, String name, String address) {
        this.store_id = store_id;
        this.store_code = store_code;
        this.name = name;
        this.address = address;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getStore_code() {
        return store_code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
