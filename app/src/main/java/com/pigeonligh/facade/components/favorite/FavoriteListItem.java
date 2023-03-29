package com.pigeonligh.facade.components.favorite;

import android.view.View;
import android.widget.TextView;

import com.pigeonligh.facade.R;
import com.pigeonligh.facade.common.list.BaseListItem;
import com.pigeonligh.facade.data.types.DataFavoriteItem;

public class FavoriteListItem extends DataFavoriteItem.Viewer {
    public FavoriteListItem(DataFavoriteItem item, Object content) {
        super(item, R.layout.component_favorite_list_item, content);
    }

    @Override
    protected FavoriteListItem.ViewHolder setupViewHolder(View view) {
        return new FavoriteListItem.ViewHolder(view);
    }

    private class ViewHolder extends BaseListItem.ViewHolder {
        TextView titleView;

        public ViewHolder(View view) {
            titleView = view.findViewById(R.id.favoritelistitem_title);
            view.setTag(this);
        }

        @Override
        public void show() {
            titleView.setText(data.getName());
        }
    }
}
