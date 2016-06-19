package com.kosbrother.mongmongwoo.model;

import com.kosbrother.mongmongwoo.entity.CategoryEntity;

public class Category extends CategoryEntity {

    public String getImageUrl() {
        return image.getUrl();
    }

    public enum Type {
        ALL("所有商品", 10),
        NEW("新品上架", 11),
        STATIONERY("文具用品", 12),
        JAPAN_KOREA("日韓精選", 13),
        BIRTHDAY("生日專區", 14),
        LIFE("生活小物", 16);

        private final String name;
        private final int id;

        Type(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public enum SortName {
        popular("人氣排行"), date("最新商品"), price_desc("價格高到低"), price_asc("價格低到高");

        private final String title;

        SortName(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
