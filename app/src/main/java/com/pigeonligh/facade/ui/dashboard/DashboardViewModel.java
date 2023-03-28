package com.pigeonligh.facade.ui.dashboard;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.pigeonligh.facade.components.source.SourceListItem;
import com.pigeonligh.facade.data.adapter.DataSourceAdapter;
import com.pigeonligh.facade.data.store.DataSourceStore;
import com.pigeonligh.facade.data.store.DataStore;
import com.pigeonligh.facade.data.types.DataNamedList;
import com.pigeonligh.facade.data.types.DataSourceItem;

public class DashboardViewModel extends ViewModel {
    private final DataSourceItem.ViewFactory sourceItemViewFactory;
    private final DataSourceStore sourceStore;
    private final DataSourceAdapter adapter;

    public DashboardViewModel(Context context) {
        sourceStore = DataStore.getSourceStore();
        sourceItemViewFactory = new DataSourceItem.ViewFactory() {
            @Override
            public SourceListItem create(DataSourceItem item) {
                return new SourceListItem(item, getContext());
            }

            @Override
            public Context getContext() {
                return context;
            }
        };
        adapter = new DataSourceAdapter(sourceItemViewFactory, sourceStore.getList());
    }

    public void saveData() {
        sourceStore.saveData();
    }

    public DataNamedList<DataSourceItem> getSourceList() {
        return sourceStore.getList();
    }

    public DataSourceAdapter getAdapter() {
        return adapter;
    }
}