package com.pigeonligh.facade.data.types;

import android.content.Context;

import com.pigeonligh.facade.common.Utils;
import com.pigeonligh.facade.common.list.BaseListItem;

import org.json.JSONException;
import org.json.JSONObject;

public class DataSourceItem extends DataNamedItem {
    private String name;
    private String url;

    public DataSourceItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public DataSourceItem(JSONObject obj) {
        try {
            name = obj.getString("name");
            url = obj.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean Validate(DataSourceItem item) {
        if (item.name.length() == 0) {
            return false;
        }
        return Utils.ValidateURL(item.url);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String toJSONString() {
        return toJSON().toString();
    }

    public abstract static class Viewer extends BaseListItem {
        protected DataSourceItem data;

        public Viewer(DataSourceItem data, int layoutId, Object content) {
            super(layoutId, content);
            this.data = data;
        }

        public DataSourceItem getData() {
            return data;
        }

        public void setData(DataSourceItem data) {
            this.data = data;
        }
    }

    public abstract static class ViewFactory<V extends Viewer> {
        public abstract V create(DataSourceItem item);

        public abstract Context getContext();
    }
}
