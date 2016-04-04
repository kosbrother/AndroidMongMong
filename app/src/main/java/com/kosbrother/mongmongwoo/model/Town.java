package com.kosbrother.mongmongwoo.model;

/**
 * Created by kolichung on 3/14/16.
 */
public class Town {

    int town_id;
    String name;

    public Town(int town_id, String name) {
        this.town_id = town_id;
        this.name = name;
    }

    public int getTown_id() {
        return town_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
