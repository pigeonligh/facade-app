package com.pigeonligh.facade.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pigeonligh.facade.data.types.DataNamedList;
import com.pigeonligh.facade.data.types.DataSourceItem;

import java.util.HashMap;
import java.util.Map;

public class DataSourceAdapter<F extends DataSourceItem.ViewFactory> extends BaseAdapter {
    private final F factory;
    private final DataNamedList<DataSourceItem> list;
    private final Map<DataSourceItem, DataSourceItem.Viewer> viewers;

    public DataSourceAdapter(F f, DataNamedList<DataSourceItem> list) {
        this.list = list;
        this.viewers = new HashMap<>();
        this.factory = f;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DataSourceItem item = list.get(i);
        if (!viewers.containsKey(item)) {
            viewers.put(item, this.factory.create(item));
        }
        DataSourceItem.Viewer v = viewers.get(item);
        if (v.getView() == null) {
            view = LayoutInflater.from(factory.getContext()).inflate(v.getLayoutId(), viewGroup, false);
            v.setup(view);
        }
        v.getViewHolder().show();
        return v.getView();
    }
}
