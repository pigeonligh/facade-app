package com.pigeonligh.facade.data.types;

import android.content.Context;

import com.pigeonligh.facade.common.list.BaseListItem;

import org.json.JSONException;
import org.json.JSONObject;

public class DataFavoriteItem extends DataNamedItem {
    private String name;
    private String sourceName;
    private String path;

    public DataFavoriteItem() {
        //
    }

    public DataFavoriteItem(JSONObject obj) {
        try {
            name = obj.getString("name");
            sourceName = obj.getString("source");
            path = obj.getString("path");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getPath() {
        return path;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("source", sourceName);
            obj.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String toJSONString() {
        return toJSON().toString();
    }

    public abstract static class Viewer extends BaseListItem {
        protected DataFavoriteItem data;

        public Viewer(DataFavoriteItem data, int layoutId, Object content) {
            super(layoutId, content);
            this.data = data;
        }

        public DataFavoriteItem getData() {
            return data;
        }
    }

    public abstract static class ViewFactory<V extends DataFavoriteItem.Viewer> {
        public abstract V create(DataFavoriteItem item);

        public abstract Context getContext();
    }
}
