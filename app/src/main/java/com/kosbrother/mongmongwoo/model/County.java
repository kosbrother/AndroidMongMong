package com.kosbrother.mongmongwoo.model;

/**
 * Created by kolichung on 3/14/16.
 */
public class County {

    int county_id;
    String name;

    public County(int county_id, String name) {
        this.county_id = county_id;
        this.name = name;
    }

    public int getCounty_id() {
        return county_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
