package com.pigeonligh.facade.components.view;

import android.view.View;
import android.widget.TextView;

import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseListItem;
import com.pigeonligh.facade.data.types.DataRefItem;

public class ViewRefListItem extends DataRefItem.Viewer {
    public ViewRefListItem(DataRefItem item, Object content) {
        super(item, R.layout.component_view_ref_list_item, content);
    }

    @Override
    protected ViewRefListItem.ViewHolder setupViewHolder(View view) {
        return new ViewRefListItem.ViewHolder(view);
    }

    private class ViewHolder extends BaseListItem.ViewHolder {
        TextView titleView;

        public ViewHolder(View view) {
            titleView = view.findViewById(R.id.view_reflistitem_title);
            view.setTag(this);
        }

        @Override
        public void show() {
            titleView.setText(data.getTitle());
        }
    }
}
