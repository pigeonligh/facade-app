package com.pigeonligh.facade.common.list;

import android.view.View;

public abstract class BaseListItem {
    private final int layoutId;
    private final Object content;
    private ViewHolder viewHolder;
    private View view;

    public BaseListItem(int layoutId, Object content) {
        this.layoutId = layoutId;
        this.view = null;
        this.viewHolder = null;
        this.content = content;
    }

    public View getView() {
        return view;
    }

    public Object getContent() {
        return content;
    }

    public int getLayoutId() {
        return this.layoutId;
    }

    public void setup(View view) {
        this.view = view;
        this.viewHolder = setupViewHolder(view);
    }

    public ViewHolder getViewHolder() {
        return this.viewHolder;
    }

    protected abstract ViewHolder setupViewHolder(View view);

    public abstract class ViewHolder {
        public abstract void show();
    }


}