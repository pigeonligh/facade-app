package com.pigeonligh.facade.ui.favorite;

import android.content.Context;

import com.pigeonligh.facade.components.favorite.FavoriteListItem;
import com.pigeonligh.facade.data.adapter.DataFavoriteAdapter;
import com.pigeonligh.facade.data.store.DataFavoriteStore;
import com.pigeonligh.facade.data.store.DataStore;
import com.pigeonligh.facade.data.types.DataFavoriteItem;
import com.pigeonligh.facade.data.types.DataNamedList;

public class FavoriteViewModel {
    private final DataFavoriteItem.ViewFactory sourceItemViewFactory;
    private final DataFavoriteStore sourceStore;
    private final DataFavoriteAdapter adapter;

    public FavoriteViewModel(Context context) {
        sourceStore = DataStore.getFavoriteStore();
        sourceItemViewFactory = new DataFavoriteItem.ViewFactory() {
            @Override
            public FavoriteListItem create(DataFavoriteItem item) {
                return new FavoriteListItem(item, getContext());
            }

            @Override
            public Context getContext() {
                return context;
            }
        };
        adapter = new DataFavoriteAdapter(sourceItemViewFactory, sourceStore.getList());
    }

    public void saveData() {
        sourceStore.saveData();
    }

    public DataNamedList<DataFavoriteItem> getFavoriteList() {
        return sourceStore.getList();
    }

    public DataFavoriteAdapter getAdapter() {
        return adapter;
    }
}