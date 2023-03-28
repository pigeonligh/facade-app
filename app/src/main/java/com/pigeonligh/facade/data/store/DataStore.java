package com.pigeonligh.facade.data.store;

import android.content.Context;

public class DataStore {
    private static final DataStore instance = new DataStore();
    private Context context;
    private DataSourceStore sourceStore;
    private DataFavoriteStore favoriteStore;

    private DataStore() {
        //
    }

    public static DataStore getInstance() {
        return instance;
    }

    public static void initContext(Context context) {
        if (instance.context == null) {
            instance.context = context;
        }
    }

    public static DataFavoriteStore getFavoriteStore() {
        return instance.internalGetFavoriteStore();
    }

    public static DataSourceStore getSourceStore() {
        return instance.internalGetSourceStore();
    }

    private DataFavoriteStore internalGetFavoriteStore() {
        if (favoriteStore == null) {
            favoriteStore = new DataFavoriteStore(context);
        }
        return favoriteStore;
    }

    private DataSourceStore internalGetSourceStore() {
        if (sourceStore == null) {
            sourceStore = new DataSourceStore(context);
        }
        return sourceStore;
    }
}
