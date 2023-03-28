package com.pigeonligh.facade.data.types;

import android.content.Context;

import com.pigeonligh.facade.common.list.BaseListItem;

public class DataRefItem {
    private final String title;
    private final String path;

    public DataRefItem(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public DataRefItem(DataRefItem src) {
        this.title = src.title;
        this.path = src.path;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public abstract static class Viewer extends BaseListItem {
        protected DataRefItem data;

        public Viewer(DataRefItem data, int layoutId, Object content) {
            super(layoutId, content);
            this.data = data;
        }

        public DataRefItem getData() {
            return data;
        }


    }

    public abstract static class ViewFactory<V extends DataRefItem.Viewer> {
        public abstract V create(DataRefItem item);

        public abstract Context getContext();
    }
}
