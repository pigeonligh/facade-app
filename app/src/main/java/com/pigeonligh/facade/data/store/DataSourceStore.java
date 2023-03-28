package com.pigeonligh.facade.data.store;

import android.content.Context;
import android.util.Log;

import com.pigeonligh.facade.common.Utils;
import com.pigeonligh.facade.data.types.DataNamedList;
import com.pigeonligh.facade.data.types.DataSourceItem;

import java.io.File;

public class DataSourceStore {
    private static final String TAG = "datastore.source";

    private static final String FILENAME = "source.json";
    private final File file;
    private DataNamedList<DataSourceItem> list;

    public DataSourceStore(Context context) {
        file = new File(context.getFilesDir(), FILENAME);
        loadData();
    }

    public void loadData() {
        if (!file.exists() || !file.canRead()) {
            list = new DataNamedList<>(DataSourceItem.class);
            return;
        }

        String data = Utils.ReadFile(file, "[]");
        list = new DataNamedList<>(data, DataSourceItem.class);


        Log.d(TAG, String.format("load %d items", list.size()));
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, String.format("loadData: %s", list.get(i).getName()));
        }
    }

    public void saveData() {
        String data = list.toJSON();
        Utils.WriteFile(file, data);
    }

    public DataNamedList<DataSourceItem> getList() {
        return list;
    }
}
