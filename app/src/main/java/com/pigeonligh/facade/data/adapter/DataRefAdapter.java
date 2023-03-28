package com.pigeonligh.facade.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pigeonligh.facade.data.types.DataRefItem;

import java.util.ArrayList;
import java.util.List;

public class DataRefAdapter<F extends DataRefItem.ViewFactory> extends BaseAdapter {
    private final F factory;
    private final ArrayList<DataRefItem.Viewer> data;

    public DataRefAdapter(F f) {
        this.data = new ArrayList<>();
        this.factory = f;
    }

    @Override
    public int getCount() {
        return data.size();
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
        DataRefItem.Viewer v = data.get(i);
        if (v.getView() == null) {
            view = LayoutInflater.from(factory.getContext()).inflate(v.getLayoutId(), viewGroup, false);
            v.setup(view);
        }
        v.getViewHolder().show();
        return v.getView();
    }

    public void setData(List<DataRefItem> list) {
        this.data.clear();
        for (int i = 0; i < list.size(); i++) {
            this.data.add(factory.create(list.get(i)));
        }

        notifyDataSetChanged();
    }

    public DataRefItem get(int i) {
        return data.get(i).getData();
    }
}
