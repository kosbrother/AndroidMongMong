package com.kosbrother.mongmongwoo.mynotification;

import com.kosbrother.mongmongwoo.entity.MyNotification.MyNotificationEntity;
import com.kosbrother.mongmongwoo.utils.DateFormatUtil;

import java.io.Serializable;

public class MyNotification extends MyNotificationEntity {

    private boolean isNew = true;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public NotificationDetail getNotificationDetail() {
        return new NotificationDetail(getTitle(), getContent(), getDateAndTime());
    }

    public String getMonthAndDay() {
        return DateFormatUtil.parseToMonthAndDay(getCreatedAt());
    }

    public String getDateAndTime() {
        return DateFormatUtil.parseToDateAndTime(getCreatedAt());
    }

    public static class NotificationDetail implements Serializable {
        private String title;
        private String content;
        private String dateAndTime;

        public NotificationDetail(String title, String content, String dateAndTime) {
            this.title = title;
            this.content = content;
            this.dateAndTime = dateAndTime;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getDateAndTime() {
            return dateAndTime;
        }
    }
}
