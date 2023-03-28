package com.pigeonligh.facade.components.source;

import android.view.View;
import android.widget.TextView;

import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseListItem;
import com.pigeonligh.facade.data.types.DataSourceItem;

public class SourceListItem extends DataSourceItem.Viewer {
    public SourceListItem(DataSourceItem item, Object content) {
        super(item, R.layout.component_source_list_item, content);
    }

    @Override
    protected ViewHolder setupViewHolder(View view) {
        return new ViewHolder(view);
    }

    private class ViewHolder extends BaseListItem.ViewHolder {
        TextView titleView;

        public ViewHolder(View view) {
            titleView = view.findViewById(R.id.sourcelistitem_title);
            view.setTag(this);
        }

        @Override
        public void show() {
            titleView.setText(data.getName());
        }
    }
}
