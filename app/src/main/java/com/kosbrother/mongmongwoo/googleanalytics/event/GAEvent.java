package com.kosbrother.mongmongwoo.googleanalytics.event;

public interface GAEvent {
    String getCategory();

    String getAction();

    String getLabel();

    long getValue();
}
