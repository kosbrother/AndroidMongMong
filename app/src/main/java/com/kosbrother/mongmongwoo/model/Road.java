package com.kosbrother.mongmongwoo.model;

/**
 * Created by kolichung on 3/14/16.
 */
public class Road {

    int road_id;
    String name;

    public Road(int road_id, String name) {
        this.road_id = road_id;
        this.name = name;
    }

    public int getRoad_id() {
        return road_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
