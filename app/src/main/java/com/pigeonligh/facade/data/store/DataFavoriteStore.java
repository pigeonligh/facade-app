package com.pigeonligh.facade.data.store;

import android.content.Context;

import com.pigeonligh.facade.common.Utils;
import com.pigeonligh.facade.data.types.DataFavoriteItem;
import com.pigeonligh.facade.data.types.DataNamedList;

import java.io.File;

public class DataFavoriteStore {
    private static final String FILENAME = "favorite.json";
    private final File file;
    private DataNamedList<DataFavoriteItem> list;

    public DataFavoriteStore(Context context) {
        file = new File(context.getFilesDir(), FILENAME);
        loadData();
    }

    public void loadData() {
        if (!file.exists() || !file.canRead()) {
            list = new DataNamedList<>(DataFavoriteItem.class);
            return;
        }

        String data = Utils.ReadFile(file, "[]");
        list = new DataNamedList<>(data, DataFavoriteItem.class);
    }

    public void saveData() {
        String data = list.toJSON();
        Utils.WriteFile(file, data);
    }

    public DataNamedList getList() {
        return list;
    }
}
